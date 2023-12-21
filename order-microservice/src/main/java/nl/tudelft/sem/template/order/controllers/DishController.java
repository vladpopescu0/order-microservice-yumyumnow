package nl.tudelft.sem.template.order.controllers;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.order.api.DishApi;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.domain.user.DishNotFoundException;
import nl.tudelft.sem.template.order.domain.user.DishService;
import nl.tudelft.sem.template.order.domain.user.VendorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DishController implements DishApi {
    private final transient DishService dishService;

    /**
     * Instantiates a new DishController.
     *
     * @param dishService   the dish service
     */
    @Autowired
    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    /**
     * Endpoint for adding a dish.
     *
     * @param dish The Dish that has to be added to the database
     * @return 200 OK if the addition of the dish was successful, including the added dish
     *         400 BAD REQUEST if the addition was unsuccessful
     */
    @Override
    public ResponseEntity<Dish> addDish(Dish dish) {
        try {
            Dish d = dishService.addDish(dish);
            return ResponseEntity.ok(d);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for getting a dish.
     *
     * @param dishId The id of the dish that has to be retrieved from the database
     * @return 200 OK with the dish if the retrieval was successful
     *         404 NOT FOUND if the dish could not be found
     *         400 BAD REQUEST if the format for the dishId is malformed
     */
    @Override
    public ResponseEntity<Dish> getDishByID(UUID dishId) {
        try {
            Dish d = dishService.getDishById(dishId);
            return ResponseEntity.ok(d);
        } catch (DishNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for updating a dish.
     *
     * @param dishId The id of the dish that has to be retrieved from the database
     * @param dish The dish that has to replace the old one
     * @return 200 OK with the dish if the update was successful
     *         404 NOT FOUND if the dish could not be found
     *         400 BAD REQUEST if the format for the dishId or dish is malformed
     */
    @Override
    public ResponseEntity<Dish> updateDishByID(UUID dishId, Dish dish) {
        if (!dishId.equals(dish.getDishID())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Dish d = dishService.updateDish(dishId, dish);
            return ResponseEntity.ok(d);
        } catch (DishNotFoundException dishNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for getting dishes from a vendor filtered by a list of allergies.
     *
     * @param vendorId The id of the vendor from whom the dishes have to be retrieved from the database
     * @param allergies List of allergies that should not be in the dishes
     * @return 200 OK with the list of filtered dishes from the vendor if the retrieval was successful
     *         404 NOT FOUND if the vendor could not be found
     *         400 BAD REQUEST if the format for the vendorId or allergies is malformed
     */
    @Override
    public ResponseEntity<List<Dish>> getAllergyFilteredDishesFromVendor(UUID vendorId, List<String> allergies) {
        try {
            List<Dish> d = dishService.getAllergyFilteredDishesFromVendor(vendorId, allergies);
            return ResponseEntity.ok(d);
        } catch (VendorNotFoundException vendorNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for getting dishes from a vendor.
     *
     * @param vendorId The id of the vendor from whom the dishes have to be retrieved from the database
     * @return 200 OK with the list of dishes from the vendor if the retrieval was successful
     *         404 NOT FOUND if the vendor could not be found
     *         400 BAD REQUEST if the format for the vendorId is malformed
     */
    @Override
    public ResponseEntity<List<Dish>> getDishesByVendorID(UUID vendorId) {
        try {
            List<Dish> d = dishService.getDishByVendorId(vendorId);
            return ResponseEntity.ok(d);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for deleting a dish.
     *
     * @param dishId The id of the dish that has to be removed from the database
     * @return 200 OK if the dish was successfully removed
     *         404 NOT FOUND if the dish could not be found
     *         400 BAD REQUEST if the format for the dishId is malformed
     */
    @Override
    public ResponseEntity<Void> deleteDishByID(UUID dishId) {
        try {
            dishService.deleteDishByDishId(dishId);
            return ResponseEntity.ok().build();
        } catch (DishNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
