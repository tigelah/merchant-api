package br.com.tigelah.merchantapi.application.usecase;

import br.com.tigelah.merchantapi.application.ports.AcquirerCoreClient;
import br.com.tigelah.merchantapi.domain.model.PaymentView;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizePaymentUseCaseTest {

    @Test
    void requires_idempotency_key() {
        var client = mock(AcquirerCoreClient.class);
        var uc = new AuthorizePaymentUseCase(client);

        var cmd = new AcquirerCoreClient.AuthorizeCommand("m1","o1",100,"BRL","4111","JOAO","12","2030","123");
        assertThrows(IllegalArgumentException.class, () -> uc.execute(cmd, "", "c1"));
    }

    @Test
    void delegates_to_client() {
        var client = mock(AcquirerCoreClient.class);
        var uc = new AuthorizePaymentUseCase(client);

        var pv = new PaymentView(UUID.randomUUID(),"AUTHORIZED","BRL",100,"1111","SIM", Instant.now());
        when(client.authorize(any(), anyString(), anyString())).thenReturn(pv);

        var cmd = new AcquirerCoreClient.AuthorizeCommand("m1","o1",100,"BRL","4111","JOAO","12","2030","123");
        var out = uc.execute(cmd, "k1", "c1");

        assertEquals(pv.paymentId(), out.paymentId());
        verify(client, times(1)).authorize(any(), eq("k1"), eq("c1"));
    }
}
