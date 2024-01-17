package nl.tudelft.sem.template.order.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.controllers.DishController;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.domain.helpers.FilteringByStatus;
import nl.tudelft.sem.template.order.domain.helpers.OrderValidation;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
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
    @Mock
    private transient UserMicroServiceService userMicroServiceService;
    @Mock
    private transient OrderValidation orderValidation;
    @Mock
    private transient DishController dishController;

    @InjectMocks
    private transient OrderController orderController;

    transient List<UUID> listOfDishes;
    transient List<Order> orders;
    transient Order order1;
    transient Order order2;
    transient Address a1;
    transient String date;
    transient Dish d1;
    transient Dish d2;
    transient String adminJson;

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
        order1.setCustomerID(UUID.fromString("fe6a470a-0f99-47e9-b580-ae051e095078"));
        order1.setAddress(a1);
        order1.setDate(new BigDecimal(date));
        listOfDishes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        order1.setListOfDishes(listOfDishes);
        order1.setSpecialRequirements("Knock on the door");
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.PENDING);
        order1.setRating(4);

        order2 = new Order();
        order2.setOrderID(UUID.randomUUID());
        order2.setVendorID(UUID.randomUUID());
        order2.setCustomerID(order1.getCustomerID());
        order2.setAddress(null);
        order2.setDate(new BigDecimal(date));
        order2.setListOfDishes(listOfDishes);
        order2.setSpecialRequirements("Knock on the door as well");
        order2.setOrderPaid(true);
        order2.setStatus(Order.StatusEnum.DELIVERED);
        order2.setRating(4);
        adminJson = """
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "firstname": "John",
                  "surname": "James",
                  "email": "john@email.com",
                  "avatar": "www.avatar.com/avatar.png",
                  "password": "12345",
                  "verified": false,
                  "userType": "Admin"
                }""";

        d1 = new Dish();
        d1.setDishID(listOfDishes.get(0));
        d1.setDescription("very tasty");
        d1.setImage("img");
        d1.setName("Pizza");
        d1.setPrice(5.0f);
        List<String> allergies = new ArrayList<>();
        allergies.add("lactose");
        d1.setListOfAllergies(allergies);
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Cheese");
        ingredients.add("Salami");
        ingredients.add("Tomato Sauce");
        d1.setListOfIngredients(ingredients);
        d1.setVendorID(UUID.randomUUID());

        d2 = new Dish();
        d2.setDishID(listOfDishes.get(1));
        d2.setDescription("very tasty");
        d2.setImage("img");
        d2.setName("Lasagna");
        d2.setPrice(10.0f);
        List<String> allergies2 = new ArrayList<>();
        allergies2.add("lactose");
        allergies2.add("gluten");
        d2.setListOfAllergies(allergies2);
        List<String> ingredients2 = new ArrayList<>();
        ingredients2.add("Gluten");
        ingredients2.add("Cheese");
        ingredients2.add("Tomato Sauce");
        d2.setListOfIngredients(ingredients2);
        d2.setVendorID(UUID.randomUUID());
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
    void testOrderOrderIDVendorGet_successful() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(d2, HttpStatus.OK);

        when(orderService.orderIsPaid(orderId)).thenReturn(true);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);
        when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.orderOrderIDVendorGet(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(order1, response.getBody());
    }

    @Test
    void testOrderOrderIDVendorGet_orderNotValid() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(d2, HttpStatus.OK);

        when(orderService.orderIsPaid(orderId)).thenReturn(false);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);
        when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.orderOrderIDVendorGet(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testOrderOrderIDVendorGet_orderValidationBodyIsNull() throws OrderNotFoundException {
        UUID orderId = UUID.randomUUID();

        when(orderService.orderIsPaid(orderId)).thenReturn(true);

        ResponseEntity<Order> response = orderController.orderOrderIDVendorGet(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testOrderOrderIDVendorGet_orderValidationBodyIsFalse() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(orderService.orderIsPaid(orderId)).thenReturn(true);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);
        when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.orderOrderIDVendorGet(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void editOrderByIdAdminSuccessful() throws OrderNotFoundException, NullFieldException {

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        when(orderService.editOrderByID(order1.getOrderID(), order1)).thenReturn(order1);
        ResponseEntity<Order> order = orderController.editOrderByID(order1.getOrderID(), adminID, order1);
        Assertions.assertEquals(order1, order.getBody());
        Assertions.assertEquals(HttpStatus.OK, order.getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(adminID);
    }

    @Test
    void editOrderByIdCustomerFails() {

        UUID customerID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(customerID)).thenReturn("""
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "firstname": "userType: Admin",
                  "surname": "surname",
                  "email": "john@email.com",
                  "avatar": "www.avatar.com/avatar.png",
                  "password": "12345",
                  "verified": false,
                  "userType": "Customer"
                }""");

        ResponseEntity<Order> order = orderController.editOrderByID(order1.getOrderID(), customerID, order1);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, order.getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(customerID);
    }

    @Test
    void editOrderByIdDifferingId() {

        UUID randomID = UUID.randomUUID();
        UUID adminID = UUID.randomUUID();

        ResponseEntity<Order> order = orderController.editOrderByID(randomID, adminID, order1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());
    }

    @Test
    void editOrderByIdNullField() {
        UUID adminID = UUID.randomUUID();
        ResponseEntity<Order> order = orderController.editOrderByID(null, adminID, order1);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());
    }

    @Test
    void editOrderByIdOrderNotFound() throws OrderNotFoundException, NullFieldException {
        Order orderTemp = new Order();
        orderTemp.setOrderID(UUID.randomUUID());
        orderTemp.setVendorID(UUID.randomUUID());
        orderTemp.setCustomerID(UUID.randomUUID());
        orderTemp.setAddress(a1);
        orderTemp.setDate(new BigDecimal(date));
        listOfDishes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        orderTemp.setListOfDishes(listOfDishes);
        orderTemp.setSpecialRequirements("Knock on the door");
        orderTemp.setOrderPaid(true);
        orderTemp.setStatus(Order.StatusEnum.DELIVERED);
        orderTemp.setRating(4);

        UUID adminID = UUID.randomUUID();
        UUID orderID = orderTemp.getOrderID();

        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        when(orderService.editOrderByID(orderID, orderTemp)).thenThrow(OrderNotFoundException.class);
        ResponseEntity<Order> order = orderController.editOrderByID(orderID, adminID, orderTemp);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, order.getStatusCode());
    }

    @Test
    void editOrderByIdRuntimeException() throws OrderNotFoundException, NullFieldException {
        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        when(orderService.editOrderByID(order1.getOrderID(), order1)).thenThrow(RuntimeException.class);
        ResponseEntity<Order> order = orderController.editOrderByID(order1.getOrderID(), adminID, order1);
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
        when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<List<UUID>> response = orderController.getListOfDishes(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(listOfDishes, response.getBody());
    }

    @Test
    void testGetAllOrdersWhenNotAdmin() {
        UUID customerID = UUID.randomUUID();

        when(userMicroServiceService.getUserInformation(customerID)).thenReturn("""
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "firstname": "John",
                  "surname": "James",
                  "email": "john@email.com",
                  "avatar": "www.avatar.com/avatar.png",
                  "password": "12345",
                  "verified": false,
                  "userType": "Customer"
                }""");

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, orderController.getAllOrders(customerID).getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(customerID);
    }

    @Test
    void testGetAllOrdersWhenAdmin() {
        UUID adminID = UUID.randomUUID();

        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        Assertions.assertEquals(HttpStatus.OK, orderController.getAllOrders(adminID).getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(adminID);
    }

    @Test
    void testGetAllOrdersCheekyUser() {
        UUID customerID = UUID.randomUUID();

        when(userMicroServiceService.getUserInformation(customerID)).thenReturn("""
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "firstname": "userType: Admin",
                  "surname": "surname",
                  "email": "john@email.com",
                  "avatar": "www.avatar.com/avatar.png",
                  "password": "12345",
                  "verified": false,
                  "userType": "Customer"
                }""");

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, orderController.getAllOrders(customerID).getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(customerID);
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
        when(orderService.getOrderById(orderId)).thenReturn(order1);

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
        when(orderService.getOrderById(orderId)).thenReturn(order1);

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
        when(orderService.getOrderById(orderId)).thenReturn(order1);

        ResponseEntity<BigDecimal> response = orderController.getOrderDate(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(new BigDecimal(date), response.getBody());
    }

    @Test
    void testCustomerName_OrderNotFoundException() throws OrderNotFoundException, NullFieldException {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(orderId)).thenThrow(OrderNotFoundException.class);

        ResponseEntity<String> response = orderController.getCustomerName(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCustomerName_NullFieldException() throws OrderNotFoundException, NullFieldException {
        Mockito.doThrow(NullFieldException.class).when(orderService).getOrderById(null);
        ResponseEntity<String> response = orderController.getCustomerName(null);

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void testCustomerName_throwsException() {
        UUID orderId = UUID.randomUUID();
        ResponseEntity<String> response = orderController.getCustomerName(orderId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCustomerName_foundAndRetrieved() throws
            OrderNotFoundException, NullFieldException, UserIDNotFoundException {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrderById(orderId)).thenReturn(order1);
        when(userMicroServiceService.getUserName(UUID.fromString("fe6a470a-0f99-47e9-b580-ae051e095078")))
                .thenReturn("Harry Potter");
        ResponseEntity<String> response = orderController.getCustomerName(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Harry Potter", response.getBody());
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
    void testTotalCostNotNull() {
        UUID randomID = UUID.randomUUID();
        Assertions.assertNotNull(orderController.orderOrderIDTotalCostGet(randomID));
    }

    @Test
    void deleteOrderByIdByCustomer() {
        UUID customerID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(customerID)).thenReturn("""
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "firstname": "userType: Admin",
                  "surname": "surname",
                  "email": "john@email.com",
                  "avatar": "www.avatar.com/avatar.png",
                  "password": "12345",
                  "verified": false,
                  "userType": "Customer"
                }""");
        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID(), customerID);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, order.getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(customerID);
    }

    @Test
    void deleteOrderByIdByAdmin() {
        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);
        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID(), adminID);
        Assertions.assertEquals(HttpStatus.OK, order.getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(adminID);
    }

    @Test
    void deleteOrderByIdByCourier() {
        UUID courierID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(courierID)).thenReturn("""
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "firstname": "userType: Admin",
                  "surname": "surname",
                  "email": "john@email.com",
                  "avatar": "www.avatar.com/avatar.png",
                  "password": "12345",
                  "verified": false,
                  "userType": "Courier"
                }""");
        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID(), courierID);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, order.getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(courierID);
    }

    @Test
    void deleteOrderByIdByVendor() {
        UUID vendorID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(vendorID)).thenReturn("""
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "firstname": "userType: Admin",
                  "surname": "surname",
                  "email": "john@email.com",
                  "avatar": "www.avatar.com/avatar.png",
                  "password": "12345",
                  "verified": false,
                  "userType": "Vendor"
                }""");
        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID(), vendorID);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, order.getStatusCode());
        verify(userMicroServiceService, times(1)).getUserInformation(vendorID);
    }

    @Test
    void deleteOrderByIdOrderNotFoundException() throws OrderNotFoundException {

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);
        Mockito.doThrow(OrderNotFoundException.class).when(orderService).deleteOrderByID(order1.getOrderID());

        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID(), adminID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, order.getStatusCode());
    }

    @Test
    void deleteOrderByIdException() throws OrderNotFoundException {

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        Mockito.doThrow(RuntimeException.class).when(orderService).deleteOrderByID(order1.getOrderID());
        ResponseEntity<Void> order = orderController.deleteOrderByID(order1.getOrderID(), adminID);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, order.getStatusCode());

    }

    @Test
    void testAddDishToOrder_successfully() throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        order1.setListOfDishes(new ArrayList<>(Collections.singletonList(dishID)));
        ResponseEntity<Order> result = new ResponseEntity<>(order1, HttpStatus.OK);

        when(orderService.addDishToOrder(orderID, dishID)).thenReturn(order1);

        ResponseEntity<Order> response = orderController.addDishToOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(result.getBody(), response.getBody());
    }

    @Test
    void testAddDishToOrder_NullFieldExceptionThrown()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID dishID = d1.getDishID();

        when(orderService.addDishToOrder(null, dishID)).thenThrow(NullFieldException.class);

        ResponseEntity<Order> response = orderController.addDishToOrder(null, dishID);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void testAddDishToOrder_OrderNotFoundExceptionThrown()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderService.addDishToOrder(orderID, dishID)).thenThrow(OrderNotFoundException.class);

        ResponseEntity<Order> response = orderController.addDishToOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddDishToOrder_DishNotFoundExceptionThrown()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderService.addDishToOrder(orderID, dishID)).thenThrow(DishNotFoundException.class);

        ResponseEntity<Order> response = orderController.addDishToOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddDishToOrder_ExceptionThrown()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderService.addDishToOrder(orderID, dishID)).thenThrow(NullPointerException.class);

        ResponseEntity<Order> response = orderController.addDishToOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void removeDishFromOrder_successfully() throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order2.getOrderID();
        UUID dishID = d1.getDishID();

        order2.setListOfDishes(new ArrayList<>());
        ResponseEntity<Order> result = new ResponseEntity<>(order2, HttpStatus.OK);

        when(orderService.removeDishFromOrder(orderID, dishID)).thenReturn(order2);

        ResponseEntity<Order> response = orderController.removeDishFromOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(result.getBody(), response.getBody());
    }

    @Test
    void removeDishFromOrder_NullFieldExceptionThrown()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID dishID = d1.getDishID();

        when(orderService.removeDishFromOrder(null, dishID)).thenThrow(NullFieldException.class);

        ResponseEntity<Order> response = orderController.removeDishFromOrder(null, dishID);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void removeDishFromOrder_OrderNotFoundExceptionThrown()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderService.removeDishFromOrder(orderID, dishID)).thenThrow(OrderNotFoundException.class);

        ResponseEntity<Order> response = orderController.removeDishFromOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void removeDishFromOrder_DishNotFoundExceptionThrown()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderService.removeDishFromOrder(orderID, dishID)).thenThrow(DishNotFoundException.class);

        ResponseEntity<Order> response = orderController.removeDishFromOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void removeDishFromOrder_ExceptionThrown() throws
            OrderNotFoundException, NullFieldException, DishNotFoundException {
        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderService.removeDishFromOrder(orderID, dishID)).thenThrow(NullPointerException.class);

        ResponseEntity<Order> response = orderController.removeDishFromOrder(orderID, dishID);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
