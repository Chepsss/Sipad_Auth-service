package it.almaviva.difesa.cessazione.auth.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Component
public class WebClientConfig {

    @Bean
    @Qualifier("serviceWebClient")
    WebClient.Builder getServiceWebClient() {
        return WebClient.builder();
    }

    @Bean
    @Qualifier("webClient")
    WebClient.Builder getWebClient() {

        HttpClient httpClient = HttpClient
                .create()
                .wiretap(this.getClass().getCanonicalName(),
                        LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL);

        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

}
