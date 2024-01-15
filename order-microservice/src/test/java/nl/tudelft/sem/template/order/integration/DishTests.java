package nl.tudelft.sem.template.order.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.domain.user.DishService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class DishTests {
    @Autowired
    private transient MockMvc mockMvc;

    @Autowired
    private transient DishService dishService;

    @Autowired
    private transient ObjectMapper objectMapper; // Used for converting Java objects to JSON

    transient Dish d1;

    transient Dish d2;

    transient String postPath = "/dish";
    transient String getPath = "/dish/{dishID}";
    transient String getListPath = "/dish/list/{dishId}";
    transient String allergiesPath = "/dish/allergy-list/{vendorId}";

    transient String allergiesString = "allergies";

    /**
     * setup for DishTests.
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
        d2.setDishID(UUID.randomUUID());
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

    @Transactional
    @Test
    public void createDish_withValidData_worksCorrectly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(postPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(d1))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        Dish persistedDish = dishService.getDishById(d1.getDishID());
        assertThat(d1).isEqualTo(persistedDish);
        assertThat(persistedDish).isEqualTo(d1);
    }

    @Transactional
    @Test
    public void createDish_withNullData() throws Exception {
        d1.setDishID(null);
        mockMvc.perform(MockMvcRequestBuilders.post(postPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void createDish_duplicate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(postPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        Dish persistedDish = dishService.getDishById(d1.getDishID());
        assertThat(d1).isEqualTo(persistedDish);
        assertThat(persistedDish).isEqualTo(d1);

        mockMvc.perform(MockMvcRequestBuilders.post(postPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void getDishThatDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(getPath, d1.getDishID())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void getDishWithWrongParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(getPath, new Object())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void getDishThatExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(postPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(getPath, d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        Dish returnedDish = objectMapper.readValue(res.getResponse().getContentAsString(), Dish.class);
        assertThat(returnedDish).isEqualTo(d1);
    }

    @Transactional
    @Test
    public void vendorDoesNotExist() throws Exception {
        dishService.addDish(d1);
        mockMvc.perform(MockMvcRequestBuilders.get(getListPath, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void getDishesFromVendorWithWrongParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(getListPath, new Object())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void vendorDoesExist() throws Exception {
        dishService.addDish(d1);
        dishService.addDish(d2);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(getListPath, d1.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> returnedDishes = objectMapper.readValue(res.getResponse()
                .getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(returnedDishes.size()).isEqualTo(1);
        assertThat(returnedDishes).contains(d1).doesNotContain(d2);
    }

    @Transactional
    @Test
    public void vendorDoesExistMultipleResults() throws Exception {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(getListPath, d1.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> returnedDishes = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(returnedDishes.size()).isEqualTo(2);
        assertThat(returnedDishes).contains(d1).contains(d2);
    }

    @Transactional
    @Test
    public void deleteNonExistentDish() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(getPath, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void deleteExistingDish() throws Exception {
        dishService.addDish(d1);
        assertThat(dishService.checkDishUuidIsUnique(d1.getDishID())).isFalse();
        mockMvc.perform(MockMvcRequestBuilders.delete(getPath, d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertThat(dishService.checkDishUuidIsUnique(d1.getDishID())).isTrue();
    }

    @Transactional
    @Test
    public void deleteExistingDishWithWrongParameter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(getPath, new Object())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void updateNonExistingDish() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(getPath, d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void updateWithDifferentId() throws Exception {
        dishService.addDish(d1);
        dishService.addDish(d2);
        mockMvc.perform(MockMvcRequestBuilders.put(getPath, d2.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void updateWithBadParameters() throws Exception {
        dishService.addDish(d1);
        mockMvc.perform(MockMvcRequestBuilders.put(getPath, new Object())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void updateWithBadDish() throws Exception {
        dishService.addDish(d1);
        d1.setListOfIngredients(null);
        mockMvc.perform(MockMvcRequestBuilders.put(getPath, d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Transactional
    @Test
    public void updateExistingDish() throws Exception {
        d1.name("name");
        List<String> ingredients = new ArrayList<>();
        ingredients.add("cheese");
        ingredients.add("water");
        d1.setListOfIngredients(ingredients);
        dishService.addDish(d1);
        Dish databaseDish = dishService.getDishById(d1.getDishID());
        assertThat(databaseDish.getName()).isEqualTo("name");
        assertThat(databaseDish.getListOfIngredients()).isEqualTo(ingredients);
        d1.name("name 2");
        List<String> ingredients2 = new ArrayList<>();
        ingredients.add("milk");
        ingredients.add("egg");
        d1.setListOfIngredients(ingredients2);
        dishService.updateDish(d1.getDishID(), d1);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.put(getPath, d1.getDishID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(d1))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON)).andReturn();
        Dish databaseDish2 = objectMapper.readValue(res.getResponse()
                .getContentAsString(), Dish.class);
        assertThat(databaseDish2.getName()).isEqualTo("name 2")
                .isNotEqualTo("name");
        assertThat(databaseDish2.getListOfIngredients())
                .isEqualTo(ingredients2).isNotEqualTo(ingredients);
    }


    @Transactional
    @Test
    public void filterOnNonExistentVendor() throws Exception {
        List<String> allergies = new ArrayList<>();
        mockMvc.perform(MockMvcRequestBuilders.get(allergiesPath, d1.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allergies))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithNoResults() throws Exception {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add("lactose");
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(allergiesPath, d1.getVendorID())
                        .param(allergiesString, allergies.toArray(new String[0]))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> filteredDishes = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(filteredDishes.size()).isEqualTo(0);
        assertThat(filteredDishes).doesNotContain(d1).doesNotContain(d2);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithOneResult() throws Exception {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add("gluten");
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(allergiesPath, d1.getVendorID())
                        .param(allergiesString, allergies.toArray(new String[0]))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> filteredDishes = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(filteredDishes.size()).isEqualTo(1);
        assertThat(filteredDishes).doesNotContain(d2).contains(d1);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithMultipleResults() throws Exception {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add("chicken");
        allergies.add("grain");
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(allergiesPath, d1.getVendorID())
                        .param(allergiesString, allergies.toArray(new String[0]))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> filteredDishes = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(filteredDishes.size()).isEqualTo(2);
        assertThat(filteredDishes).contains(d1).contains(d2);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithNonFilteredDishes() throws Exception {
        dishService.addDish(d1);
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add(d1.getListOfAllergies().get(0));
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(allergiesPath, d1.getVendorID())
                        .param(allergiesString, allergies.toArray(new String[0]))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> filteredDishes = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(filteredDishes.size()).isEqualTo(0);
        assertThat(filteredDishes).doesNotContain(d1).doesNotContain(d2);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithNoFilter() throws Exception {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        MvcResult res = mockMvc.perform(MockMvcRequestBuilders.get(allergiesPath, d1.getVendorID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andReturn();
        List<Dish> filteredDishes = objectMapper
                .readValue(res.getResponse().getContentAsString(), new TypeReference<List<Dish>>() {});
        assertThat(filteredDishes.size()).isEqualTo(2);
        assertThat(filteredDishes).contains(d1).contains(d2);
    }
}
