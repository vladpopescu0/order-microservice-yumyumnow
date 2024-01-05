package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.order.commons.Address;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.repositories.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    Address a1;
    Order order1;

    @BeforeEach
    void setup() {
        a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(UUID.randomUUID());
        order1.setCustomerID(UUID.randomUUID());
        order1.setAddress(a1);
        order1.setDate(new BigDecimal("1700006405000"));
        order1.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order1.setSpecialRequirements("Knock on the door");
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.ACCEPTED);
        order1.setRating(4);

    }

    @Test
    void testCheckUUIDIsUnique_WhenExists() {
        UUID existingUUID = UUID.randomUUID();
        Mockito.when(orderRepository.existsByOrderID(existingUUID)).thenReturn(true);

        boolean isUnique = orderService.checkUUIDIsUnique(existingUUID);

        Assertions.assertTrue(isUnique);
    }

    @Test
    void testCheckUUIDIsUnique_WhenNotExists() {
        UUID nonExistingUUID = UUID.randomUUID();
        Mockito.when(orderRepository.existsByOrderID(nonExistingUUID)).thenReturn(false);

        boolean isUnique = orderService.checkUUIDIsUnique(nonExistingUUID);

        Assertions.assertFalse(isUnique);
    }

    @Test
    void testCreateOrderSuccessful() throws OrderIdAlreadyInUseException {

        Mockito.when(orderRepository.save(order1)).thenReturn(order1);

        Order savedOrder = orderService.createOrder(order1);

        Assertions.assertEquals(savedOrder, order1);

    }

    @Test
    void testCreateOrderIdTaken() throws OrderIdAlreadyInUseException {

        UUID takenId = order1.getOrderID();
        Mockito.when(orderService.checkUUIDIsUnique(takenId)).thenReturn(true);

        Assertions.assertThrows(OrderIdAlreadyInUseException.class, () -> orderService.createOrder(order1));

    }

    @Test
    void testOrderIsPaid_WhenOrderExistsAndIsPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Order paidOrder = new Order();
        paidOrder.setOrderID(orderID);
        paidOrder.setOrderPaid(true);

        Mockito.when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        Mockito.when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.of(paidOrder));

        boolean isPaid = orderService.orderIsPaid(orderID);

        Assertions.assertTrue(isPaid);
    }

    @Test
    void testOrderIsPaid_WhenOrderExistsAndIsNotPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Order unpaidOrder = new Order();
        unpaidOrder.setOrderID(orderID);
        unpaidOrder.setOrderPaid(false);

        Mockito.when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        Mockito.when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.of(unpaidOrder));

        boolean isPaid = orderService.orderIsPaid(orderID);

        Assertions.assertFalse(isPaid);
    }

    @Test
    void testOrderIsPaid_WhenOrderDoesNotExist() {
        UUID nonExistingOrderID = UUID.randomUUID();

        Mockito.when(orderRepository.existsByOrderID(nonExistingOrderID)).thenReturn(false);

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.orderIsPaid(nonExistingOrderID));
    }
}
