package br.com.tigelah.merchantapi.entrypoints.http;

import br.com.tigelah.merchantapi.application.ports.AcquirerCoreClient;
import br.com.tigelah.merchantapi.application.usecase.AuthorizePaymentUseCase;
import br.com.tigelah.merchantapi.application.usecase.CapturePaymentUseCase;
import br.com.tigelah.merchantapi.application.usecase.GetPaymentUseCase;
import br.com.tigelah.merchantapi.domain.model.PaymentView;
import br.com.tigelah.merchantapi.entrypoints.http.dto.AuthorizeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
public class PaymentsController {

    private final AuthorizePaymentUseCase authorize;
    private final CapturePaymentUseCase capture;
    private final GetPaymentUseCase get;

    public PaymentsController(AuthorizePaymentUseCase authorize, CapturePaymentUseCase capture, GetPaymentUseCase get) {
        this.authorize = authorize;
        this.capture = capture;
        this.get = get;
    }

    @PostMapping("/authorize")
    public ResponseEntity<PaymentView> authorize(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @Valid @RequestBody AuthorizeRequest req
    ) {
        var cmd = new AcquirerCoreClient.AuthorizeCommand(
                req.merchantId(), req.orderId(), req.amountCents(), req.currency(),
                req.card().pan(), req.card().holderName(), req.card().expMonth(), req.card().expYear(), req.card().cvv(),
                req.accountId(),  // NOVO
                req.userId()      // NOVO
        );
        return ResponseEntity.ok(authorize.execute(cmd, idempotencyKey, correlationId));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentView> capture(
            @PathVariable UUID paymentId,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        return ResponseEntity.ok(capture.execute(paymentId, correlationId));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentView> get(
            @PathVariable UUID paymentId,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId
    ) {
        return ResponseEntity.ok(get.execute(paymentId, correlationId));
    }
}
