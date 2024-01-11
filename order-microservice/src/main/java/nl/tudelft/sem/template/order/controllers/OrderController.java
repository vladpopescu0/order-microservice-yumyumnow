package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.api.OrderApi;
import nl.tudelft.sem.template.order.commons.Address;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.OrderNotFoundException;
import nl.tudelft.sem.template.order.domain.user.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
public class OrderController implements OrderApi {

    private final transient OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
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

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    /**
     * Endpoint for returning all Orders in the database.
     *
     * @return 200 OK - The Orders are successfully returned
     *         404 NOT FOUND - No Orders are stored in the database
     */
    @Override
    public ResponseEntity<List<Order>> getAllOrders() {

        try {
            List<Order> list = orderService.getAllOrders();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for returning an Order with specific ID.
     *
     * @param orderID The provided ID of the order to return
     * @return 200 OK - The provided Order is returned
     *         400 BAD REQUEST - Returning the provided Order is unsuccessful
     *         404 NOT FOUND - No Order exists with the provided ID
     */
    @Override
    public ResponseEntity<Order> getOrderById(UUID orderID) {

        try {
            Order o = orderService.getOrderById(orderID);
            return ResponseEntity.ok(o);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

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
    public ResponseEntity<Order> editOrderByID(UUID orderID, Order order) {

        if (!order.getOrderID().equals(orderID)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Order edited = orderService.editOrderByID(orderID, order);
            return ResponseEntity.ok(edited);
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
    public ResponseEntity<Void> deleteOrderByID(UUID orderID) {
        try {
            orderService.deleteOrderByID(orderID);
            return ResponseEntity.ok().build();
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
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<List<UUID>> getListOfDishes(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getListOfDishes());
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
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<String> getSpecialRequirements(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getSpecialRequirements());
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
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<Address> getOrderAddress(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getAddress());
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
     *         404 NOT FOUND if the order could not be found
     *         400 BAD REQUEST if the retrieval was not successful
     */
    public ResponseEntity<BigDecimal> getOrderDate(UUID orderID) {
        try {
            Order order = orderService.getOrderById(orderID);
            return ResponseEntity.ok(order.getDate());
        } catch (OrderNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //Methods
}
