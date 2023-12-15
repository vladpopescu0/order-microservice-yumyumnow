package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.api.DishApi;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.order.domain.user.DishIdAlreadyInUseException;
import nl.tudelft.sem.template.order.domain.user.DishNotFoundException;
import nl.tudelft.sem.template.order.domain.user.DishService;
import nl.tudelft.sem.template.order.domain.user.VendorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
    @Override
    public ResponseEntity<Dish> addDish(UUID vendorID, Dish dish) {
        try {
            Dish d = dishService.addDish(vendorID, dish);
            return ResponseEntity.ok(dish);
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Dish> getDishByID(UUID dishID) {
        try{
            Dish d = dishService.getDishById(dishID);
            return ResponseEntity.ok(d);
        } catch (DishNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Dish> updateDishByID(UUID dishID, Dish dish) {
        if(dishID.equals(dish.getDishID())){
            return ResponseEntity.badRequest().build();
        }
        try {
            Dish d = dishService.updateDish(dishID, dish);
            return ResponseEntity.ok(d);
        } catch (DishNotFoundException dishNotFoundException){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Dish>> getAllergyFilteredDishesFromVendor(UUID vendorID, List<String> allergies) {
        try {
            List<Dish> d = dishService.getAllergyFilteredDishesFromVendor(vendorID, allergies);
            return ResponseEntity.ok(d);
        } catch (VendorNotFoundException vendorNotFoundException){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Dish>> getDishesByVendorID(UUID vendorID) {
        try{
            List<Dish> d = dishService.getDishByVendorId(vendorID);
            return ResponseEntity.ok(d);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteDishByID(UUID dishID) {
        try{
            dishService.deleteDishByDishId(dishID);
            return ResponseEntity.ok().build();
        } catch (DishNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
