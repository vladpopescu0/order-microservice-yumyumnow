package nl.tudelft.sem.template.order.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Order;
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
    private transient OrderService orderService;

    @InjectMocks
    private transient OrderController orderController;

    transient List<UUID> listOfDishes;
    transient List<Order> orders;
    transient Order order1;
    transient Order order2;
    transient Address a1;
    transient String date;

    @BeforeEach
    void setUp() {
        date = "1700006405000";
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
        order1.setDate(new BigDecimal(date));
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
        order2.setDate(new BigDecimal(date));
        order2.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order2.setSpecialRequirements("Knock on the door as well");
        order2.setOrderPaid(true);
        order2.setStatus(Order.StatusEnum.DELIVERED);
        order2.setRating(4);
    }

    @Test
    void testGetListOfDishes_OrderNotFoundException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetListOfDishes_NullFieldException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(NullFieldException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void testGetListOfDishes_exceptionThrown() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(NullPointerException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
    void testGetSpecialRequirements_NullFieldException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = null;
        Mockito.doThrow(NullFieldException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<String> response = orderController.getSpecialRequirements(orderId);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void testGetSpecialRequirements_exceptionThrown() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(NullPointerException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<String> response = orderController.getSpecialRequirements(orderId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
    void testGetOrderAddress_NullFieldException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = null;
        Mockito.doThrow(NullFieldException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<Address> response = orderController.getOrderAddress(orderId);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void testGetOrderAddress_exceptionThrown() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(NullPointerException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<Address> response = orderController.getOrderAddress(orderId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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
    void testGetOrderDate_NullFieldException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = null;
        Mockito.doThrow(NullFieldException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void testGetOrderDate_exceptionThrown() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(NullPointerException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetOrderDate_foundAndRetrieved() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(new BigDecimal(date), response.getBody());
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

}
