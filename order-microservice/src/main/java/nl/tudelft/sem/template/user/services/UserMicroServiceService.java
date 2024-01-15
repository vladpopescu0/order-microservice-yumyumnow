package nl.tudelft.sem.template.user.services;

import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.order.domain.user.UserIDNotFoundException;
import nl.tudelft.sem.template.user.api.UserMicroServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * class that implements the interface UserMicroServiceAPI
 * Is used for making external API calls
 */
@Component
public class UserMicroServiceService implements UserMicroServiceAPI {

    private final transient Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private final transient WebClient userMicroServiceWebClient;

    /**
     * Instantiates a new UserMicroService service.
     *
     * @param userMicroServiceWebClient the userMicroService webClient, used for making calls to API endpoints
     */
    @Autowired
    public UserMicroServiceService(WebClient userMicroServiceWebClient){
        this.userMicroServiceWebClient = userMicroServiceWebClient;
    }

    @Override
    public Address getUserAddress(UUID userID) throws UserIDNotFoundException{
        return userMicroServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/customer/address/{userID").build(userID))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new UserIDNotFoundException(userID)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new UserIDNotFoundException(userID)))
                .bodyToMono(Address.class)
                .block(REQUEST_TIMEOUT);
    }

    /**
     * This method makes an API call to the endpoint <a href="http://localhost:8081/customer/location/">...</a>{userID}
     * Given a userID, it makes the API call to the user microservice to get its location.
     * When the http status is 5xx or 4xx, it will just throw a UserIDNotFoundException
     * Otherwise the API call succeeded, and we convert the responseBody into a string
     * Block(REQUEST_TIMEOUT) is necessary, since making external calls takes time. This is a blocking request
     * In the future, we may change this into a reactive call instead of a blocking one.
     *
     * @param userID of the customer we want the location of.
     */
    @Override
    public String getUserLocation(UUID userID) throws UserIDNotFoundException {
        return userMicroServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/customer/location/{userID}").build(userID))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new UserIDNotFoundException(userID)))
                .onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new UserIDNotFoundException(userID)))
                .bodyToMono(String.class)
                .block(REQUEST_TIMEOUT); // wait only 3 seconds, instead of default 30
    }

    @Override
    public List<String> getAllVendors(){
        return userMicroServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/vendor").build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new RuntimeException("no vendors in database")))
                .bodyToFlux(String.class)
                .collectList()
                .block(REQUEST_TIMEOUT); // wait only 3 seconds, instead of default 30
    }


}
