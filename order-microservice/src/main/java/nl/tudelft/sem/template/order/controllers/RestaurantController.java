package nl.tudelft.sem.template.order.controllers;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.api.RestaurantsApi;
import nl.tudelft.sem.template.order.domain.user.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestaurantController implements RestaurantsApi {

    private final transient RestaurantService restaurantService;

    /**
     * Instantiates a new Restaurant controller.
     *
     * @param restaurantService the restaurant service
     */
    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Endpoint for getting the restaurants within 5km around a user's address.
     * if the user's address exists, otherwise search around the user's current location
     *
     * @param userID of the user
     * @return 200 OK - Searching the nearby restaurants around user successful
     *         400 BAD REQUEST - UserID is not valid
     *         404 NOT FOUND - Could not get restaurants around user
     */
    @Override
    public ResponseEntity<List<UUID>> getAllRestaurants(UUID userID) {
        if (userID == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<UUID> list = restaurantService.getAllRestaurants(userID);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for searching for restaurants on cuisineType around the user.
     *
     * @param userID of the user
     * @param query of the user, query searches for cuisineType of a vendor
     * @return 200 OK - Searching restaurants with query successful
     *         400 BAD REQUEST - UserID is not valid or query is not valid
     *         404 NOT FOUND - Could not find restaurants around the user meeting the criteria
     */
    @Override
    public ResponseEntity<List<UUID>> getAllRestaurantsWithQuery(UUID userID, String query) {
        if (userID == null || query == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<UUID> list = restaurantService.getAllRestaurantsWithQuery(userID, query);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}
