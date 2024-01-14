package nl.tudelft.sem.template.order.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
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
import nl.tudelft.sem.template.order.controllers.VendorAnalyticsController;
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
public class VendorAnalyticsControllerTests {

    @Mock
    private OrderController orderController;

    @Mock
    private OrderService orderService;
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

        when(orderController.getListOfDishes(orderID)).thenReturn(respList);

        when(dishController.getDishByID(listOfDishes.get(0))).thenReturn(new ResponseEntity<>(dish1, HttpStatus.OK));
        when(dishController.getDishByID(listOfDishes.get(1))).thenReturn(new ResponseEntity<>(dish2, HttpStatus.OK));

        ResponseEntity<Float> response = vendorAnalyticsController.getOrderEarnings(orderID);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(15.0f, response.getBody());
    }

    @Test
    void get_customer_history_vendor_not_found() throws Exception, CustomerNotFoundException {
        when(orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), order1.getCustomerID())).thenThrow(VendorNotFoundException.class);

        ResponseEntity<List<Order>> response = vendorAnalyticsController.vendorVendorIDAnalyticsHistoryCustomerIDGet(order1.getVendorID(), order1.getCustomerID());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void get_customer_history_customer_not_found() throws Exception, CustomerNotFoundException {
        when(orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), order1.getCustomerID())).thenThrow(CustomerNotFoundException.class);

        ResponseEntity<List<Order>> response = vendorAnalyticsController.vendorVendorIDAnalyticsHistoryCustomerIDGet(order1.getVendorID(), order1.getCustomerID());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void get_customer_history_no_order() throws Exception, CustomerNotFoundException {
        when(orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), order1.getCustomerID())).thenThrow(NoOrdersException.class);

        ResponseEntity<List<Order>> response = vendorAnalyticsController.vendorVendorIDAnalyticsHistoryCustomerIDGet(order1.getVendorID(), order1.getCustomerID());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(new ArrayList<>());
    }

    @Test
    void get_customer_history_unexpected_exception() throws Exception, CustomerNotFoundException {
        when(orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), order1.getCustomerID())).thenThrow(NullPointerException.class);

        ResponseEntity<List<Order>> response = vendorAnalyticsController.vendorVendorIDAnalyticsHistoryCustomerIDGet(order1.getVendorID(), order1.getCustomerID());

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void get_customer_history_proper_request() throws Exception, CustomerNotFoundException {
        when(orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), order1.getCustomerID())).thenReturn(List.of(order1));

        ResponseEntity<List<Order>> response = vendorAnalyticsController.vendorVendorIDAnalyticsHistoryCustomerIDGet(order1.getVendorID(), order1.getCustomerID());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(List.of(order1));
    }

    @Test
    void get_vendor_order_volume_vendor_not_found() throws Exception {
        when(orderService.getOrderVolume(order1.getVendorID())).thenThrow(VendorNotFoundException.class);

        ResponseEntity<Integer> response = vendorAnalyticsController.vendorVendorIDAnalyticsOrderVolumesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void get_vendor_order_volume_no_orders() throws Exception {
        when(orderService.getOrderVolume(order1.getVendorID())).thenThrow(NoOrdersException.class);

        ResponseEntity<Integer> response = vendorAnalyticsController.vendorVendorIDAnalyticsOrderVolumesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(0);
    }

    @Test
    void get_vendor_order_volume_unexpected_error() throws Exception {
        when(orderService.getOrderVolume(order1.getVendorID())).thenThrow(NullPointerException.class);

        ResponseEntity<Integer> response = vendorAnalyticsController.vendorVendorIDAnalyticsOrderVolumesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void get_vendor_order_volume_proper_request() throws Exception {
        when(orderService.getOrderVolume(order1.getVendorID())).thenReturn(20);

        ResponseEntity<Integer> response = vendorAnalyticsController.vendorVendorIDAnalyticsOrderVolumesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(20);
    }

    @Test
    void get_vendor_popular_items_vendor_not_found() throws Exception {
        when(orderService.getDishesSortedByVolume(order1.getVendorID())).thenThrow(VendorNotFoundException.class);

        ResponseEntity<List<Dish>> response = vendorAnalyticsController.vendorVendorIDAnalyticsPopularItemsGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void get_vendor_popular_items_unexpected_exception() throws Exception {
        when(orderService.getDishesSortedByVolume(order1.getVendorID())).thenThrow(NullPointerException.class);

        ResponseEntity<List<Dish>> response = vendorAnalyticsController.vendorVendorIDAnalyticsPopularItemsGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void get_vendor_popular_items_proper_request() throws Exception {
        when(orderService.getDishesSortedByVolume(order1.getVendorID())).thenReturn(List.of(dish2, dish1));

        ResponseEntity<List<Dish>> response = vendorAnalyticsController.vendorVendorIDAnalyticsPopularItemsGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(2);
        assertThat(response.getBody().get(0)).isEqualTo(dish2);
        assertThat(response.getBody().get(1)).isEqualTo(dish1);
    }

    @Test
    void get_vendor_peak_times_vendor_not_found() throws Exception {
        when(orderService.getOrderVolumeByTime(order1.getVendorID())).thenThrow(VendorNotFoundException.class);

        ResponseEntity<List<Integer>> response = vendorAnalyticsController.vendorVendorIDAnalyticsPeakTimesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void get_vendor_peak_times_no_orders() throws Exception {
        when(orderService.getOrderVolumeByTime(order1.getVendorID())).thenThrow(NoOrdersException.class);

        ResponseEntity<List<Integer>> response = vendorAnalyticsController.vendorVendorIDAnalyticsPeakTimesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Integer> res = new ArrayList<>();
        while (res.size() < 24) {
            res.add(0);
        }
        assertThat(response.getBody()).isEqualTo(res);
    }

    @Test
    void get_vendor_peak_times_unexpected_error() throws Exception {
        when(orderService.getOrderVolumeByTime(order1.getVendorID())).thenThrow(NullPointerException.class);

        ResponseEntity<List<Integer>> response = vendorAnalyticsController.vendorVendorIDAnalyticsPeakTimesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void get_vendor_peak_times_proper_request() throws Exception {
        List<Integer> res = new ArrayList<>();
        while (res.size() < 24) {
            res.add(0);
        }
        res.set(2, 10);
        res.set(9, 20);
        res.set(7, 2);
        res.set(0, 1);
        res.set(23, 7);
        when(orderService.getOrderVolumeByTime(order1.getVendorID())).thenReturn(res);

        ResponseEntity<List<Integer>> response = vendorAnalyticsController.vendorVendorIDAnalyticsPeakTimesGet(order1.getVendorID());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(res);
    }
}
