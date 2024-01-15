package nl.tudelft.sem.template.order.domain.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.order.commons.Address;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.domain.helpers.FilteringByStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderControllerTests {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    List<UUID> listOfDishes;
    List<Order> orders;
    Order order1;
    Order order2;
    Address a1;

    @BeforeEach
    void setUp() {
        a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        orders = new ArrayList<>();
        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(UUID.randomUUID());
        order1.setCustomerID(UUID.randomUUID());
        order1.setAddress(a1);
        order1.setDate(new BigDecimal("1700006405000"));
        listOfDishes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        order1.setListOfDishes(listOfDishes);
        order1.setSpecialRequirements("Knock on the door");
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.DELIVERED);
        order1.setRating(4);

        order2 = new Order();
        order2.setOrderID(UUID.randomUUID());
        order2.setVendorID(UUID.randomUUID());
        order2.setCustomerID(order1.getCustomerID());
        order2.setAddress(null);
        order2.setDate(new BigDecimal("1700006405000"));
        order2.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order2.setSpecialRequirements("Knock on the door");
        order2.setOrderPaid(true);
        order2.setStatus(Order.StatusEnum.DELIVERED);
        order2.setRating(4);
    }

    @Test
    void createOrderSuccessful() throws NullFieldException, OrderIdAlreadyInUseException {

        when(orderService.createOrder(order1)).thenReturn(order1);
        ResponseEntity<Order> order = orderController.createOrder(order1);
        Assertions.assertEquals(order1, order.getBody());
        Assertions.assertEquals(HttpStatus.OK, order.getStatusCode());

    }

    @Test
    void createNullFieldOrder() throws NullFieldException, OrderIdAlreadyInUseException {

        when(orderService.createOrder(order1)).thenThrow(NullFieldException.class);
        ResponseEntity<Order> order = orderController.createOrder(order1);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, order.getStatusCode());

    }

    @Test
    void createOrderBadRequest() throws NullFieldException, OrderIdAlreadyInUseException {

        when(orderService.createOrder(order1)).thenThrow(OrderIdAlreadyInUseException.class);
        ResponseEntity<Order> order = orderController.createOrder(order1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());

    }

    @Test
    void getOrderByIdSuccessful() throws OrderNotFoundException, NullFieldException {

        when(orderService.getOrderById(order1.getOrderID())).thenReturn(order1);
        ResponseEntity<Order> order = orderController.getOrderById(order1.getOrderID());
        Assertions.assertEquals(order1, order.getBody());
        Assertions.assertEquals(HttpStatus.OK, order.getStatusCode());

    }

    @Test
    void getOrderByIdNullField() throws OrderNotFoundException, NullFieldException {

        when(orderService.getOrderById(order1.getOrderID())).thenThrow(NullFieldException.class);
        ResponseEntity<Order> order = orderController.getOrderById(order1.getOrderID());
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, order.getStatusCode());
    }

    @Test
    void getOrderByIdOrderNotFound() throws OrderNotFoundException, NullFieldException {

        when(orderService.getOrderById(order1.getOrderID())).thenThrow(OrderNotFoundException.class);
        ResponseEntity<Order> order = orderController.getOrderById(order1.getOrderID());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, order.getStatusCode());
    }
    
    @Test
    void getOrderByIdException() throws OrderNotFoundException, NullFieldException {
        
        when(orderService.getOrderById(order1.getOrderID())).thenThrow(RuntimeException.class);
        ResponseEntity<Order> order = orderController.getOrderById(order1.getOrderID());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());
        
    }

    @Test
    void editOrderByIdSuccessful() throws OrderNotFoundException, NullFieldException {

        when(orderService.editOrderByID(order1.getOrderID(), order1)).thenReturn(order1);
        ResponseEntity<Order> order = orderController.editOrderByID(order1.getOrderID(), order1);
        Assertions.assertEquals(order1, order.getBody());
        Assertions.assertEquals(HttpStatus.OK, order.getStatusCode());

    }

    @Test
    void editOrderByIdDifferingId() {

        UUID randomID = UUID.randomUUID();
        ResponseEntity<Order> order = orderController.editOrderByID(randomID, order1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());

    }

    @Test
    void editOrderByIdNullField() throws OrderNotFoundException, NullFieldException {

        when(orderService.editOrderByID(order1.getOrderID(), order1)).thenThrow(NullFieldException.class);
        ResponseEntity<Order> order = orderController.editOrderByID(order1.getOrderID(), order1);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, order.getStatusCode());

    }

    @Test
    void editOrderByIdOrderNotFound() throws OrderNotFoundException, NullFieldException {

        when(orderService.editOrderByID(order1.getOrderID(), order1)).thenThrow(OrderNotFoundException.class);
        ResponseEntity<Order> order = orderController.editOrderByID(order1.getOrderID(), order1);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, order.getStatusCode());

    }

    @Test
    void editOrderByIdOrderException() throws OrderNotFoundException, NullFieldException {

        when(orderService.editOrderByID(order1.getOrderID(), order1)).thenThrow(RuntimeException.class);
        ResponseEntity<Order> order = orderController.editOrderByID(order1.getOrderID(), order1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());

    }




    @Test
    void testGetListOfDishes_OrderNotFoundException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetListOfDishes_listFound() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(listOfDishes, response.getBody());
    }

    @Test
    void testGetSpecialRequirements_OrderNotFoundException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<String> response = orderController.getSpecialRequirements(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetSpecialRequirements_foundAndRetrieved() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<String> response = orderController.getSpecialRequirements(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Knock on the door", response.getBody());
    }

    @Test
    void testGetOrderAddress_OrderNotFoundException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<Address> response = orderController.getOrderAddress(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOrderAddress_foundAndRetrieved() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<Address> response = orderController.getOrderAddress(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(a1, response.getBody());
    }

    @Test
    void testGetOrderDate_OrderNotFoundException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOrderDate_foundAndRetrieved() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(new BigDecimal("1700006405000"), response.getBody());

        order2 = new Order();
        order2.setOrderID(UUID.randomUUID());
        order2.setVendorID(UUID.randomUUID());
        order2.setCustomerID(order1.getCustomerID());
        order2.setAddress(null);
        order2.setDate(new BigDecimal("1700006405000"));
        order2.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order2.setSpecialRequirements("Knock on the door");
        order2.setOrderPaid(true);
        order2.setStatus(Order.StatusEnum.DELIVERED);
        order2.setRating(4);
    }

    @Test
    void testOrderOrderIDIsPaidGet_OrderIsPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        when(orderService.orderIsPaid(orderID)).thenReturn(true);

        ResponseEntity<Void> response = orderController.orderOrderIDIsPaidGet(orderID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testOrderOrderIDIsPaidGet_OrderIsNotPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        when(orderService.orderIsPaid(orderID)).thenReturn(false);

        ResponseEntity<Void> response = orderController.orderOrderIDIsPaidGet(orderID);

        assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
    }

    @Test
    void testOrderOrderIDIsPaidGet_OrderNotFoundException() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).orderIsPaid(orderID);

        ResponseEntity<Void> response = orderController.orderOrderIDIsPaidGet(orderID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testPaymentWhenExists() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Order order = new Order();
        order.setOrderID(orderID);
        order.setOrderPaid(false);
        when(orderService.orderIsPaidUpdate(orderID)).thenReturn(order);

        ResponseEntity<Order> response = orderController.updateOrderPaid(orderID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(order, response.getBody());
    }

    @Test
    void testPaymentWhenNotExists() throws OrderNotFoundException {
        UUID orderIDFake = UUID.randomUUID();
        when(orderService.orderIsPaidUpdate(orderIDFake)).thenThrow(OrderNotFoundException.class);

        ResponseEntity<Order> response = orderController.updateOrderPaid(orderIDFake);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetCustomerOrderHistory_NoOrdersFound() throws NoOrdersException {
        UUID customerId = UUID.randomUUID();

        when(orderService.getPastOrdersByCustomerID(eq(customerId),  Mockito.any(FilteringByStatus.class)))
                .thenThrow(new NoOrdersException());

        var response = orderController.getCustomerOrderHistory(customerId);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    void testGetCustomerOrderHistory_Success() throws NoOrdersException {
        UUID customerId = UUID.randomUUID();
        List<Order> orders = new ArrayList<>();



        orders.add(order1);
        orders.add(order2);

        when(orderService.getPastOrdersByCustomerID(eq(customerId), Mockito.any(FilteringByStatus.class)))
                .thenReturn(orders);

        var response = orderController.getCustomerOrderHistory(customerId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orders, response.getBody());
    }

    @Test
    void deleteOrderByIdSuccessful() {

        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID());
        Assertions.assertEquals(HttpStatus.OK, order.getStatusCode());

    }

    @Test
    void deleteOrderByIdOrderNotFoundException() throws OrderNotFoundException {

        Mockito.doThrow(OrderNotFoundException.class).when(orderService).deleteOrderByID(order1.getOrderID());
        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, order.getStatusCode());

    }

    @Test
    void deleteOrderByIdException() throws OrderNotFoundException {

        Mockito.doThrow(RuntimeException.class).when(orderService).deleteOrderByID(order1.getOrderID());
        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());

    }

    @Test
    void getAllOrdersSuccessful() throws NoOrdersException {

        List<Order> allOrders = new ArrayList<>();
        allOrders.add(order1);
        allOrders.add(order2);

        when(orderService.getAllOrders()).thenReturn(allOrders);

        ResponseEntity<List<Order>> orders = orderController.getAllOrders();

        Assertions.assertEquals(allOrders, orders.getBody());
        Assertions.assertEquals(HttpStatus.OK, orders.getStatusCode());

    }

    @Test
    void getAllOrdersException() throws NoOrdersException {

        when(orderService.getAllOrders()).thenThrow(NoOrdersException.class);
        ResponseEntity<List<Order>> orders = orderController.getAllOrders();
        Assertions.assertEquals(HttpStatus.NOT_FOUND, orders.getStatusCode());


    }


}
