package nl.tudelft.sem.template.user.API;

import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

public interface UserMicroServiceAPI {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }


}
