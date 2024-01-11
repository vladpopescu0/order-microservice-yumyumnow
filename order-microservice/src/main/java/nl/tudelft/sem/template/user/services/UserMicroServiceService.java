package nl.tudelft.sem.template.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import nl.tudelft.sem.template.user.API.UserMicroServiceAPI;

import java.time.Duration;

@Component
public class UserMicroServiceService implements UserMicroServiceAPI {

    private final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private final WebClient userMicroServiceWebClient;

    @Autowired
    public UserMicroServiceService(WebClient userMicroServiceWebClient){
        this.userMicroServiceWebClient = userMicroServiceWebClient;
    }



}
