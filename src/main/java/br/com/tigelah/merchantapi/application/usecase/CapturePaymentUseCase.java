package br.com.tigelah.merchantapi.application.usecase;

import br.com.tigelah.merchantapi.application.ports.AcquirerCoreClient;
import br.com.tigelah.merchantapi.domain.model.PaymentView;

import java.util.UUID;

public class CapturePaymentUseCase {
    private final AcquirerCoreClient client;

    public CapturePaymentUseCase(AcquirerCoreClient client) {
        this.client = client;
    }

    public PaymentView execute(UUID paymentId, String correlationId) {
        return client.capture(paymentId, correlationId);
    }
}
