package nl.tudelft.sem.template.order.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.domain.user.repositories.DishRepository;
import nl.tudelft.sem.template.order.PersistentBagMock;
import org.hibernate.collection.internal.PersistentBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DishServiceTests {
    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    Dish d1;
    Dish d1CopyResult;

    Dish d2;

    List<String> ingredients = new ArrayList<>();

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
        PersistentBag pbAllergies = new PersistentBagMock();
        pbAllergies.addAll(allergies);
        d1.setListOfAllergies(pbAllergies);
//        d1.setListOfAllergies(allergies);
        ingredients = new ArrayList<>();
        ingredients.add("Cheese");
        ingredients.add("Salami");
        ingredients.add("Tomato Sauce");
        PersistentBag pbIngredients = new PersistentBagMock();
        pbIngredients.addAll(ingredients);
        d1.setListOfIngredients(pbIngredients);
        d1.setVendorID(UUID.randomUUID());

        d1CopyResult = new Dish();
        d1CopyResult.setDishID(d1.getDishID());
        d1CopyResult.setDescription("very tasty");
        d1CopyResult.setImage("img");
        d1CopyResult.setName("Pizza");
        d1CopyResult.setPrice(5.0f);
        d1CopyResult.setListOfAllergies(allergies);
        ingredients = new ArrayList<>();
        ingredients.add("Cheese");
        ingredients.add("Salami");
        ingredients.add("Tomato Sauce");
        d1CopyResult.setListOfIngredients(ingredients);
        d1CopyResult.setVendorID(d1.getVendorID());

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
        when(dishRepository.save(d1)).thenReturn(d1);

        Dish res = dishService.addDish(d1);
        assertThat(res).isEqualTo(d1CopyResult);
    }

    @Test
    public void add_dish_unsuccessful() {
        when(dishRepository.existsByDishID(d1.getDishID())).thenReturn(true);

        Assertions.assertThrows(DishIdAlreadyInUseException.class, () -> {
            dishService.addDish(d1);
        });
    }

    @Test
    public void get_dish_successful() throws DishNotFoundException {
        when(dishRepository.findDishByDishID(d1.getDishID())).thenReturn(Optional.ofNullable(d1));

        Dish res = dishService.getDishById(d1.getDishID());

        assertThat(res).isEqualTo(d1CopyResult);
    }

    @Test
    public void get_dish_not_exists() throws DishNotFoundException {
        when(dishRepository.findDishByDishID(d1.getDishID())).thenReturn(Optional.empty());

        Assertions.assertThrows(DishNotFoundException.class, () -> {
            dishService.getDishById(d1.getDishID());
        });
    }

    @Test
    public void update_dish_correct_id() throws DishNotFoundException {
        when(dishRepository.existsByDishID(d1.getDishID())).thenReturn(true);
        when(dishRepository.save(d1)).thenReturn(d1);

        Dish res = dishService.updateDish(d1.getDishID(), d1);

        assertThat(res).isEqualTo(d1CopyResult);
    }

    @Test
    public void update_dish_not_found() throws DishNotFoundException {
        Assertions.assertThrows(DishNotFoundException.class, () -> {
            dishService.updateDish(d1.getDishID(), d1);
        });
    }


    @Test
    public void delete_dish_correct_id() throws DishNotFoundException {
        when(dishRepository.existsByDishID(d1.getDishID())).thenReturn(true);
        dishService.deleteDishByDishId(d1.getDishID());

        verify(dishRepository, times(1)).existsByDishID(d1.getDishID());
        verify(dishRepository, times(1)).deleteById(d1.getDishID());
    }

    @Test
    public void delete_dish_not_found() throws DishNotFoundException {
        when(dishRepository.existsByDishID(d1.getDishID())).thenReturn(false);
        Assertions.assertThrows(DishNotFoundException.class, () -> {
            dishService.deleteDishByDishId(d1.getDishID());
        });
        verify(dishRepository, times(1)).existsByDishID(d1.getDishID());
        verify(dishRepository, never()).deleteById(d1.getDishID());
    }

    @Test
    public void get_dishes_by_vendor_correct_id() throws VendorNotFoundException {
        when(dishRepository.findDishesByVendorID(d1.getVendorID())).thenReturn(Optional.of(List.of(d1, d2)));

        List<Dish> res = dishService.getDishByVendorId(d1.getVendorID());

        assertThat(res).contains(d1CopyResult).contains(d2);
    }

    @Test
    public void get_dishes_by_vendor_not_found() throws VendorNotFoundException {
        when(dishRepository.findDishesByVendorID(d1.getVendorID())).thenReturn(Optional.empty());

        Assertions.assertThrows(VendorNotFoundException.class, () -> {
            dishService.getDishByVendorId(d1.getVendorID());
        });
    }

    @Test
    public void get_dishes_with_allergies_correct() throws VendorNotFoundException {
        when(dishRepository.existsByVendorID(d1.getVendorID())).thenReturn(true);
        when(dishRepository.findDishesByVendorIDAndListOfAllergies(d1.getVendorID(), new ArrayList<>())).thenReturn(Optional.of(List.of(d1, d2)));

        List<Dish> res = dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());

        assertThat(res).contains(d1CopyResult).contains(d2);
    }

    @Test
    public void get_dishes_with_no_dish_found() throws VendorNotFoundException {
        when(dishRepository.existsByVendorID(d1.getVendorID())).thenReturn(true);
        when(dishRepository.findDishesByVendorIDAndListOfAllergies(d1.getVendorID(), new ArrayList<>())).thenReturn(Optional.empty());

        List<Dish> res = dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());
        assertThat(res).isInstanceOf(ArrayList.class);
        assertThat(res).isEmpty();
    }

    @Test
    public void get_dishes_with_allergies_not_found() throws VendorNotFoundException {
        when(dishRepository.existsByVendorID(d1.getVendorID())).thenReturn(false);
        Assertions.assertThrows(VendorNotFoundException.class, () -> {
            dishService.getAllergyFilteredDishesFromVendor(d1.getVendorID(), new ArrayList<>());
        });
        verify(dishRepository, never()).findDishesByVendorIDAndListOfAllergies(d1.getDishID(), new ArrayList<>());
    }
}
