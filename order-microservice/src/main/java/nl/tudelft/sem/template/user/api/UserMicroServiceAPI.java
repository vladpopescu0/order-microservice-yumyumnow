package nl.tudelft.sem.template.user.api;

import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMicroServiceAPI {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    String getUserLocation(UUID userID);
    List<String> getAllVendors();
}
