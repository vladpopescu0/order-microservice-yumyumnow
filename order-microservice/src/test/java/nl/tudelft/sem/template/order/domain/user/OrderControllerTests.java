package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.order.commons.Address;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.controllers.OrderController;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrderControllerTests {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    Order order1;
    Address a1;
    List<UUID> listOfDishes;

    @BeforeEach
    void setUp() {
        a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        listOfDishes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(UUID.randomUUID());
        order1.setCustomerID(UUID.randomUUID());
        order1.setAddress(a1);
        order1.setDate(new BigDecimal("1700006405000"));
        order1.setListOfDishes(listOfDishes);
        order1.setSpecialRequirements("Knock on the door");
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.ACCEPTED);
        order1.setRating(4);
    }

    @Test
    void testGetListOfDishes_OrderNotFoundException() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetListOfDishes_listFound() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(listOfDishes, response.getBody());
    }

    @Test
    void testGetSpecialRequirements_OrderNotFoundException() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<String> response = orderController.getSpecialRequirements(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetSpecialRequirements_foundAndRetrieved() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<String> response = orderController.getSpecialRequirements(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Knock on the door", response.getBody());
    }

    @Test
    void testGetOrderAddress_OrderNotFoundException() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<Address> response = orderController.getOrderAddress(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOrderAddress_foundAndRetrieved() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<Address> response = orderController.getOrderAddress(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(a1, response.getBody());
    }

    @Test
    void testGetOrderDate_OrderNotFoundException() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).getOrderById(orderId);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetOrderDate_foundAndRetrieved() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(new BigDecimal("1700006405000"), response.getBody());
    }

    @Test
    void testOrderOrderIDIsPaidGet_OrderIsPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Mockito.when(orderService.orderIsPaid(orderID)).thenReturn(true);

        ResponseEntity<Void> response = orderController.orderOrderIDIsPaidGet(orderID);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testOrderOrderIDIsPaidGet_OrderIsNotPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Mockito.when(orderService.orderIsPaid(orderID)).thenReturn(false);

        ResponseEntity<Void> response = orderController.orderOrderIDIsPaidGet(orderID);

        Assertions.assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
    }

    @Test
    void testOrderOrderIDIsPaidGet_OrderNotFoundException() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).orderIsPaid(orderID);

        ResponseEntity<Void> response = orderController.orderOrderIDIsPaidGet(orderID);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
