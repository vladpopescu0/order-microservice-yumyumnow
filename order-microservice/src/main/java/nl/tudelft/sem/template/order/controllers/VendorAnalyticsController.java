package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.api.VendorApi;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.domain.user.DishNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public class VendorAnalyticsController implements VendorApi {
    private final OrderController orderController;
    private final DishController dishController;

    public VendorAnalyticsController(OrderController orderController, DishController dishController) {
        this.orderController = orderController;
        this.dishController = dishController;
    }

    /**
     * Calculates the total earnings of an order
     *
     * @param orderId the UUID of the order to calculate the total earnings of
     * @return 200 OK if the calculation is successful, including a float representing the total earnings
     *         400 BAD REQUEST if the calculation was unsuccessful
     */
    public ResponseEntity<Float> getOrderEarnings(UUID orderId) throws DishNotFoundException {
        try {
            Float earnings = 0.0f;
            ResponseEntity<List<UUID>> listResponse = orderController.getListOfDishes(orderId);
            if (listResponse.getStatusCode().equals(HttpStatus.OK)) {
                List<UUID> listOfDishes = orderController.getListOfDishes(orderId).getBody();

                if (listOfDishes != null) {
                    for (UUID id : listOfDishes) {
                        ResponseEntity<Dish> dishResponse;
                        int t = 0;

                        while (t < 3) {
                            dishResponse = dishController.getDishByID(id);
                            HttpStatus dishResponseStatus = dishResponse.getStatusCode();

                            if (dishResponseStatus.equals(HttpStatus.OK)) {
                                Dish dish = dishController.getDishByID(id).getBody();
                                earnings += dish.getPrice();
                                break;
                            }
                            else if (dishResponseStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR)){
                                wait(1500);
                                t++;
                            } else {
                                break;
                            }
                        }
                    }
                    return ResponseEntity.ok(earnings);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
