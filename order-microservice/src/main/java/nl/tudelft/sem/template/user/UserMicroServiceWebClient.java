package nl.tudelft.sem.template.user;

import org.springframework.web.reactive.function.client.WebClient;

public class UserMicroServiceWebClient {

//    @Bean
    public WebClient userMicroServiceWebClient() {
        return WebClient.create("http://localhost:8081");
    }

}
