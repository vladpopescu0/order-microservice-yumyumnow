package nl.tudelft.sem.template.order.domain.helpers;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.order.controllers.DishController;
import nl.tudelft.sem.template.order.controllers.OrderController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OrderValidation {
    private final transient OrderController orderController;
    private final transient DishController dishController;

    public OrderValidation(OrderController orderController, DishController dishController) {
        this.orderController = orderController;
        this.dishController = dishController;
    }

    /**
     * Verifies if all dishes of the order are available.
     *
     * @param orderId the id of the order from which the availability of dishes is verified
     * @return 200 OK if no problems appear during the verification, together with the boolean TRUE or FALSE
     *         404 NOT FOUND if the list of dishes cannot be retrieved
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<Boolean> areAllDishesAvailable(UUID orderId) {
        try {
            ResponseEntity<List<UUID>> dishesResponse = orderController.getListOfDishes(orderId);

            if (notFoundHandler(dishesResponse)) {
                for (UUID dishId : dishesResponse.getBody()) {
                    ResponseEntity<Dish> dishResponseEntity = dishController.getDishByID(dishId);
                    if (!dishResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                        return ResponseEntity.ok(false);
                    }
                }
                return ResponseEntity.ok(true);
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** Checks whether it needs to throw a 404.
     *
     * @param dishesResponse the response on which to take the status code from
     * @return true if the conditions are fine, false otherwise
     */
    private static boolean notFoundHandler(ResponseEntity<List<UUID>> dishesResponse) {
        return dishesResponse.getStatusCode().equals(HttpStatus.OK) && dishesResponse.getBody() != null;
    }

    /**
     * Verifies if the order is valid and can be processed by the vendor.
     * An order is valid if it has been paid and all dishes are available.
     *
     * @param orderId the id of the order that is verified
     * @return 200 OK if the order is valid, and the boolean TRUE or FALSE
     *         400 BAD REQUEST if the verification was not successful
     */
    public ResponseEntity<Boolean> isOrderValid(UUID orderId) {
        try {
            HttpStatus isOrderPaid = orderController.orderOrderIDIsPaidGet(orderId).getStatusCode();
            ResponseEntity<Boolean> dishesAvailability = this.areAllDishesAvailable(orderId);

            if (isOrderPaid.equals(HttpStatus.OK)
                    && (dishesAvailability.getBody() != null && dishesAvailability.getBody())) {
                return ResponseEntity.ok(true);
            }

            return ResponseEntity.ok(false);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
