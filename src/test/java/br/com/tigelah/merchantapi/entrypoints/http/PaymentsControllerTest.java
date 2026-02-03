package br.com.tigelah.merchantapi.entrypoints.http;

import br.com.tigelah.merchantapi.application.usecase.AuthorizePaymentUseCase;
import br.com.tigelah.merchantapi.application.usecase.CapturePaymentUseCase;
import br.com.tigelah.merchantapi.application.usecase.GetPaymentUseCase;
import br.com.tigelah.merchantapi.domain.model.PaymentView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentsController.class)
class PaymentsControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean AuthorizePaymentUseCase authorize;
    @MockitoBean CapturePaymentUseCase capture;
    @MockitoBean GetPaymentUseCase get;

    @Test
    void authorize_returns_200() throws Exception {
        var accountId = UUID.randomUUID();
        var pv = new PaymentView(
                UUID.randomUUID(),
                "AUTHORIZED",
                "BRL",
                100,
                "1111",
                "SIM",
                Instant.parse("2030-01-01T00:00:00Z"),
                accountId,
                "user-1"
        );
        when(authorize.execute(any(), eq("k1"), eq("c1"))).thenReturn(pv);

        mvc.perform(post("/v1/payments/authorize")
                        .header("Idempotency-Key","k1")
                        .header("X-Correlation-Id","c1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {
                                   "merchantId":"m1",
                                   "orderId":"o1",
                                   "amountCents":100,
                                   "currency":"BRL",
                                   "accountId":"%s",
                                   "userId":"user-1",
                                   "card":{
                                      "pan":"4111111111111111",
                                      "holderName":"Fulano",
                                      "expMonth":"01",
                                      "expYear":"2030",
                                      "cvv":"123"
                                   }
                                 }
                        """, accountId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").exists())
                .andExpect(jsonPath("$.status").value("AUTHORIZED"))
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.userId").value("user-1"));
    }

    @Test
    void authorize_missing_accountId_returns_400() throws Exception {
        mvc.perform(post("/v1/payments/authorize")
                        .header("Idempotency-Key","k1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                   "merchantId":"m1",
                                   "orderId":"o1",
                                   "amountCents":100,
                                   "currency":"BRL",
                                   "card":{
                                      "pan":"4111111111111111",
                                      "holderName":"Fulano",
                                      "expMonth":"01",
                                      "expYear":"2030",
                                      "cvv":"123"
                                   }
                                 }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_returns_200() throws Exception {
        var pv = new PaymentView(
                UUID.randomUUID(),
                "AUTHORIZED",
                "BRL",
                100,
                "1111",
                "SIM",
                Instant.parse("2030-01-01T00:00:00Z"),
                UUID.randomUUID(),
                "user-1"
        );
        when(get.execute(any(), any())).thenReturn(pv);

        mvc.perform(get("/v1/payments/" + pv.paymentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(pv.paymentId().toString()));
    }
}