package br.com.tigelah.merchantapi.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PaymentView(
        UUID paymentId,
        String status,
        String currency,
        long amountCents,
        String panLast4,
        String authCode,
        Instant createdAt
) { }
