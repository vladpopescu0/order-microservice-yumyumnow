package nl.tudelft.sem.template.order.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.controllers.RestaurantController;
import nl.tudelft.sem.template.order.domain.user.DishService;
import nl.tudelft.sem.template.order.domain.user.OrderService;
import nl.tudelft.sem.template.order.domain.user.RestaurantService;
import nl.tudelft.sem.template.user.services.MockLocationService;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class SystemTests {

    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient ObjectMapper objectMapper;

    @Autowired
    private transient OrderService orderService;

    @Autowired
    private transient OrderController orderController;

    @Autowired
    private transient RestaurantController restaurantController;

    @Autowired
    private transient DishService dishService;

    @Autowired
    private transient RestaurantService restaurantService;

    @MockBean
    private transient UserMicroServiceService userMicroServiceService;

    @Mock
    transient MockLocationService mockLocationService;


    transient Order order1;

    transient String location;

    transient Dish dish1;
    transient Dish dish2;

    transient Address address;

    transient UUID vendorID;
    transient UUID customerID;
    transient UUID orderID;

    transient List<String> vendors;

    final transient String postGetPath = "/dish/{dishID}";
    final transient String orderPath = "/order";
    final transient String getRestaurantsPath = "/restaurants/{userID}";
    final transient String getRestaurantsWithQuery = "/restaurants/{userID}/{searchQuery}";
    final transient String addDishToOrder = "/order/{orderID}/addDishToOrder/{dishID}";
    final transient String removeDishFromOrder = "/order/{orderID}/removeDishFromOrder/{dishID}";
    final transient String getOrderByID = "/order/{orderID}";
    final transient String isPaidOrder = "/order/{orderID}/isPaid";
    final transient String orderStatusPath = "/order/{orderID}/status";
    final transient String orderForVendor = "/order/{orderID}/vendor";
    final transient String setRatingPath = "/order/{orderID}/orderRating";

    /** setup for tests.
     *
     */
    @BeforeEach
    public void setup() {
        order1 = new Order();
        orderID = UUID.randomUUID();
        dish1 = new Dish();
        dish2 = new Dish();
        vendorID = UUID.fromString("110e8400-e29b-41d4-a716-446655440000");
        customerID = UUID.randomUUID();
        final UUID dish1ID = UUID.randomUUID();
        final UUID dish2ID = UUID.randomUUID();

        dish1.addListOfAllergiesItem("fish");
        dish1.addListOfAllergiesItem("milk");
        dish1.addListOfIngredientsItem("fish");
        dish1.addListOfIngredientsItem("lemon");
        dish1.addListOfIngredientsItem("yoghurt");
        dish1.setDescription("fish with yoghurt yummy");
        dish1.setImage("cHVQR084e302LT11S0A+Q1Q/NlBeTm4k");
        dish1.setName("Fish and Yog");
        dish1.setVendorID(vendorID);
        dish1.setPrice(9.55f);
        dish1.setDishID(dish1ID);


        dish2.addListOfAllergiesItem("nuts");
        dish2.addListOfAllergiesItem("milk");
        dish2.addListOfIngredientsItem("cake");
        dish2.setDescription("tortilla cake with yummy sauce");
        dish2.setImage("Km1DLERRTGhfJ2Y8Xzo5Ril8dTh8ejQ/PSg=");
        dish2.setName("TortCake");
        dish2.setVendorID(vendorID);
        dish2.setPrice(12.39f);
        dish2.setDishID(dish2ID);

        order1.setOrderPaid(false);
        order1.setAddress(address);
        order1.setDate(BigDecimal.ONE);
        order1.setCustomerID(customerID);
        order1.setSpecialRequirements("none");
        order1.setStatus(Order.StatusEnum.PENDING);
        order1.setPrice(0.0f);
        order1.setRating(0);
        order1.setVendorID(vendorID);
        order1.setListOfDishes(new ArrayList<>());
        order1.setOrderID(orderID);

        address = new Address();
        address.setCity("Delft");
        address.setCity("Netherlands");
        address.setStreet("Mekelweg 5");
        address.setZip("2628CC");

        location = """
                {
                    "latitude": 51.998513,
                    "longitude": 4.37127
                },""";
        vendors = new ArrayList<>();
        vendors.add("""
                {
                  "userID": "550e8400-e29b-41d4-a716-446655440000",
                  "user": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "firstname": "John",
                    "surname": "James",
                    "email": "john@email.com",
                    "avatar": "www.avatar.com/avatar.png",
                    "password": "12345",
                    "verified": false,
                    "userType": "Customer"
                  },
                  "cuisineType": "chinese",
                  "location": {
                    "latitude": 51.998513,
                    "longitude": 4.37127
                  }
                }""");
        vendors.add("""
                {
                  "userID": "110e8400-e29b-41d4-a716-446655440000",
                  "user": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "firstname": "John",
                    "surname": "James",
                    "email": "john@email.com",
                    "avatar": "www.avatar.com/avatar.png",
                    "password": "12345",
                    "verified": false,
                    "userType": "Customer"
                  },
                  "cuisineType": "romanian",
                  "location": {
                    "latitude": 51.998514,
                    "longitude": 4.37127
                  }
                }""");
    }
    /**Stress testing the microservice by:
     * Add dish1 -> Add dish2 -> find all restaurants -> find all chinese restaurants ->
     * Create Order -> Add dishes to order -> Get Order -> Remove dish1 from order ->
     * update pay order -> update status -> retrieve order for vendor.
     */

    @Test
    public void addDishesCreateOrderAndDeleteOrder() throws Exception {

        Mockito.when(userMicroServiceService.checkVendorExists(vendorID)).thenReturn(true);
        Mockito.when(userMicroServiceService.checkUserExists(customerID)).thenReturn(true);
        Mockito.when(userMicroServiceService.getUserAddress(customerID)).thenReturn(address);

        Mockito.when(mockLocationService.convertAddressToGeoCoords(address)).thenReturn(List.of(51.998513, 4.37127));
        Mockito.when(userMicroServiceService.getAllVendors()).thenReturn(vendors.toString());

        mockMvc.perform(MockMvcRequestBuilders.post(postGetPath, dish1.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dish1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.post(postGetPath, dish2.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dish2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        MvcResult allRestaurants = mockMvc.perform(MockMvcRequestBuilders.get(getRestaurantsPath, customerID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        List<UUID> list = objectMapper.readValue(allRestaurants.getResponse().getContentAsString(), ArrayList.class);
        Assertions.assertEquals(2, list.size());
        Mockito.when(userMicroServiceService
                .getVendorsFromID(Mockito.anyList())).thenReturn(vendors);
        MvcResult allRestaurantsQuery = mockMvc
                .perform(MockMvcRequestBuilders
                        .get(getRestaurantsWithQuery, customerID, "chinese")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        List<UUID> listQuery = objectMapper.readValue(allRestaurantsQuery
                .getResponse().getContentAsString(), ArrayList.class);
        Assertions.assertEquals(1, listQuery.size());

        mockMvc.perform(MockMvcRequestBuilders.post(orderPath, order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.put(addDishToOrder, order1.getOrderID(), dish1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.put(addDishToOrder, order1.getOrderID(), dish2.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        MvcResult order1Return = mockMvc.perform(MockMvcRequestBuilders.get(getOrderByID, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        Order o1 = objectMapper.readValue(order1Return.getResponse().getContentAsString(), Order.class);
        Assertions.assertTrue(o1.getListOfDishes().get(0).equals(dish1.getDishID())
                || o1.getListOfDishes().get(0).equals(dish2.getDishID()));
        Assertions.assertTrue(o1.getListOfDishes().get(1).equals(dish1.getDishID())
                || o1.getListOfDishes().get(1).equals(dish2.getDishID()));
        mockMvc.perform(MockMvcRequestBuilders.put(removeDishFromOrder, order1.getOrderID(), dish1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put(isPaidOrder, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        Boolean isPaid = objectMapper.readValue(res.getResponse().getContentAsString(),
                new TypeReference<Order>() {}).getOrderPaid();
        Assertions.assertTrue(isPaid);
        String status = "preparing";
        mockMvc.perform(MockMvcRequestBuilders.put(orderStatusPath, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(status)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MvcResult orderVendorReturn = mockMvc.perform(MockMvcRequestBuilders.get(orderForVendor, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        Order orderVendor = objectMapper.readValue(orderVendorReturn.getResponse().getContentAsString(), Order.class);
        Assertions.assertEquals(orderVendor.getStatus(), Order.StatusEnum.PREPARING);
        Assertions.assertEquals(orderVendor.getListOfDishes().size(), 1);
    }
}
