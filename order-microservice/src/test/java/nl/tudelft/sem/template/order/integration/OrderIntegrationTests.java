package nl.tudelft.sem.template.order.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.order.commons.Address;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.OrderService;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderIntegrationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper; // Used for converting Java objects to JSON

    @Autowired
    private transient OrderService orderService;

    Order order1;
    Order order2;
    Address a1;
    Address a2;

    @BeforeEach
    public void setup() throws Exception {
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

    }

    @Transactional
    @Test
    public void createOrderSuccessful() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/order", order1)
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

        mockMvc.perform(MockMvcRequestBuilders.post("/order", order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/order", order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Transactional
    @Test
    public void createOrderNullException() throws Exception {

        Order order = null;
        mockMvc.perform(MockMvcRequestBuilders.post("/order", order)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

    }

    @Transactional
    @Test
    public void getAllOrdersSuccessful() throws Exception {

        orderService.createOrder(order1);
        orderService.createOrder(order2);

        MvcResult dbReturn = mockMvc.perform(MockMvcRequestBuilders.get("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Order> allOrders = objectMapper.readValue(dbReturn.getResponse().getContentAsString(), new TypeReference<List<Order>>() {});
        Assertions.assertTrue(allOrders.contains(order1));
        Assertions.assertTrue(allOrders.contains(order2));
        Assertions.assertEquals(2, allOrders.size());

    }

    @Transactional
    @Test
    public void getAllOrdersNoOrdersFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void getOrderByIdSuccessful() throws Exception {

        orderService.createOrder(order1);
        orderService.createOrder(order2);

        MvcResult order1Return = mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}", order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        MvcResult order2Return = mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}", order2.getOrderID())
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
        mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}", order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Transactional
    @Test
    public void editOrderByIdSuccessful() throws Exception {

        Order orderToCompareTo = new Order();
        orderToCompareTo.setOrderID(order1.getOrderID());
        orderToCompareTo.setVendorID(order1.getVendorID());
        orderToCompareTo.setCustomerID(order1.getCustomerID());
        orderToCompareTo.setAddress(a1);
        orderToCompareTo.setDate(new BigDecimal("1700006405000"));
        orderToCompareTo.setListOfDishes(order1.getListOfDishes());
        orderToCompareTo.setSpecialRequirements("Don't knock!");
        orderToCompareTo.setOrderPaid(true);
        orderToCompareTo.setStatus(Order.StatusEnum.ACCEPTED);
        orderToCompareTo.setRating(4);

        orderService.createOrder(order1);
        Order inDb = orderService.getOrderById(order1.getOrderID());
        Assertions.assertEquals("Knock on the door", inDb.getSpecialRequirements());

        order1.setSpecialRequirements("Don't knock!");

        mockMvc.perform(MockMvcRequestBuilders.put("/order/{orderID}", order1.getOrderID(), order1)
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

        orderService.createOrder(order1);

        order1.setOrderPaid(false);

        mockMvc.perform(MockMvcRequestBuilders.put("/order/{orderID}", UUID.randomUUID(), order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Transactional
    @Test
    public void editOrderByIdNotInDb() throws Exception {

        orderService.createOrder(order1);
        order1.setRating(1);

        mockMvc.perform(MockMvcRequestBuilders.put("/order/{orderID}", order2.getOrderID(), order1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }


    @Transactional
    @Test
    public void checkOrderIsPaidIsCorrect() throws Exception {

        orderService.createOrder(order1);

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}/isPaid",order1.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    public void checkOrderIsPaidWrongParameter() throws Exception {

        orderService.createOrder(order1);

        UUID uuid = UUID.randomUUID();
        if(uuid.equals(order1.getOrderID())){
            uuid = UUID.randomUUID();
        }
        mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}/isPaid",uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Transactional
    @Test
    public void checkOrderIsPaidNotPaid() throws Exception {

        orderService.createOrder(order1);

        order2.setOrderPaid(false);
        ResultActions resultActions2 = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}/isPaid",order2.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isPaymentRequired());
    }
    @Transactional
    @Test
    public void checkOrderPaidUpdate() throws Exception {

        orderService.createOrder(order1);

        order2.setOrderPaid(false);
        ResultActions resultActions2 = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());


        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/order/{orderID}/isPaid",order2.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        Boolean isPaid = objectMapper.readValue(res.getResponse().getContentAsString(),new TypeReference<Order>() {}).getOrderPaid();
        assertThat(isPaid).isTrue();
    }

    @Transactional
    @Test
    public void checkOrderPaidUpdateOrderWasPaid() throws Exception {

        orderService.createOrder(order1);

        order2.setOrderPaid(true);
        ResultActions resultActions2 = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());


        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put("/order/{orderID}/isPaid",order2.getOrderID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        Boolean isPaid = objectMapper.readValue(res.getResponse().getContentAsString(),new TypeReference<Order>() {}).getOrderPaid();
        assertThat(isPaid).isFalse();
    }
    @Transactional
    @Test
    public void checkOrderPaidUpdateNotFound() throws Exception {

        orderService.createOrder(order1);

        order2.setOrderPaid(false);
        ResultActions resultActions2 = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order2)));
        resultActions2.andExpect(status().isOk());

        UUID notExistent = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.put("/order/{orderID}/isPaid",notExistent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void testGetHistoryOrdersOfUser() throws Exception {

        orderService.createOrder(order1);

        Order order3 = new Order();
        order3.setOrderID(UUID.randomUUID());
        order3.setVendorID(UUID.randomUUID());
        order3.setCustomerID(order1.getCustomerID());
        order3.setAddress(a1);
        order3.setDate(new BigDecimal("1700006405000"));
        order3.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order3.setSpecialRequirements("Knock on the door");
        order3.setOrderPaid(true);
        order3.setStatus(Order.StatusEnum.DELIVERED);
        order3.setRating(4);

        Order order4 = new Order();
        order4.setOrderID(UUID.randomUUID());
        order4.setVendorID(UUID.randomUUID());
        order4.setCustomerID(order1.getCustomerID());
        order4.setAddress(a1);
        order4.setDate(new BigDecimal("1700006405000"));
        order4.setListOfDishes(Arrays.asList(UUID.randomUUID(), UUID.randomUUID()));
        order4.setSpecialRequirements("Knock on the door");
        order4.setOrderPaid(true);
        order4.setStatus(Order.StatusEnum.DELIVERED);
        order4.setRating(4);

        //Action
        ResultActions resultActions = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order3)));
        // Assert
        resultActions.andExpect(status().isOk());

        //Action
        ResultActions resultActions2 = mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order4)));
        // Assert
        resultActions2.andExpect(status().isOk());

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get("/order/{customerID}/history",order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Order> orders = objectMapper.readValue(res.getResponse().getContentAsString(),new TypeReference<>() {});
        assertThat(orders).containsExactlyInAnyOrder(order3,order4);

    }

    @Transactional
    @Test
    public void testGetHistoryOrdersOfUserNoMatchingInDatabase() throws Exception {

        orderService.createOrder(order1);

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{customerID}/history",order1.getCustomerID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Transactional
    @Test
    public void testGetHistoryOrdersOfUserNoUserFound() throws Exception {

        orderService.createOrder(order1);

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{customerID}/history",UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

}
