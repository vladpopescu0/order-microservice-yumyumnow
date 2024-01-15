package nl.tudelft.sem.template.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    /**
     * configurations for webclient.
     *
     * @return Webclient
     */
    @Bean
    public WebClient userMicroServiceWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }
}
