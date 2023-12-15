package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<Dish, UUID> {
    Optional<Dish> findDishByDishID(UUID dishId);

    Optional<List<Dish>> findDishesByVendorID(UUID vendorId);

    @Override
    void deleteById(UUID uuid);

    Optional<List<Dish>> findDishesByVendorIDAndAndListOfAllergies(UUID vendorId, List<String> allergies);

    boolean existsByDishID(UUID dishId);
}
