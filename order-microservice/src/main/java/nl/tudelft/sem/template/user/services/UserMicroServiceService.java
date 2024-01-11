package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.user.API.UserMicroServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;

@Component
public class UserMicroServiceService implements UserMicroServiceAPI {

    private final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private final WebClient userMicroServiceWebClient;

    @Autowired
    public UserMicroServiceService(WebClient userMicroServiceWebClient){
        this.userMicroServiceWebClient = userMicroServiceWebClient;
    }

    @Override
    public String getUserLocation(UUID userID){
        return userMicroServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/customer/location/{userID}").build(userID))
                .retrieve()
                .bodyToMono(String.class)
                .block(REQUEST_TIMEOUT);
    }

}
