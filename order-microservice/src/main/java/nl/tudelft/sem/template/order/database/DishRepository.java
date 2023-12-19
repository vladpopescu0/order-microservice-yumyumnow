package nl.tudelft.sem.template.order.database;

import nl.tudelft.sem.template.order.commons.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DishRepository extends JpaRepository<Dish, UUID> {
    Optional<Dish> findDishByDishID(UUID dishId);

    Optional<List<Dish>> findDishesByVendorID(UUID vendorId);

    @Override
    void deleteById(UUID uuid);

    Optional<List<Dish>> findDishesByVendorIDAndAndListOfAllergies(UUID vendorId, List<String> allergies);

    boolean existsByDishID(UUID dishId);
}