package nl.tudelft.sem.template.order.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.domain.user.*;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class VendorAnalyticsControllerIntegrationTests {
    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient DishService dishService;

    @Autowired
    private transient OrderService orderService;

    @Autowired
    private transient ObjectMapper objectMapper; // Used for converting Java objects to JSON

    @MockBean
    private transient UserMicroServiceService userMicroServiceService;

    transient Dish d1;

    transient Dish d2;

    transient Order order1;
    transient Order order2;
    transient String historyPath = "/vendor/{vendorID}/analytics/history/{customerID}";
    transient String popularItemPath = "/vendor/{vendorID}/analytics/popularItems";

    /**
     * setup for VendorAnalyticsControllerIntegrationTests.
     */
    @BeforeEach
    public void setup() {
        d1 = new Dish();
        d1.setDishID(UUID.randomUUID());
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
        d2.setDishID(d1.getVendorID());
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

        Address a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(d1.getVendorID());
        order1.setCustomerID(UUID.randomUUID());
        order1.setAddress(a1);
        order1.setDate(new BigDecimal("1700007405000"));
        order1.setListOfDishes(List.of(d1.getDishID(), d2.getDishID()));
        order1.setSpecialRequirements("Knock on the door");
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.DELIVERED);
        order1.setRating(4);

        Address a2 = new Address();
        a2.setStreet("Mekelweg 9");
        a2.setCity("Delft");
        a2.setCountry("Netherlands");
        a2.setZip("2628CD");

        order2 = new Order();
        order2.setOrderID(UUID.randomUUID());
        order2.setVendorID(d1.getVendorID());
        order2.setCustomerID(UUID.randomUUID());
        order2.setAddress(a1);
        order2.setDate(new BigDecimal("1790006416060"));
        order2.setListOfDishes(Collections.singletonList(d1.getDishID()));
        order2.setSpecialRequirements("Knock on the door");
        order2.setOrderPaid(true);
        order2.setStatus(Order.StatusEnum.ACCEPTED);
        order2.setRating(4);
    }

    @Transactional
    @Test
    public void get_orders_of_customer_from_vendor_non_existent_vendor() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        order2.setCustomerID(order1.getCustomerID());
        orderService.createOrder(order2);
        mockMvc.perform(MockMvcRequestBuilders.get(historyPath, UUID.randomUUID(), order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void get_orders_of_customer_from_vendor_non_existent_customer() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        order2.setCustomerID(order1.getCustomerID());
        orderService.createOrder(order2);
        mockMvc.perform(MockMvcRequestBuilders.get(historyPath, order1.getVendorID(), UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void get_orders_of_customer_from_vendor_no_results() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        order2.setVendorID(UUID.randomUUID());
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        orderService.createOrder(order2);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(historyPath, order1.getVendorID(), order2.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Order> returnedOrders = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Order>>() {});
        assertThat(returnedOrders.size()).isEqualTo(0);
        assertThat(returnedOrders).doesNotContain(order1).doesNotContain(order2);
    }

    @Transactional
    @Test
    public void get_orders_of_customer_from_vendor_one_result_different_vendor() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        order2.setCustomerID(order1.getCustomerID());
        order2.setVendorID(UUID.randomUUID());
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        orderService.createOrder(order2);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(historyPath, order1.getVendorID(), order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Order> returnedOrders = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Order>>() {});
        assertThat(returnedOrders.size()).isEqualTo(1);
        assertThat(returnedOrders).contains(order1).doesNotContain(order2);
    }

    @Transactional
    @Test
    public void get_orders_of_customer_from_vendor_one_result_different_customer() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        orderService.createOrder(order2);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(historyPath, order1.getVendorID(), order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Order> returnedOrders = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Order>>() {});
        assertThat(returnedOrders.size()).isEqualTo(1);
        assertThat(returnedOrders).contains(order1).doesNotContain(order2);
    }

    @Transactional
    @Test
    public void get_orders_of_customer_from_vendor_two_results() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        order2.setCustomerID(order1.getCustomerID());
        orderService.createOrder(order2);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(historyPath, order1.getVendorID(), order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Order> returnedOrders = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Order>>() {});
        assertThat(returnedOrders.size()).isEqualTo(2);
        assertThat(returnedOrders).contains(order1).contains(order2);
    }

    @Test
    @Transactional
    public void get_order_volume_from_vendor_two_orders() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        orderService.createOrder(order2);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders
                        .get("/vendor/{vendorID}/analytics/orderVolumes", order1.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        Integer volume = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<Integer>() {});
        assertThat(volume).isEqualTo(2);
    }

    @Test
    @Transactional
    public void get_order_volume_from_vendor_large_amount_of_orders() throws Exception {
        when(userMicroServiceService.checkVendorExists(any())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(any())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        UUID originalId = order1.getVendorID();
        for (int i = 0; i < 20; i++) {
            order1.setOrderID(UUID.randomUUID());
            orderService.createOrder(order1);
        }
        for (int i = 0; i < 15; i++) {
            order1.setOrderID(UUID.randomUUID());
            order1.setVendorID(UUID.randomUUID());
            orderService.createOrder(order1);
        }
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/vendor/{vendorID}/analytics/orderVolumes", originalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        Integer volume = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<Integer>() {});
        assertThat(volume).isEqualTo(20);
    }

    @Test
    @Transactional
    public void get_order_volume_from_vendor_where_vendor_does_not_exist() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        orderService.createOrder(order2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vendor/{vendorID}/analytics/orderVolumes", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    public void get_peak_times_vendor_does_not_exist() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        orderService.createOrder(order2);

        mockMvc.perform(MockMvcRequestBuilders.get("/vendor/{vendorID}/analytics/peakTimes", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
        This test contains testing using functionality of the method.
        This is not ideal, but I am not sure how else to approach it.
     */
    @Test
    @Transactional
    public void get_peak_times_large_test() throws Exception {
        when(userMicroServiceService.checkVendorExists(any())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(any())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        order2.setVendorID(UUID.randomUUID());
        int[] time = new int[24];
        for (int i = 0; i < 1000; i++) {
            Random r = new Random(); // pmd moment
            long cur = r.nextInt();
            order1.setOrderID(UUID.randomUUID());
            order1.setDate(BigDecimal.valueOf(cur));
            orderService.createOrder(order1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cur);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            time[hours] += 1;
            if (i % 10 == 0) {
                order2.setOrderID(UUID.randomUUID());
                order2.setDate(BigDecimal.valueOf(r.nextInt()));
                orderService.createOrder(order2);
            }
        }
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders
                        .get("/vendor/{vendorID}/analytics/peakTimes", order1.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Integer> volume = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Integer>>() {});
        assertThat(volume).isEqualTo(Arrays.stream(time).boxed().collect(Collectors.toList()));
    }

    @Test
    @Transactional
    public void get_popular_items_vendor_does_not_exist() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order1);
        orderService.createOrder(order2);

        mockMvc.perform(MockMvcRequestBuilders.get(popularItemPath, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Transactional
    public void get_popular_items_one_dish() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order2);
        order1.setVendorID(UUID.randomUUID());
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        orderService.createOrder(order1);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(popularItemPath, order2.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> popularItems = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(popularItems.size()).isEqualTo(1);
        assertThat(popularItems.get(0)).isEqualTo(d1);
    }

    @Test
    @Transactional
    public void get_popular_items_two_dishes() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order2);
        orderService.createOrder(order1);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(popularItemPath, order2.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> popularItems = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(popularItems.size()).isEqualTo(2);
        assertThat(popularItems.get(0)).isEqualTo(d1);
        assertThat(popularItems.get(1)).isEqualTo(d2);
    }

    @Test
    @Transactional
    public void get_popular_items_two_dishes_multiple_of_same_dish() throws Exception {
        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);
        orderService.createOrder(order2);
        order1.setListOfDishes(List.of(d2.getDishID(), d2.getDishID()));
        orderService.createOrder(order1);

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(popularItemPath, order2.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> popularItems = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(popularItems.size()).isEqualTo(2);
        assertThat(popularItems.get(0)).isEqualTo(d2);
        assertThat(popularItems.get(1)).isEqualTo(d1);
    }

}
