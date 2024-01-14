package nl.tudelft.sem.template.order.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.order.controllers.DishController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class DishControllerTests {
    @Mock
    private DishService dishService;

    @InjectMocks
    private DishController dishController;

    Dish d1;

    Dish d2;

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

    @Test
    public void add_dish_successful() throws DishIdAlreadyInUseException {
        when(dishService.addDish(d1)).thenReturn(d1);

        ResponseEntity<Dish> res = dishController.addDish(d1);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(d1);
    }

    @Test
    public void add_dish_unsuccessful() throws DishIdAlreadyInUseException {
        when(dishService.addDish(d1)).thenThrow(DishIdAlreadyInUseException.class);

        ResponseEntity<Dish> res = dishController.addDish(d1);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void get_dish_successful() throws DishNotFoundException {
        when(dishService.getDishById(d1.getDishID())).thenReturn(d1);

        ResponseEntity<Dish> res = dishController.getDishByID(d1.getDishID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(d1);
    }

    @Test
    public void get_dish_not_exists() throws DishNotFoundException {
        when(dishService.getDishById(d1.getDishID())).thenThrow(DishNotFoundException.class);

        ResponseEntity<Dish> res = dishController.getDishByID(d1.getDishID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void get_dish_bad_request() throws DishNotFoundException {
        when(dishService.getDishById(d1.getDishID())).thenThrow(NullPointerException.class);

        ResponseEntity<Dish> res = dishController.getDishByID(d1.getDishID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void update_dish_different_id() {
        ResponseEntity<Dish> res = dishController.updateDishByID(UUID.randomUUID(), d1);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void update_dish_correct_id() throws DishNotFoundException {
        when(dishService.updateDish(d1.getDishID(), d1)).thenReturn(d1);

        ResponseEntity<Dish> res = dishController.updateDishByID(d1.getDishID(), d1);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(d1);
    }

    @Test
    public void update_dish_not_found() throws DishNotFoundException {
        when(dishService.updateDish(d1.getDishID(), d1)).thenThrow(DishNotFoundException.class);

        ResponseEntity<Dish> res = dishController.updateDishByID(d1.getDishID(), d1);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void update_dish_bad_request() throws DishNotFoundException {
        when(dishService.updateDish(d1.getDishID(), d1)).thenThrow(NullPointerException.class);

        ResponseEntity<Dish> res = dishController.updateDishByID(d1.getDishID(), d1);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_dish_correct_id() {
        ResponseEntity<Void> res = dishController.deleteDishByID(d1.getDishID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_dish_not_found() throws DishNotFoundException {
        doThrow(DishNotFoundException.class).when(dishService).deleteDishByDishId(d1.getDishID());

        ResponseEntity<Void> res = dishController.deleteDishByID(d1.getDishID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void delete_dish_bad_request() throws DishNotFoundException {
        doThrow(NullPointerException.class).when(dishService).deleteDishByDishId(d1.getDishID());

        ResponseEntity<Void> res = dishController.deleteDishByID(d1.getDishID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void get_dishes_by_vendor_correct_id() throws VendorNotFoundException {
        when(dishService.getDishByVendorId(d1.getVendorID())).thenReturn(List.of(d1, d2));

        ResponseEntity<List<Dish>> res = dishController.getDishesByVendorID(d1.getVendorID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains(d1).contains(d2);
    }

    @Test
    public void get_dishes_by_vendor_not_found() throws VendorNotFoundException {
        when(dishService.getDishByVendorId(d1.getVendorID())).thenThrow(VendorNotFoundException.class);

        ResponseEntity<List<Dish>> res = dishController.getDishesByVendorID(d1.getVendorID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void get_dishes_by_vendor_bad_request() throws VendorNotFoundException {
        when(dishService.getDishByVendorId(d1.getVendorID())).thenThrow(NullPointerException.class);

        ResponseEntity<List<Dish>> res = dishController.getDishesByVendorID(d1.getVendorID());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void get_dishes_with_allergies_correct() throws VendorNotFoundException {
        when(dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>())).thenReturn(List.of(d1, d2));

        ResponseEntity<List<Dish>> res = dishController.getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains(d1).contains(d2);
    }

    @Test
    public void get_dishes_with_allergies_not_found() throws VendorNotFoundException {
        doThrow(VendorNotFoundException.class).when(dishService).getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());

        ResponseEntity<List<Dish>> res = dishController.getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void get_dishes_with_allergies_bad_request() throws VendorNotFoundException {
        doThrow(NullPointerException.class).when(dishService).getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());

        ResponseEntity<List<Dish>> res = dishController.getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
