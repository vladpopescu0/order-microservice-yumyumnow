package nl.tudelft.sem.template.order.domain.helpers;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.controllers.DishController;
import nl.tudelft.sem.template.order.controllers.OrderController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class OrderValidationTests {

    @Mock
    transient OrderController orderController;
    @Mock
    transient DishController dishController;
    @InjectMocks
    transient OrderValidation orderValidation;

    transient Order order1;
    transient Address a1;
    transient List<UUID> listOfDishes;
    transient Dish d1;
    transient Dish d2;

    @BeforeEach
    void setUp() {
        a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(UUID.randomUUID());
        order1.setCustomerID(UUID.fromString("fe6a470a-0f99-47e9-b580-ae051e095078"));
        order1.setAddress(a1);
        order1.setDate(new BigDecimal("1700006405000"));
        listOfDishes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        order1.setListOfDishes(listOfDishes);
        order1.setSpecialRequirements("Knock on the door");
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.PENDING);
        order1.setRating(4);

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
    void testAreAllDishesAvailable_allDishesAvailable() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<List<UUID>> responseDishes =
                new ResponseEntity<>(listOfDishes, HttpStatus.OK);
        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(d2, HttpStatus.OK);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);

        ResponseEntity<Boolean> response = orderValidation.areAllDishesAvailable(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(true, response.getBody());
    }

    @Test
    void testAreAllDishesAvailable_notAll() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(listOfDishes, HttpStatus.OK);
        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);

        ResponseEntity<Boolean> response = orderValidation.areAllDishesAvailable(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(false, response.getBody());
    }

    @Test
    void testAreAllDishesAvailable_noListOfDishes() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        ResponseEntity<Boolean> response = orderValidation.areAllDishesAvailable(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAreAllDishesAvailable_falseBody() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        ResponseEntity<Boolean> response = orderValidation.areAllDishesAvailable(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAreAllDishesAvailable_noListOfDishes_nullBody() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(null, HttpStatus.OK);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);

        ResponseEntity<Boolean> response = orderValidation.areAllDishesAvailable(orderId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAreAllDishesAvailable_throwsException() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<List<UUID>> responseDishes =
                new ResponseEntity<>(listOfDishes, HttpStatus.OK);
        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenThrow(NullPointerException.class);

        ResponseEntity<Boolean> response = orderValidation.areAllDishesAvailable(orderId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testIsOrderValid_validOrder() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Void> responseIsOrderPaid = new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(listOfDishes, HttpStatus.OK);
        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(d2, HttpStatus.OK);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);
        when(orderController.orderOrderIDIsPaidGet(orderId)).thenReturn(responseIsOrderPaid);

        ResponseEntity<Boolean> response = orderValidation.isOrderValid(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(true, response.getBody());
    }

    @Test
    void testIsOrderValid_notValidOrder_notPaid() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Void> responseIsOrderPaid = new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(listOfDishes, HttpStatus.OK);
        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(d2, HttpStatus.OK);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);
        when(orderController.orderOrderIDIsPaidGet(orderId)).thenReturn(responseIsOrderPaid);

        ResponseEntity<Boolean> response = orderValidation.isOrderValid(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(false, response.getBody());
    }

    @Test
    void testIsOrderValid_notValidOrder_notAvailableDishes() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Void> responseIsOrderPaid = new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(orderController.orderOrderIDIsPaidGet(orderId)).thenReturn(responseIsOrderPaid);

        ResponseEntity<Boolean> response = orderValidation.isOrderValid(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(false, response.getBody());
    }

    @Test
    void testIsOrderValid_notValidOrder_noDishes() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Void> responseIsOrderPaid = new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(listOfDishes, HttpStatus.OK);
        ResponseEntity<Dish> responseDish1 = new ResponseEntity<>(d1, HttpStatus.OK);
        ResponseEntity<Dish> responseDish2 = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(responseDish1);
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(responseDish2);
        when(orderController.orderOrderIDIsPaidGet(orderId)).thenReturn(responseIsOrderPaid);

        ResponseEntity<Boolean> response = orderValidation.isOrderValid(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(false, response.getBody());
    }

    @Test
    void testIsOrderValid_notValidOrder_noDishesBody() {
        UUID orderId = UUID.randomUUID();

        ResponseEntity<Void> responseIsOrderPaid = new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<List<UUID>> responseDishes = new ResponseEntity<>(null, HttpStatus.OK);

        when(orderController.getListOfDishes(orderId)).thenReturn(responseDishes);
        when(orderController.orderOrderIDIsPaidGet(orderId)).thenReturn(responseIsOrderPaid);

        ResponseEntity<Boolean> response = orderValidation.isOrderValid(orderId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(false, response.getBody());
    }

    @Test
    void testIsOrderValid_throwsException() {
        UUID orderId = UUID.randomUUID();

        when(orderController.orderOrderIDIsPaidGet(orderId)).thenThrow(NullPointerException.class);

        ResponseEntity<Boolean> response = orderValidation.isOrderValid(orderId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
