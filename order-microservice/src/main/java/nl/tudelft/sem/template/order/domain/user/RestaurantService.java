package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {

    private final transient UserMicroServiceService userMicroServiceService;

    /**
     * Instantiates a new Restaurant service.
     */
    public RestaurantService(UserMicroServiceService userMicroServiceService) {
        this.userMicroServiceService = userMicroServiceService;
    }

    public List<UUID> getAllRestaurants(UUID userID) throws UserIDNotFoundException {
        return null;
    }
}
