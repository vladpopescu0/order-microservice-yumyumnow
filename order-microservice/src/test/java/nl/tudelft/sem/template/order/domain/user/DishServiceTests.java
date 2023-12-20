package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.order.commons.Dish;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DishServiceTests {

    @Autowired
    private transient DishService dishService;

    Dish d1;
    Dish d2;
    @BeforeEach
    public void setup(){
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
        // Arrange
        dishService.addDish(d1);
        Dish persistedDish = dishService.getDishById(d1.getDishID());
        assertThat(d1).isEqualTo(persistedDish);
        assertThat(persistedDish).isEqualTo(d1);
    }

    @Transactional
    @Test
    public void createDish_withNullData() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () ->{
            dishService.addDish(null);
        });
    }

    @Transactional
    @Test
    public void createDuplicateDishes() throws Exception {
        dishService.addDish(d1);
        Assertions.assertThrows(DishIdAlreadyInUseException.class, () ->{
            dishService.addDish(d1);
        });
    }

    @Transactional
    @Test
    public void DishDoesNotExist() throws Exception {
        Assertions.assertThrows(DishNotFoundException.class, () ->{
            dishService.getDishById(UUID.randomUUID());
        });
    }

    @Transactional
    @Test
    public void VendorDoesNotExist() throws Exception {
        dishService.addDish(d1);
        Assertions.assertThrows(VendorNotFoundException.class, () ->{
            dishService.getDishByVendorId(UUID.randomUUID());
        });
    }

    @Transactional
    @Test
    public void VendorDoesExist() throws Exception {
        dishService.addDish(d1);
        dishService.addDish(d2);
        List<Dish> dishes = dishService.getDishByVendorId(d1.getVendorID());
        assertThat(dishes.size()).isEqualTo(1);
        assertThat(dishes).contains(d1);
        assertThat(dishes).doesNotContain(d2);
    }

    @Transactional
    @Test
    public void VendorDoesExistMultipleResults() throws Exception {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<Dish> dishes = dishService.getDishByVendorId(d1.getVendorID());
        assertThat(dishes.size()).isEqualTo(2);
        assertThat(dishes).contains(d1);
        assertThat(dishes).contains(d1);
    }

    @Transactional
    @Test
    public void DeleteNonExistentDish() throws Exception {
        Assertions.assertThrows(DishNotFoundException.class, () ->{
            dishService.deleteDishByDishId(UUID.randomUUID());
        });
    }

    @Transactional
    @Test
    public void DeleteExistingDish() throws Exception {
        dishService.addDish(d1);
        dishService.deleteDishByDishId(d1.getDishID());
        Assertions.assertThrows(DishNotFoundException.class, () ->{
            dishService.deleteDishByDishId(d1.getDishID());
        });
    }

    @Transactional
    @Test
    public void UpdateNonExistingDish() throws Exception {
        Assertions.assertThrows(DishNotFoundException.class, () ->{
            dishService.updateDish(d1.getDishID(),d1);
        });
    }

    @Transactional
    @Test
    public void UpdateExistingDish() throws Exception {
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
        dishService.updateDish(d1.getDishID(),d1);
        Dish databaseDish2 = dishService.getDishById(d1.getDishID());
        assertThat(databaseDish2.getName()).isEqualTo("name 2").isNotEqualTo("name");
        assertThat(databaseDish2.getListOfIngredients()).isEqualTo(ingredients2).isNotEqualTo(ingredients);
    }

    @Transactional
    @Test
    public void isNotUnique() throws DishIdAlreadyInUseException {
        dishService.addDish(d1);
        assertThat(dishService.checkDishUuidIsUnique(d1.getDishID())).isFalse();
    }

    @Transactional
    @Test
    public void isUnique(){
        assertThat(dishService.checkDishUuidIsUnique(d1.getDishID())).isTrue();
    }

    @Transactional
    @Test
    public void filterOnNonExistentVendor(){
        List<String> allergies = new ArrayList<>();
        Assertions.assertThrows(VendorNotFoundException.class, () ->{
            dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(),allergies);
        });
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithNoResults() throws DishIdAlreadyInUseException, VendorNotFoundException {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add("lactose");
        List<Dish> filteredDishes = dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(),allergies);
        assertThat(filteredDishes.size()).isEqualTo(0);
        assertThat(filteredDishes).doesNotContain(d1).doesNotContain(d2);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithOneResult() throws VendorNotFoundException, DishIdAlreadyInUseException {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add("gluten");
        List<Dish> filteredDishes = dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(),allergies);
        assertThat(filteredDishes.size()).isEqualTo(1);
        assertThat(filteredDishes).doesNotContain(d2).contains(d1);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithMultipleResults() throws VendorNotFoundException, DishIdAlreadyInUseException {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add("chicken");
        allergies.add("grain");
        List<Dish> filteredDishes = dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(),allergies);
        assertThat(filteredDishes.size()).isEqualTo(2);
        assertThat(filteredDishes).contains(d1).contains(d2);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithNonFilteredDishes() throws VendorNotFoundException, DishIdAlreadyInUseException {
        dishService.addDish(d1);
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        allergies.add(d1.getListOfAllergies().get(0));
        List<Dish> filteredDishes = dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(),allergies);
        assertThat(filteredDishes.size()).isEqualTo(0);
        assertThat(filteredDishes).doesNotContain(d1).doesNotContain(d2);
    }

    @Transactional
    @Test
    public void filterOnExistentVendorWithNoFilter() throws VendorNotFoundException, DishIdAlreadyInUseException {
        dishService.addDish(d1);
        d2.setVendorID(d1.getVendorID());
        dishService.addDish(d2);
        List<String> allergies = new ArrayList<>();
        List<Dish> filteredDishes = dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(),allergies);
        assertThat(filteredDishes.size()).isEqualTo(2);
        assertThat(filteredDishes).contains(d1).contains(d2);
    }
}