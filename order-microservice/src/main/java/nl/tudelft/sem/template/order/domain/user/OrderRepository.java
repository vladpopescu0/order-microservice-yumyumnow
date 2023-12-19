package nl.tudelft.sem.template.order.domain.user;

import nl.tudelft.sem.template.order.commons.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // additional query methods if needed

    Optional<Order> findOrderByOrderID(UUID orderID);

    boolean existsByOrderID(UUID orderID);
}