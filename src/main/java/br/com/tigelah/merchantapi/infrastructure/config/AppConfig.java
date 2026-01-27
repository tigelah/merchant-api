package br.com.tigelah.merchantapi.infrastructure.config;

import br.com.tigelah.merchantapi.application.ports.AcquirerCoreClient;
import br.com.tigelah.merchantapi.application.usecase.AuthorizePaymentUseCase;
import br.com.tigelah.merchantapi.application.usecase.CapturePaymentUseCase;
import br.com.tigelah.merchantapi.application.usecase.GetPaymentUseCase;
import br.com.tigelah.merchantapi.infrastructure.http.AcquirerCoreHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AcquirerCoreClient acquirerCoreClient(RestTemplate rest, @Value("${acquirer.core.base-url}") String baseUrl) {
        return new AcquirerCoreHttpClient(rest, baseUrl);
    }

    @Bean public AuthorizePaymentUseCase authorizePaymentUseCase(AcquirerCoreClient client) { return new AuthorizePaymentUseCase(client); }
    @Bean public CapturePaymentUseCase capturePaymentUseCase(AcquirerCoreClient client) { return new CapturePaymentUseCase(client); }
    @Bean public GetPaymentUseCase getPaymentUseCase(AcquirerCoreClient client) { return new GetPaymentUseCase(client); }
}
