package nl.tudelft.sem.template.order.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.order.api.VendorApi;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.CustomerNotFoundException;
import nl.tudelft.sem.template.order.domain.user.DishNotFoundException;
import nl.tudelft.sem.template.order.domain.user.NoOrdersException;
import nl.tudelft.sem.template.order.domain.user.OrderService;
import nl.tudelft.sem.template.order.domain.user.VendorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;


@Controller
public class VendorAnalyticsController implements VendorApi {
    private final OrderController orderController;
    private final DishController dishController;
    private final transient OrderService orderService;

    /**
     * Instantiates a new VendorAnalyticsController.
     *
     * @param orderController   the OrderController
     * @param dishController    the DishController
     * @param orderService      the OrderService
     */
    @Autowired
    public VendorAnalyticsController(OrderController orderController,
                                     DishController dishController, OrderService orderService) {
        this.orderController = orderController;
        this.dishController = dishController;
        this.orderService = orderService;
    }

    /**
     * Calculates the total earnings of an order.
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
                            } else if (dishResponseStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
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

    /**
     * Getter for all the orders from a customer at a specific vendor.
     *
     * @param vendorID the UUID of the vendor where the orders have been placed
     * @param customerID the UUID of the customer who placed the orders
     * @return 200 OK with a list of orders from a customer at a certain vendor if they both exist
     *         404 NOT FOUND if either the vendor or the customer does not exist, or both
     *         400 BAD REQUEST if something else went wrong
     */
    @Override
    public ResponseEntity<List<Order>> vendorVendorIDAnalyticsHistoryCustomerIDGet(UUID vendorID, UUID customerID) {
        try {
            List<Order> orders = orderService.getOrdersFromCustomerAtVendor(vendorID, customerID);
            return ResponseEntity.ok(orders);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoOrdersException e) {
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Getter for the total number of orders made at a vendor.
     *
     * @param vendorID the UUID of the vendor from whom the total number of orders is returned
     * @return 200 OK with the total number of orders made at a vendor
     *         404 NOT FOUND if the vendor could not be found
     *         400 BAD REQUEST if something else went wrong
     */
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

    /**
     * Getter for a list containing the volume of orders divided over each hour of the day.
     *
     * @param vendorID the UUID of the vendor from whom the volumes divided over the hours are retrieved
     * @return 200 OK with the volume of orders divided over each hour of the day, where index
     *             0 is for 00:00 till 01:00, 1 is for 01:00 till 02:00, ... 23 is for 23:00 till 00:00
     *         404 NOT FOUND if the vendor could not be found
     *         400 BAD REQUEST if something else went wrong
     */
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

    /**
     * Getter for a list of dishes offered by a vendor ordered by how often they have been ordered.
     * The list only contain dishes that have been ordered at least once
     *
     * @param vendorID UUID of the vendor from which the popular dishes will be retrieved
     * @return 200 OK with the dishes ordered by how often they have been ordered, only including dishes
     *             that have been ordered before
     *         404 NOT FOUND if the vendor could not be found
     *         400 BAD REQUEST if something else went wrong
     */
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
