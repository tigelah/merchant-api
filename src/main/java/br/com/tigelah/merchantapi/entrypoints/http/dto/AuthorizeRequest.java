package br.com.tigelah.merchantapi.entrypoints.http.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record AuthorizeRequest(
        @NotBlank String merchantId,
        @NotBlank String orderId,
        @Positive long amountCents,
        @NotBlank String currency,
        @NotNull UUID accountId,
        String userId,
        @Valid Card card
) {
    public record Card(
            @NotBlank @Size(min = 12, max = 19) String pan,
            @NotBlank String holderName,
            @NotBlank @Pattern(regexp = "^(0[1-9]|1[0-2])$") String expMonth,
            @NotBlank @Pattern(regexp = "^[0-9]{4}$") String expYear,
            @NotBlank @Pattern(regexp = "^[0-9]{3,4}$") String cvv
    ) {}
}
