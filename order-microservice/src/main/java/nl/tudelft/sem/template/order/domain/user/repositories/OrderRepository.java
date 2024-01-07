package nl.tudelft.sem.template.order.domain.user.repositories;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.order.commons.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // additional query methods if needed

    Optional<Order> findOrderByOrderID(UUID orderID);

    boolean existsByOrderID(UUID orderID);

    @Modifying
    @Transactional
    @Query("update Order u set u.orderPaid = ?1 where u.orderID = ?2")
    void updateOrderPayment(Boolean orderPaid, UUID orderID);

    Optional<List<Order>> findOrdersByCustomerID(UUID customerID);
}