package br.com.tigelah.merchantapi.infrastructure.http;

import br.com.tigelah.merchantapi.application.ports.AcquirerCoreClient;
import br.com.tigelah.merchantapi.domain.model.PaymentView;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class AcquirerCoreHttpClientTest {

    @Test
    void authorize_calls_core_with_headers_and_new_fields() {
        var rest = new RestTemplate();
        var server = MockRestServiceServer.createServer(rest);
        var client = new AcquirerCoreHttpClient(rest, "http://core");

        var pid = UUID.randomUUID();
        var accountId = UUID.randomUUID();

        server.expect(requestTo("http://core/payments/authorize"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Idempotency-Key", "k1"))
                .andExpect(header("X-Correlation-Id", "c1"))
                // garante que o JSON enviado contém accountId/userId
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.installments").value(6))
                .andRespond(withSuccess(
                        "{"
                                + "\"paymentId\":\"" + pid + "\","
                                + "\"status\":\"AUTHORIZED\","
                                + "\"currency\":\"BRL\","
                                + "\"amountCents\":100,"
                                + "\"panLast4\":\"1111\","
                                + "\"authCode\":\"SIM\","
                                + "\"createdAt\":\"2030-01-01T00:00:00Z\","
                                + "\"accountId\":\"" + accountId + "\","
                                + "\"userId\":\"user-1\""
                                + "}",
                        MediaType.APPLICATION_JSON
                ));

        var cmd = new AcquirerCoreClient.AuthorizeCommand(
                "m1","o1",100,"BRL",
                "4111","JOAO","12","2030","123",
                accountId,
                "user-1",
                6
        );

        PaymentView out = client.authorize(cmd, "k1", "c1");

        assertEquals(pid, out.paymentId());
        assertEquals(accountId, out.accountId());
        assertEquals("user-1", out.userId());
        server.verify();
    }

    @Test
    void propagates_status_code() {
        var rest = new RestTemplate();
        var server = MockRestServiceServer.createServer(rest);
        var client = new AcquirerCoreHttpClient(rest, "http://core");

        var accountId = UUID.randomUUID();

        server.expect(requestTo("http://core/payments/authorize"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY).body("declined"));

        var cmd = new AcquirerCoreClient.AuthorizeCommand(
                "m1","o1",100,"BRL",
                "4111","JOAO","12","2030","123",
                accountId,
                "user-1",
                6
        );

        var ex = assertThrows(CoreHttpException.class, () -> client.authorize(cmd, "k1", "c1"));
        assertEquals(422, ex.statusCode());
        server.verify();
    }
}