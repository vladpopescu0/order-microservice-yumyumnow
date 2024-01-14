package nl.tudelft.sem.template.order.domain.user.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // additional query methods if needed

    Optional<Order> findOrderByOrderID(UUID orderID);

    boolean existsByOrderID(UUID orderID);

    boolean existsByVendorID(UUID vendorID);

    boolean existsByCustomerID(UUID customerID);

    Optional<List<Order>> findOrdersByVendorIDAndCustomerID(UUID vendorId, UUID customerID);

    Optional<Integer> countOrderByVendorID(UUID vendorID);

    Optional<List<Order>> findOrdersByVendorID(UUID vendorID);

    @Query(value = "SELECT d "
            + "FROM Order o "
            + "JOIN o.listOfDishes lod "
            + "JOIN Dish d on d.dishID = lod "
            + "WHERE o.vendorID = :vendorID "
            + "GROUP BY lod "
            + "ORDER BY COUNT(lod) DESC")
    List<Dish> countDishesOccurrencesFromVendor(@Param("vendorID") UUID vendorID);

    @Modifying
    @Transactional
    @Query("update Order u set u.orderPaid = ?1 where u.orderID = ?2")
    void updateOrderPayment(Boolean orderPaid, UUID orderID);

    Optional<List<Order>> findOrdersByCustomerID(UUID customerID);
}