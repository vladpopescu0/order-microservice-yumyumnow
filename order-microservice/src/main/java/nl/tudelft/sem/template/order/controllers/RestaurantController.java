package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.api.RestaurantsApi;
import nl.tudelft.sem.template.order.domain.user.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    @Override
    public ResponseEntity<List<UUID>> getAllRestaurants(UUID userID){
        if(userID == null){
            return ResponseEntity.badRequest().build();
        }
        try {
            List<UUID> list = restaurantService.getAllRestaurants(userID);
            return ResponseEntity.ok(list);
        } catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

}
