package nl.tudelft.sem.template.user.API;

import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;
import java.util.UUID;

public interface UserMicroServiceAPI {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    String getUserLocation(UUID userID);

}
