package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.order.commons.Address;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.controllers.DishController;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.controllers.VendorAnalyticsController;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class VendorAnalyticsControllerTests {

    @Mock
    private OrderController orderController;
    @Mock
    private DishController dishController;

    @InjectMocks
    private VendorAnalyticsController vendorAnalyticsController;

    Order order1;
    Address a1;
    Dish dish1;
    Dish dish2;
    List<UUID> listOfDishes;

    @BeforeEach
    void setUp() {
        a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        listOfDishes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        dish1 = new Dish();
        dish1.setDishID(listOfDishes.get(0));
        dish1.setDescription("very tasty");
        dish1.setImage("img");
        dish1.setName("Pizza");
        dish1.setPrice(5.0f);
        List<String> allergies = new ArrayList<>();
        allergies.add("lactose");
        dish1.setListOfAllergies(allergies);
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Cheese");
        ingredients.add("Salami");
        ingredients.add("Tomato Sauce");
        dish1.setListOfIngredients(ingredients);
        dish1.setVendorID(UUID.randomUUID());

        dish2 = new Dish();
        dish2.setDishID(listOfDishes.get(1));
        dish2.setDescription("very tasty");
        dish2.setImage("img");
        dish2.setName("Lasagna");
        dish2.setPrice(10.0f);
        List<String> allergies2 = new ArrayList<>();
        allergies2.add("lactose");
        allergies2.add("gluten");
        dish2.setListOfAllergies(allergies2);
        List<String> ingredients2 = new ArrayList<>();
        ingredients2.add("Gluten");
        ingredients2.add("Cheese");
        ingredients2.add("Tomato Sauce");
        dish2.setListOfIngredients(ingredients2);
        dish2.setVendorID(UUID.randomUUID());

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
    void testGetOrderEarnings() throws Exception {
        UUID orderID = UUID.randomUUID();
        ResponseEntity<List<UUID>> respList = new ResponseEntity<>(listOfDishes, HttpStatus.OK);

        Mockito.when(orderController.getListOfDishes(orderID)).thenReturn(respList);

        Mockito.when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(new ResponseEntity<>(dish1, HttpStatus.OK));
        Mockito.when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(new ResponseEntity<>(dish2, HttpStatus.OK));

        ResponseEntity<Float> response = vendorAnalyticsController.getOrderEarnings(orderID);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(15.0f, response.getBody());
    }

}
