package nl.tudelft.sem.template.order.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.api.OrderApi;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.domain.helpers.FilteringByStatus;
import nl.tudelft.sem.template.order.domain.helpers.FilteringParam;
import nl.tudelft.sem.template.order.domain.helpers.OrderValidation;
import nl.tudelft.sem.template.order.domain.user.CustomerNotFoundException;
import nl.tudelft.sem.template.order.domain.user.InvalidOrderStatusException;
import nl.tudelft.sem.template.order.domain.user.NoOrdersException;
import nl.tudelft.sem.template.order.domain.user.NullFieldException;
import nl.tudelft.sem.template.order.domain.user.OrderNotFoundException;
import nl.tudelft.sem.template.order.domain.user.OrderService;
import nl.tudelft.sem.template.order.domain.user.UserIDNotFoundException;
import nl.tudelft.sem.template.user.services.JsonParserService;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements OrderApi {

    private final transient OrderService orderService;
    private final transient UserMicroServiceService userMicroServiceService;
    private final transient DishController dishController;

    /**
     * Constructor method for OrderController.
     *
     * @param orderService an orderservice
     * @param userMicroServiceService a userMicroServiceService
     * @param dishController a dishController
     */
    @Autowired
    public OrderController(OrderService orderService, UserMicroServiceService userMicroServiceService,
                           DishController dishController) {
        this.orderService = orderService;
        this.userMicroServiceService = userMicroServiceService;
        this.dishController = dishController;
    }

    /**
     * Endpoint for adding an order.
     *
     * @param order Order to be added, containing: orderID, vendorID,
     *              customerID, address, date, listOfDishes, specialRequirements,
     *              orderPaid, status, rating, price
     * @return 200 OK - Creating and adding the order was successful
     *         400 BAD REQUEST - Adding operation wasn't successful
     */
    @Override
    public ResponseEntity<Order> createOrder(Order order) {
        try {
            Order o = orderService.createOrder(order);
            return ResponseEntity.ok(o);
        } catch (NullFieldException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for returning all Orders in the database.
     * First checks if the user is an admin
     *
     * @return 200 OK - The Orders are successfully returned
     *         401 UNAUTHORIZED - The user is not authorised to ask for all the orders
     *         404 NOT FOUND - No Orders are stored in the database
     */
    @Override
    public ResponseEntity<List<Order>> getAllOrders(UUID userID) {

        try {
            String jsonUser = userMicroServiceService.getUserInformation(userID);
            if (jsonUser == null || jsonUser.isEmpty()) {
                throw new UserIDNotFoundException(userID);
            }
            String userType = JsonParserService.parseUserType(jsonUser);
            if (userType.equals("Admin")) {
                List<Order> list = orderService.getAllOrders();
                return ResponseEntity.ok(list);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for returning an Order with specific ID.
     *
     * @param orderID The provided ID of the order to return
     * @return 200 OK - The provided Order is returned
     *         422 UNPROCESSABLE ENTITY if the orderID is null
     *         400 BAD REQUEST - Returning the provided Order is unsuccessful
     *         404 NOT FOUND - No Order exists with the provided ID
     */
    @Override
    public ResponseEntity<Order> getOrderById(UUID orderID) {

        try {
            Order o = orderService.getOrderById(orderID);
            return ResponseEntity.ok(o);
        } catch (NullFieldException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for returning an order to a vendor by specifying its id.
     * The order is returned only after it is paid and if all of its dishes are available
     *
     * @param orderId the id of the order to be retrieved
     * @return 200 OK if the order has been successfully retrieved, including the order
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful or the order was invalid
     */
    @Override
    public ResponseEntity<Order> orderOrderIDVendorGet(UUID orderId) {
        OrderValidation orderValidation = new OrderValidation(this, dishController);
        ResponseEntity<Boolean> response = orderValidation.isOrderValid(orderId);
        if (Boolean.TRUE.equals(response.getBody())) {
            ResponseEntity<Order> order = getOrderById(orderId);
            return order;
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Endpoint for editing an Order.
     *
     * @param orderID The provided ID of the order to be edited
     * @param order The edited Order
     * @return 200 OK - The Order is successfully edited
     *         400 BAD REQUEST - Editing the Order is unsuccessful
     *         404 NOT FOUND - No Order exists with the provided ID
     */
    @Override
    public ResponseEntity<Order> editOrderByID(UUID orderID, UUID userID, Order order) {

        if (!order.getOrderID().equals(orderID)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String jsonUser = userMicroServiceService.getUserInformation(userID);
            if (jsonUser == null || jsonUser.isEmpty()) {
                throw new UserIDNotFoundException(userID);
            }
            String userType = JsonParserService.parseUserType(jsonUser);
            if (userType.equals("Admin")) {
                Order edited = orderService.editOrderByID(orderID, order);
                return ResponseEntity.ok(edited);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for deleting an Order.
     *
     * @param orderID The provided ID of the Order to delete
     * @return 200 OK - The provided Order is deleted
     *         400 BAD REQUEST - Deleting the provided Order is unsuccessful
     *         404 NOT FOUND - No Order exists with the provided ID
     */
    @Override
    public ResponseEntity<Void> deleteOrderByID(UUID orderID, UUID userID) {
        try {
            String jsonUser = userMicroServiceService.getUserInformation(userID);
            if (jsonUser == null || jsonUser.isEmpty()) {
                throw new UserIDNotFoundException(userID);
            }
            String userType = JsonParserService.parseUserType(jsonUser);
            if (userType.equals("Admin")) {
                orderService.deleteOrderByID(orderID);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OrderID isPaid controller methods.
     * It throws a 402 if the order is not paid and a 404 if the order is not found.
     *
     * @param orderID the id of the order to be checked.
     * @return the http response as said in the description.
     */
    @Override
    public ResponseEntity<Void> orderOrderIDIsPaidGet(UUID orderID) {
        try {
            boolean isPaid = orderService.orderIsPaid(orderID);
            if (isPaid) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(402).build();
            }
        } catch (OrderNotFoundException notFound) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * OrderID isPaid controller method to update the isPaid field.
     * It throws a 404 if the order is not found.
     *
     * @param orderID the id of the order to be checked
     * @return the order after it was updated
     */

    @Override
    public ResponseEntity<Order> updateOrderPaid(UUID orderID) {
        try {
            Order updatedOrder = orderService.orderIsPaidUpdate(orderID);
            return ResponseEntity.ok(updatedOrder);
        } catch (OrderNotFoundException notFound) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OrderID getListOfDishes controller methods.
     * It returns the list of the UUID of the dishes the order contains.
     *
     * @param orderID the id of the order to be retrieved the list of dishes from.
     * @return 200 OK if the list of dishes has been retrieved, including the list of UUID dishes
     *         422 UNPROCESSABLE ENTITY if the orderID is null
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<List<UUID>> getListOfDishes(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getListOfDishes());
        } catch (NullFieldException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OrderID getSpecialRequirements controller methods.
     * It returns the special requirements for the order.
     * It throws a 404 if the order is not found.
     *
     * @param orderID the id of the order to be retrieved the special requirements from.
     * @return 200 OK if the special requirements of the order has been retrieved, including the special requirements
     *         422 UNPROCESSABLE ENTITY if the orderID is null
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<String> getSpecialRequirements(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getSpecialRequirements());
        } catch (NullFieldException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OrderID getOrderAddress controller methods.
     * It returns the address to which the order should be delivered.
     *
     * @param orderID the id of the order to be delivered to the retrieved address.
     * @return 200 OK if the address of the order has been retrieved, including the address
     *         422 UNPROCESSABLE ENTITY if the orderID is null
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<Address> getOrderAddress(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getAddress());
        } catch (NullFieldException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OrderID getOrderDate controller methods.
     * It returns the date when the order was made.
     *
     * @param orderID the id of the order of which the date of creation should be retrieved.
     * @return 200 OK if the date of the order has been retrieved, including the date
     *         422 UNPROCESSABLE ENTITY if the orderID is null
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<BigDecimal> getOrderDate(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getDate());
        } catch (NullFieldException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * OrderID getCustomerName controller method.
     * It returns the name of the customer that created a specific order
     *
     * @param orderID the id of the order from which the customer name should be retrieved
     * @return 200 OK if the name of the customer has been successfully retrieved, including the name
     *         422 UNPROCESSABLE ENTITY if the orderID is null
     *         404 NOT FOUND if the order or the user could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<String> getCustomerName(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            UUID userID = order.getCustomerID();
            return ResponseEntity.ok(userMicroServiceService.getUserName(userID));
        } catch (NullFieldException e) {
            return ResponseEntity.unprocessableEntity().build();
        } catch (UserIDNotFoundException userIDNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** Controller for the /order/{orderID}/history endpoint.
     *
     * @param customerID the id of the customer on which we base the SQL query
     *
     * @return a response which can be 200 if there is at least a past order of this user in the database
     *                                 404 if there are no orders of this user
     *                                 400 if something else goes wrong
     */
    @Override
    public ResponseEntity<List<Order>> getCustomerOrderHistory(UUID customerID) {
        try {
            FilteringParam<Order> filteringParam = new FilteringByStatus(Order.StatusEnum.DELIVERED);
            List<Order> allOrdersByCustomerID = orderService.getPastOrdersByCustomerID(customerID, filteringParam);
            return ResponseEntity.ok(allOrdersByCustomerID);
        } catch (NoOrdersException noOrdersException) {
            return ResponseEntity.notFound().build();
        } catch (CustomerNotFoundException customerNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**Controller for the /order/{orderID}/totalCost endpoint.
     * It takes the implementation provided in VendorAnalyticsController.
     *
     * @param orderID ID of order that needs to be fetched (required)
     * @return a response where for 200 returns the total sum of the
     *          dishes that can be found in the database
     */
    @Override
    public ResponseEntity<Float> orderOrderIDTotalCostGet(UUID orderID) {
        VendorAnalyticsController vendorAnalyticsController =
                new VendorAnalyticsController(this, dishController, orderService);
        return vendorAnalyticsController.getOrderEarnings(orderID);
    }


    /**
     * Endpoint for getting the status of an Order.
     *
     * @param orderID ID of order to return (required)
     * @return status of Order in String value
     */
    @Override
    public ResponseEntity<String> getStatusOfOrderById(UUID orderID) {

        try {
            String status = orderService.getStatusOfOrderById(orderID).toString();
            return ResponseEntity.ok(status);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint for updating the status of an Order.
     *
     * @param orderID ID specifying the Order
     * @param status New Status as String
     * @return Edited Order in the database
     */
    @Override
    public ResponseEntity<Void> updateStatusOfOrderById(UUID orderID, String status) {
        try {
            orderService.updateStatusOfOrderById(orderID, status);
            return ResponseEntity.ok().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidOrderStatusException e) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Endpoint for getting the rating of an Order.
     *
     * @param orderID ID specifying order
     * @return rating of Order as int value
     */
    @Override
    public ResponseEntity<Integer> getOrderRatingByID(UUID orderID) {

        try {
            int rating = orderService.getOrderRatingByID(orderID);
            return ResponseEntity.ok(rating);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
