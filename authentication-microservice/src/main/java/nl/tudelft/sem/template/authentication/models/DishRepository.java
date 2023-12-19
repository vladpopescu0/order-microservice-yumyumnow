package nl.tudelft.sem.template.authentication.models;

import org.springframework.data.jpa.repository.JpaRepository;
import order-microservice.Dish;

public interface DishRepository extends JpaRepository<Dish, Long> {
    // additional query methods if needed
}