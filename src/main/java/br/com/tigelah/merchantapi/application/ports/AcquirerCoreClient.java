package br.com.tigelah.merchantapi.application.ports;

import br.com.tigelah.merchantapi.domain.model.PaymentView;

import java.util.UUID;

public interface AcquirerCoreClient {
    PaymentView authorize(AuthorizeCommand cmd, String idempotencyKey, String correlationId);
    PaymentView capture(UUID paymentId, String correlationId);
    PaymentView get(UUID paymentId, String correlationId);

    record AuthorizeCommand(
            String merchantId,
            String orderId,
            long amountCents,
            String currency,
            String pan,
            String holderName,
            String expMonth,
            String expYear,
            String cvv,
            UUID accountId,
            String userId
    ) {}
}
