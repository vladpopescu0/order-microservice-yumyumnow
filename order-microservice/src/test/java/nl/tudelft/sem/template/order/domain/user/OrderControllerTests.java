package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.domain.helpers.FilteringByStatus;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderControllerTests {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    List<Order> orders;

    Order order1;

    Order order2;

    @BeforeEach
    void setUp() {
        orders = new ArrayList<>();
        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(UUID.randomUUID());
        order1.setCustomerID(UUID.randomUUID());
        order1.setAddress(null);
        order1.setDate(new BigDecimal("1700006405000"));
        order1.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
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
        assertEquals(order,response.getBody());
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
