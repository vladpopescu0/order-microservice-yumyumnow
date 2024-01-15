package nl.tudelft.sem.template.order.domain.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.order.domain.user.repositories.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DishService {
    private final transient DishRepository dishRepository;

    /**
     * Instantiates a new DishService.
     *
     * @param dishRepository  the dish repository
     */
    @Autowired
    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    /**
     * add a new user.
     *
     * @param dish    The Dish that has to be added
     * @return The dish that has been added to the database
     * @throws DishIdAlreadyInUseException if there already is a dish with the same id
     */
    public Dish addDish(Dish dish) throws DishIdAlreadyInUseException {
        if (!checkDishUuidIsUnique(dish.getDishID())) {
            throw new DishIdAlreadyInUseException(dish.getDishID());
        }
        dish = dishRepository.save(dish);
        dish.setListOfIngredients(new ArrayList<>(dish.getListOfIngredients()));
        dish.setListOfAllergies(new ArrayList<>(dish.getListOfAllergies()));
        return dish;
    }

    /**
     * check if the uuid of a dish is unique.
     *
     * @param uuid    The uuid that has to be checked
     * @return True is there is no dish in the database with the given uuid
     *         False otherwise
     */
    public boolean checkDishUuidIsUnique(UUID uuid) {
        return !dishRepository.existsByDishID(uuid);
    }

    /**
     * retrieve a dish.
     *
     * @param dishId   The uuid of the dish that has to be retrieved
     * @return Dish from the database with the given id
     * @throws DishNotFoundException if there is no dish with the given id in the database
     */
    public Dish getDishById(UUID dishId) throws DishNotFoundException {
        Optional<Dish> databaseDish = dishRepository.findDishByDishID(dishId);
        if (databaseDish.isEmpty()) {
            throw new DishNotFoundException(dishId);
        }
        Dish res = databaseDish.get();
        res.setListOfIngredients(new ArrayList<>(res.getListOfIngredients()));
        res.setListOfAllergies(new ArrayList<>(res.getListOfAllergies()));
        return res;
    }

    /**
     * retrieve all dishes from a vendor.
     *
     * @param vendorId  The uuid of the vendor from whom the dishes will be returned
     * @return Dishes from the database of the given vendor
     * @throws VendorNotFoundException if there are no dishes from a certain vendor
     */
    public List<Dish> getDishByVendorId(UUID vendorId) throws VendorNotFoundException {
        Optional<List<Dish>> databaseDishes = dishRepository.findDishesByVendorID(vendorId);
        if (databaseDishes.isEmpty()) {
            throw new VendorNotFoundException(vendorId);
        }
        List<Dish> res = databaseDishes.get();
        for (Dish d : res) {
            d.setListOfIngredients(new ArrayList<>(d.getListOfIngredients()));
            d.setListOfAllergies(new ArrayList<>(d.getListOfAllergies()));
        }
        return res;
    }

    /**
     * update a dish.
     *
     * @param dishId  the id of the dish that will be updated
     * @param dish    the updated dish that will replace the old one
     * @return Dish from the database after it has been updated
     * @throws DishNotFoundException if there is no dish with the given id
     */
    public Dish updateDish(UUID dishId, Dish dish) throws DishNotFoundException {
        if (checkDishUuidIsUnique(dishId)) {
            throw new DishNotFoundException(dishId);
        }
        dish = dishRepository.save(dish);
        dish.setListOfIngredients(new ArrayList<>(dish.getListOfIngredients()));
        dish.setListOfAllergies(new ArrayList<>(dish.getListOfAllergies()));
        return dish;
    }

    /**
     * delete a dish.
     *
     * @param dishId  the id of the dish that will be deleted
     * @throws DishNotFoundException if there is no dish with the given id
     */
    public void deleteDishByDishId(UUID dishId) throws DishNotFoundException {
        if (checkDishUuidIsUnique(dishId)) {
            throw new DishNotFoundException(dishId);
        }
        dishRepository.deleteById(dishId);
    }

    /**
     * retrieve a filtered list of dishes from a vendor.
     *
     * @param vendorId  the uuid of the vendor from whom the dishes will be returned
     * @param allergies the list of allergies that will be used to filter on
     * @return Dishes from the database of the given vendor where all dishes that contain
     *         an allergy in allergies will be excluded
     * @throws VendorNotFoundException if there are no dishes from a certain vendor
     */
    public List<Dish> getAllergyFilteredDishesFromVendor(UUID vendorId,
                                                         List<String> allergies) throws VendorNotFoundException {
        if (!dishRepository.existsByVendorID(vendorId)) {
            throw new VendorNotFoundException(vendorId);
        }
        Optional<List<Dish>> databaseDishes = dishRepository.findDishesByVendorIDAndListOfAllergies(vendorId, allergies);
        if (databaseDishes.isEmpty()) {
            return new ArrayList<>();
        }
        List<Dish> res = databaseDishes.get();
        for (Dish d : res) {
            d.setListOfIngredients(new ArrayList<>(d.getListOfIngredients()));
            d.setListOfAllergies(new ArrayList<>(d.getListOfAllergies()));
        }
        return res;
    }
}
