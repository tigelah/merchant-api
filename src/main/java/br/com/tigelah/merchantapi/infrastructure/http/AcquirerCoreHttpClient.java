package br.com.tigelah.merchantapi.infrastructure.http;

import br.com.tigelah.merchantapi.application.ports.AcquirerCoreClient;
import br.com.tigelah.merchantapi.domain.model.PaymentView;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

public class AcquirerCoreHttpClient implements AcquirerCoreClient {

    private final RestTemplate rest;
    private final String baseUrl;

    public AcquirerCoreHttpClient(RestTemplate rest, String baseUrl) {
        this.rest = rest;
        this.baseUrl = baseUrl;
    }

    @Override
    public PaymentView authorize(AuthorizeCommand cmd, String idempotencyKey, String correlationId) {
        var url = baseUrl + "/payments/authorize";

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Idempotency-Key", idempotencyKey);
        if (correlationId != null && !correlationId.isBlank()) headers.add("X-Correlation-Id", correlationId);

        var body = Map.of(
                "merchantId", cmd.merchantId(),
                "orderId", cmd.orderId(),
                "amountCents", cmd.amountCents(),
                "currency", cmd.currency(),
                "card", Map.of(
                        "pan", cmd.pan(),
                        "holderName", cmd.holderName(),
                        "expMonth", cmd.expMonth(),
                        "expYear", cmd.expYear(),
                        "cvv", cmd.cvv()
                )
        );

        try {
            var resp = rest.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), PaymentView.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new CoreHttpException(e.getStatusCode().value(), e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentView capture(UUID paymentId, String correlationId) {
        var url = baseUrl + "/payments/" + paymentId + "/capture";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (correlationId != null && !correlationId.isBlank()) headers.add("X-Correlation-Id", correlationId);

        try {
            var resp = rest.exchange(url, HttpMethod.POST, new HttpEntity<>(Map.of(), headers), PaymentView.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new CoreHttpException(e.getStatusCode().value(), e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentView get(UUID paymentId, String correlationId) {
        var url = baseUrl + "/payments/" + paymentId;
        var headers = new HttpHeaders();
        if (correlationId != null && !correlationId.isBlank()) headers.add("X-Correlation-Id", correlationId);

        try {
            var resp = rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), PaymentView.class);
            return resp.getBody();
        } catch (HttpStatusCodeException e) {
            throw new CoreHttpException(e.getStatusCode().value(), e.getResponseBodyAsString());
        }
    }
}
