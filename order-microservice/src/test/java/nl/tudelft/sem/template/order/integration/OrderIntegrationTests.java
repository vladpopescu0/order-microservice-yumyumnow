package nl.tudelft.sem.template.order.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.controllers.OrderController;
import nl.tudelft.sem.template.order.domain.user.DishService;
import nl.tudelft.sem.template.order.domain.user.OrderService;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderIntegrationTests {
    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient ObjectMapper objectMapper; // Used for converting Java objects to JSON

    @Autowired
    private transient OrderService orderService;
    @Autowired
    private transient DishService dishService;
    @Autowired
    private transient OrderController orderController;

    @MockBean
    private transient UserMicroServiceService userMicroServiceService;

    transient Order order1;
    transient Order order2;
    transient Address a1;
    transient Address a2;
    transient Dish d1;
    transient Dish d2;
    transient String isPaidPath = "/order/{orderID}/isPaid";
    transient String orderPath = "/order";
    transient String getAllOrdersPath = "/order/all/{userID}";
    transient String orderIdPath = "/order/{orderId}";
    transient String editOrderPath = "/order/{orderID}/{userID}";
    transient String addDishToOrderPath = "/order/{orderID}/addDishToOrder/{dishID}";
    transient String removeDishFromOrderPath = "/order/{orderID}/removeDishFromOrder/{dishID}";
    transient String getOrderToVendor = "/order/{orderID}/vendor";
    transient String orderStatusPath = "/order/{orderID}/status";
    transient String dateString = "1700006405000";
    transient String specialRequirementsString = "Knock on the door";

    transient String adminJson;

    /**
     * setup for OrderIntegrationTests.
     *
     * @throws Exception if setup went wrong
     */
    @BeforeEach
    public void setup()  {
        a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        a2 = new Address();
        a2.setStreet("Carnegieplein 2");
        a2.setCity("Den Haag");
        a2.setCountry("Netherlands");
        a2.setZip("2517KJ");

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

        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(UUID.randomUUID());
        order1.setCustomerID(UUID.randomUUID());
        order1.setAddress(a1);
        order1.setDate(new BigDecimal(dateString));
        order1.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order1.setSpecialRequirements(specialRequirementsString);
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.ACCEPTED);
        order1.setRating(4);

        order2 = new Order();
        order2.setOrderID(UUID.randomUUID());
        order2.setVendorID(UUID.randomUUID());
        order2.setCustomerID(UUID.randomUUID());
        order2.setAddress(a2);
        order2.setDate(new BigDecimal("1700006405030"));
        order2.setListOfDishes(Arrays.asList(UUID.randomUUID()));
        order2.setSpecialRequirements("The bell doesn't work");
        order2.setOrderPaid(false);
        order2.setStatus(Order.StatusEnum.ACCEPTED);
        order2.setRating(3);
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
    }

    @Transactional
    @Test
    public void createOrderSuccessful() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post(orderPath, order1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order1))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Order order = orderService.getOrderById(order1.getOrderID());
        Assertions.assertEquals(order1, order);

    }

    @Transactional
    @Test
    public void createOrderDuplicateBadRequest() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post(orderPath, order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post(orderPath, order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Transactional
    @Test
    public void createOrderNullException() throws Exception {

        Order order = null;
        mockMvc.perform(MockMvcRequestBuilders.post(orderPath, order)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

    }

    @Transactional
    @Test
    public void getAllOrdersSuccessful() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        orderService.createOrder(order1);
        orderService.createOrder(order2);

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(any())).thenReturn(adminJson);

        MvcResult dbReturn = mockMvc.perform(MockMvcRequestBuilders.get(getAllOrdersPath, adminID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Order> allOrders = objectMapper.readValue(dbReturn.getResponse().getContentAsString(),
                new TypeReference<List<Order>>() {});
        Assertions.assertTrue(allOrders.contains(order1));
        Assertions.assertTrue(allOrders.contains(order2));
        Assertions.assertEquals(2, allOrders.size());
    }

    @Transactional
    @Test
    public void getAllOrdersNoOrdersFound() throws Exception {
        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(any())).thenReturn(adminJson);
        mockMvc.perform(MockMvcRequestBuilders.get(getAllOrdersPath, adminID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void getOrderByIdSuccessful() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        orderService.createOrder(order1);
        orderService.createOrder(order2);

        MvcResult order1Return = mockMvc.perform(MockMvcRequestBuilders.get(orderIdPath, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        MvcResult order2Return = mockMvc.perform(MockMvcRequestBuilders.get(orderIdPath, order2.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Order o1 = objectMapper.readValue(order1Return.getResponse().getContentAsString(), Order.class);
        Order o2 = objectMapper.readValue(order2Return.getResponse().getContentAsString(), Order.class);

        Assertions.assertEquals(order2, o2);
        Assertions.assertEquals(order1, o1);

    }

    @Transactional
    @Test
    public void getOrderByIdEmptyDb() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(orderIdPath, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Transactional
    @Test
    public void editOrderByIdSuccessful() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        Order orderToCompareTo = new Order();
        orderToCompareTo.setOrderID(order1.getOrderID());
        orderToCompareTo.setVendorID(order1.getVendorID());
        orderToCompareTo.setCustomerID(order1.getCustomerID());
        orderToCompareTo.setAddress(a1);
        orderToCompareTo.setDate(new BigDecimal(dateString));
        orderToCompareTo.setListOfDishes(order1.getListOfDishes());
        orderToCompareTo.setSpecialRequirements("Don't knock!");
        orderToCompareTo.setOrderPaid(true);
        orderToCompareTo.setStatus(Order.StatusEnum.ACCEPTED);
        orderToCompareTo.setRating(4);

        orderService.createOrder(order1);
        Order inDb = orderService.getOrderById(order1.getOrderID());
        Assertions.assertEquals(specialRequirementsString, inDb.getSpecialRequirements());

        order1.setSpecialRequirements("Don't knock!");

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        mockMvc.perform(MockMvcRequestBuilders.put(editOrderPath, order1.getOrderID(), adminID, order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Order editedOrder = orderService.getOrderById(order1.getOrderID());

        Assertions.assertEquals("Don't knock!", editedOrder.getSpecialRequirements());
        Assertions.assertEquals(orderToCompareTo, editedOrder);

    }

    @Transactional
    @Test
    public void editOrderByIdDifferingId() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        order1.setOrderPaid(false);

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        mockMvc.perform(MockMvcRequestBuilders.put(editOrderPath, UUID.randomUUID(), adminID, order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Transactional
    @Test
    public void editOrderByIdNotInDb() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        orderService.createOrder(order1);
        order1.setRating(1);

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        mockMvc.perform(MockMvcRequestBuilders.put(editOrderPath, order2.getOrderID(), adminID, order2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Transactional
    @Test
    public void deleteOrderByIdSuccessful() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        orderService.createOrder(order1);
        orderService.createOrder(order2);

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        mockMvc.perform(MockMvcRequestBuilders.delete(editOrderPath, order1.getOrderID(), adminID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult dbReturn = mockMvc.perform(MockMvcRequestBuilders.get(getAllOrdersPath, adminID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Order> allOrders = objectMapper.readValue(dbReturn.getResponse().getContentAsString(),
                new TypeReference<List<Order>>() {});
        Assertions.assertFalse(allOrders.contains(order1));
        Assertions.assertTrue(allOrders.contains(order2));
        Assertions.assertEquals(1, allOrders.size());

    }

    @Transactional
    @Test
    public void deleteOrderByIdOrderNotFound() throws Exception {
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        orderService.createOrder(order2);

        UUID adminID = UUID.randomUUID();
        when(userMicroServiceService.getUserInformation(adminID)).thenReturn(adminJson);

        mockMvc.perform(MockMvcRequestBuilders.delete(editOrderPath, order1.getOrderID(), adminID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void checkOrderIsPaidIsCorrect() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        mockMvc.perform(MockMvcRequestBuilders.get(isPaidPath, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void checkOrderIsPaidWrongParameter() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        UUID uuid = UUID.randomUUID();
        if (uuid.equals(order1.getOrderID())) {
            uuid = UUID.randomUUID();
        }
        mockMvc.perform(MockMvcRequestBuilders.get(isPaidPath, uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void checkOrderIsPaidNotPaid() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        order2.setOrderPaid(false);
        ResultActions resultActions2 = mockMvc.perform(post(orderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(isPaidPath, order2.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isPaymentRequired());
    }

    @Transactional
    @Test
    public void checkOrderPaidUpdate() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        order2.setOrderPaid(false);
        ResultActions resultActions2 = mockMvc.perform(post(orderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put(isPaidPath, order2.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        Boolean isPaid = objectMapper.readValue(res.getResponse().getContentAsString(),
                new TypeReference<Order>() {}).getOrderPaid();
        assertThat(isPaid).isTrue();
    }

    @Transactional
    @Test
    public void checkOrderPaidUpdateOrderWasPaid() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        order2.setOrderPaid(true);
        ResultActions resultActions2 = mockMvc.perform(post(orderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());


        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put(isPaidPath, order2.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        Boolean isPaid = objectMapper.readValue(res.getResponse().getContentAsString(),
                new TypeReference<Order>() {}).getOrderPaid();
        assertThat(isPaid).isFalse();
    }

    @Transactional
    @Test
    public void checkOrderPaidUpdateNotFound() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        order2.setOrderPaid(false);
        ResultActions resultActions2 = mockMvc.perform(post(orderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());

        UUID notExistent = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.put(isPaidPath, notExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void testGetHistoryOrdersOfUser() throws Exception {
        when(userMicroServiceService.checkVendorExists(any())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(any())).thenReturn(true);
        orderService.createOrder(order1);

        Order order3 = new Order();
        order3.setOrderID(UUID.randomUUID());
        order3.setVendorID(UUID.randomUUID());
        order3.setCustomerID(order1.getCustomerID());
        order3.setAddress(a1);
        order3.setDate(new BigDecimal(dateString));
        order3.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order3.setSpecialRequirements(specialRequirementsString);
        order3.setOrderPaid(true);
        order3.setStatus(Order.StatusEnum.DELIVERED);
        order3.setRating(4);

        Order order4 = new Order();
        order4.setOrderID(UUID.randomUUID());
        order4.setVendorID(UUID.randomUUID());
        order4.setCustomerID(order1.getCustomerID());
        order4.setAddress(a1);
        order4.setDate(new BigDecimal(dateString));
        order4.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order4.setSpecialRequirements(specialRequirementsString);
        order4.setOrderPaid(true);
        order4.setStatus(Order.StatusEnum.DELIVERED);
        order4.setRating(4);

        //Action
        ResultActions resultActions = mockMvc.perform(post(orderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order3)));
        // Assert
        resultActions.andExpect(status().isOk());

        //Action
        ResultActions resultActions2 = mockMvc.perform(post(orderPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order4)));
        // Assert
        resultActions2.andExpect(status().isOk());

        MvcResult res = mockMvc
                .perform(MockMvcRequestBuilders.get("/order/{customerID}/history", order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Order> orders = objectMapper.readValue(res.getResponse().getContentAsString(), new TypeReference<>() {});
        assertThat(orders).containsExactlyInAnyOrder(order3, order4);

    }

    @Transactional
    @Test
    public void testGetHistoryOrdersOfUserNoMatchingInDatabase() throws Exception {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        orderService.createOrder(order1);

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{customerID}/history", order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void testGetHistoryOrdersOfUserNoUserFound() throws Exception {
        UUID randomCustomerId = UUID.randomUUID();
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(randomCustomerId)).thenReturn(false);

        orderService.createOrder(order1);

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{customerID}/history", randomCustomerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void getStatusOfOrderSuccessful() throws Exception {

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        orderService.createOrder(order1);

        MvcResult ret = mockMvc.perform(MockMvcRequestBuilders.get(orderStatusPath, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = ret.getResponse().getContentAsString();
        Assertions.assertEquals("accepted", content);

    }

    @Transactional
    @Test
    public void getStatusOfOrderNotFound() throws Exception {

        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        orderService.createOrder(order2);

        mockMvc.perform(MockMvcRequestBuilders.get(orderStatusPath, order1.getOrderID())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }

    @Transactional
    @Test
    public void updateStatusOfOrderSuccessful() throws Exception {

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        orderService.createOrder(order1);

        String s1 = "DELIVERED";

        mockMvc.perform(MockMvcRequestBuilders.put(orderStatusPath, order1.getOrderID(), s1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String edit1 = orderService.getStatusOfOrderById(order1.getOrderID());
        Assertions.assertEquals("delivered", edit1);

        String s2 = "rejected";

        mockMvc.perform(MockMvcRequestBuilders.put(orderStatusPath, order1.getOrderID(), s2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s2)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String edit2 = orderService.getStatusOfOrderById(order1.getOrderID());
        Assertions.assertEquals("rejected", edit2);

        String s3 = "DELIVERED";

        mockMvc.perform(MockMvcRequestBuilders.put(orderStatusPath, order1.getOrderID(), s3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String edit3 = orderService.getStatusOfOrderById(order1.getOrderID());
        Assertions.assertEquals("delivered", edit3);

    }

    @Transactional
    @Test
    public void updateStatusOfOrderNotFound() throws Exception {

        when(userMicroServiceService.checkVendorExists(order2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order2.getCustomerID())).thenReturn(true);

        orderService.createOrder(order2);

        String s1 = "DELIVERED";

        mockMvc.perform(MockMvcRequestBuilders.put(orderStatusPath, order1.getOrderID(), s1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

    }

    @Transactional
    @Test
    public void updateStatusOfOrderInvalidStatus() throws Exception {

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        orderService.createOrder(order1);

        String s1 = "not_a_status";

        mockMvc.perform(MockMvcRequestBuilders.put(orderStatusPath, order1.getOrderID(), s1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(s1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnsupportedMediaType())
                .andReturn();

    }

    @Test
    @Transactional
    public void addDishToOrder_successfully() throws Exception {

        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        
        dishService.addDish(d1);
        dishService.addDish(d2);
        
        order1.setListOfDishes(List.of(d1.getDishID()));
        orderService.createOrder(order1);
        
        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders
                        .put(addDishToOrderPath, order1.getOrderID(), d2.getDishID())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Order add1 = objectMapper.readValue(result1.getResponse().getContentAsString(), Order.class);
        Assertions.assertEquals(List.of(d1.getDishID(), d2.getDishID()), add1.getListOfDishes());

        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders
                        .put(addDishToOrderPath, order1.getOrderID(), d2.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Order add2 = objectMapper.readValue(result2.getResponse().getContentAsString(), Order.class);
        Assertions.assertEquals(List.of(d1.getDishID(), d2.getDishID(), d2.getDishID()), add2.getListOfDishes());

    }

    @Test
    @Transactional
    public void addDishToOrder_dishNotFound() throws Exception {

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        order1.setListOfDishes(List.of(d1.getDishID()));
        orderService.createOrder(order1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(addDishToOrderPath, order1.getOrderID(), d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Assertions.assertEquals(404, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void addDishToOrder_invalidDishID() throws Exception {

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        order1.setListOfDishes(List.of(d1.getDishID()));
        orderService.createOrder(order1);

        String dishID = "dishID";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(addDishToOrderPath, order1.getOrderID(), dishID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertEquals(400, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void addDishToOrder_invalidOrderID() throws Exception {

        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);

        dishService.addDish(d1);

        String orderID = "orderID";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(addDishToOrderPath, orderID, d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertEquals(400, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void addDishToOrder_orderNotFound() throws Exception {

        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);

        dishService.addDish(d1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(addDishToOrderPath, order1.getOrderID(), d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Assertions.assertEquals(404, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void removeDishFromOrder_successfully() throws Exception {

        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);

        order1.setListOfDishes(List.of(d1.getDishID(), d2.getDishID()));
        orderService.createOrder(order1);

        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders
                        .put(removeDishFromOrderPath, order1.getOrderID(), d2.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Order add1 = objectMapper.readValue(result1.getResponse().getContentAsString(), Order.class);
        Assertions.assertEquals(List.of(d1.getDishID()), add1.getListOfDishes());

        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders
                        .put(removeDishFromOrderPath, order1.getOrderID(), d2.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Order add2 = objectMapper.readValue(result2.getResponse().getContentAsString(), Order.class);
        Assertions.assertEquals(List.of(d1.getDishID()), add2.getListOfDishes());

        MvcResult result3 = mockMvc.perform(MockMvcRequestBuilders
                        .put(removeDishFromOrderPath, order1.getOrderID(), d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Order add3 = objectMapper.readValue(result3.getResponse().getContentAsString(), Order.class);
        Assertions.assertEquals(List.of(), add3.getListOfDishes());

    }

    @Test
    @Transactional
    public void removeDishFromOrder_orderNotFound() throws Exception {

        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(removeDishFromOrderPath, order1.getOrderID(), d2.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Assertions.assertEquals(404, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void removeDishFromOrder_dishNotFound() throws Exception {

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        order1.setListOfDishes(List.of(d1.getDishID()));
        orderService.createOrder(order1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(removeDishFromOrderPath, order1.getOrderID(), d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Assertions.assertEquals(404, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void removeDishFromOrder_invalidDishId() throws Exception {

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        order1.setListOfDishes(List.of(d1.getDishID()));
        orderService.createOrder(order1);
        String dishID = "dishID";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(removeDishFromOrderPath, order1.getOrderID(), dishID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertEquals(400, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void removeDishFromOrder_invalidOrderID() throws Exception {

        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);

        dishService.addDish(d2);

        String orderID = "orderID";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .put(removeDishFromOrderPath, orderID, d2.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assertions.assertEquals(400, result.getResponse().getStatus());

    }

    @Test
    @Transactional
    public void getOrderToVendor_successfully() throws Exception {

        when(userMicroServiceService.checkVendorExists(d1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(d2.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        dishService.addDish(d1);
        dishService.addDish(d2);

        order1.setListOfDishes(List.of(d1.getDishID(), d2.getDishID()));
        order1.setOrderPaid(false);
        orderService.createOrder(order1);

        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders
                        .get(getOrderToVendor, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        Assertions.assertEquals(404, result1.getResponse().getStatus());

        orderService.orderIsPaidUpdate(order1.getOrderID());

        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders
                        .get(getOrderToVendor, order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        order1.setOrderPaid(true);
        Order order = objectMapper.readValue(result2.getResponse().getContentAsString(), Order.class);
        Assertions.assertEquals(200, result2.getResponse().getStatus());
        Assertions.assertEquals(order1, order);

    }

}