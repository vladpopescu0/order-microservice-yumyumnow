package nl.tudelft.sem.template.order.domain.user.repositories;

import nl.tudelft.sem.template.order.commons.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

@Query("SELECT d FROM Dish d WHERE d.vendorID = :vendorId AND NOT EXISTS (SELECT a FROM d.listOfAllergies a WHERE a IN :allergies)")
Optional<List<Dish>> findDishesByVendorIDAndListOfAllergies(@Param("vendorId") UUID vendorId, @Param("allergies") List<String> allergies);
    boolean existsByDishID(UUID dishId);

    boolean existsByVendorID(UUID vendorID);
}