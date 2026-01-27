package br.com.tigelah.merchantapi.application.usecase;

import br.com.tigelah.merchantapi.application.ports.AcquirerCoreClient;
import br.com.tigelah.merchantapi.domain.model.PaymentView;

public class AuthorizePaymentUseCase {
    private final AcquirerCoreClient client;

    public AuthorizePaymentUseCase(AcquirerCoreClient client) {
        this.client = client;
    }

    public PaymentView execute(AcquirerCoreClient.AuthorizeCommand cmd, String idempotencyKey, String correlationId) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key is required");
        }
        return client.authorize(cmd, idempotencyKey, correlationId);
    }
}
