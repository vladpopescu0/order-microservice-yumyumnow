package nl.tudelft.sem.template.order.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.DishService;
import nl.tudelft.sem.template.order.domain.user.OrderService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper; // Used for converting Java objects to JSON

    Order o1;

    @BeforeEach
    public void setup(){
        o1 = new Order();
        o1.setOrderID(UUID.randomUUID());
        o1.setOrderPaid(true);
    }

    //These shall be uncommented when the endpoint that adds an order to the database is implemented
//    @Transactional
//    @Test
//    public void checkOrderIsPaidIsCorrect() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}/isPaid",o1.getOrderID())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    @Transactional
//    @Test
//    public void checkOrderIsPaidWrongParameter() throws Exception {
//        UUID uuid = UUID.randomUUID();
//        if(uuid.equals(o1.getOrderID())){
//            uuid = UUID.randomUUID();
//        }
//        mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}/isPaid",uuid)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//    }
//    @Transactional
//    @Test
//    public void checkOrderIsPaidNotPaid() throws Exception {
//        UUID uuid = UUID.randomUUID();
//        if(uuid.equals(o1.getOrderID())){
//            uuid = UUID.randomUUID();
//        }
//        o1.setOrderPaid(false);
//        mockMvc.perform(MockMvcRequestBuilders.get("/order/{orderID}/isPaid",uuid)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isPaymentRequired());
//    }

}
