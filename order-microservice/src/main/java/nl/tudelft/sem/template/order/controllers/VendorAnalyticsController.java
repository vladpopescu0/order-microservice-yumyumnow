package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.api.VendorApi;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.CustomerNotFoundException;
import nl.tudelft.sem.template.order.domain.user.NoOrdersException;
import nl.tudelft.sem.template.order.domain.user.OrderService;
import nl.tudelft.sem.template.order.domain.user.VendorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class VendorAnalyticsController implements VendorApi {
    private final OrderController orderController;
    private final DishController dishController;
    private final transient OrderService orderService;

    @Autowired
    public VendorAnalyticsController(OrderController orderController, DishController dishController, OrderService orderService) {
        this.orderController = orderController;
        this.dishController = dishController;
        this.orderService = orderService;
    }

    /**
     * Calculates the total earnings of an order
     *
     * @param orderId the UUID of the order to calculate the total earnings of
     * @return 200 OK if the calculation is successful, including a float representing the total earnings
     *         400 BAD REQUEST if the calculation was unsuccessful
     */
    public ResponseEntity<Float> getOrderEarnings(UUID orderId) {
        try {
            Float earnings = 0.0f;
            ResponseEntity<List<UUID>> listResponse = orderController.getListOfDishes(orderId);
            if (listResponse.getStatusCode().equals(HttpStatus.OK)) {
                List<UUID> listOfDishes = orderController.getListOfDishes(orderId).getBody();
                if (listOfDishes != null) {
                    for (UUID id : listOfDishes) {
                        ResponseEntity<Dish> dishResponse = dishController.getDishByID(id);
                        if (dishResponse.getStatusCode().equals(HttpStatus.OK)) {
                            Dish dish = dishController.getDishByID(id).getBody();
                            earnings += dish.getPrice();
                        }
                    }
                }
            }
            return ResponseEntity.ok(earnings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Order>> vendorVendorIDAnalyticsHistoryCustomerIDGet(UUID vendorID, UUID customerID) {
        try {
            List<Order> orders = orderService.getOrdersFromCostumerAtVendor(vendorID,customerID);
            return ResponseEntity.ok(orders);
        } catch (VendorNotFoundException | CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoOrdersException e) {
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Integer> vendorVendorIDAnalyticsOrderVolumesGet(UUID vendorID) {
        try {
            Integer volume = orderService.getOrderVolume(vendorID);
            return ResponseEntity.ok(volume);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoOrdersException e) {
            return ResponseEntity.ok(0);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Integer>> vendorVendorIDAnalyticsPeakTimesGet(UUID vendorID) {
        try {
            List<Integer> volumes = orderService.getOrderVolumeByTime(vendorID);
            return ResponseEntity.ok(volumes);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoOrdersException e) {
            return ResponseEntity.ok(Arrays.stream(new int[24]).boxed().collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Dish>> vendorVendorIDAnalyticsPopularItemsGet(UUID vendorID) {
        try {
            List<Dish> dishes = orderService.getDishesSortedByVolume(vendorID);
            return ResponseEntity.ok(dishes);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
