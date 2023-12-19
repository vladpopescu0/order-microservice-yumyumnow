package nl.tudelft.sem.template.order.database;

import nl.tudelft.sem.template.order.commons.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    // additional query methods if needed
}