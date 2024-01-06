package nl.tudelft.sem.template.order.domain.user.repositories;

import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

//    @Query("SELECT lod.LIST_OF_DISHES, COUNT(*)\n" +
//            "FROM Orders o JOIN ORDER_LIST_OF_DISHES as lod on lod.ORDER_ORDERID = o.ORDERID\n" +
//            "WHERE o.VENDORID = '550e8400e29b41d4a716446655440001'\n" +
//            "GROUP BY lod.LIST_OF_DISHES")
//    @Query("SELECT lod.LIST_OF_DISHES, COUNT(*)\n" +
//        "FROM Order o JOIN ORDER_LIST_OF_DISHES as lod on lod.ORDER_ORDERID = o.ORDERID\n" +
//        "WHERE o.VENDORID = '550e8400e29b41d4a716446655440001'\n" +
//        "GROUP BY lod.LIST_OF_DISHES")
//    @Query("SELECT o.listOfDishes, COUNT(*)" +
//            "FROM Order o " +
////            "WHERE o.vendorID = :vendorID " +
//            "GROUP BY o.listOfDishes")
//    List<Object[]> countDishesOccurrencesFromVendor();
    @Query(value = "SELECT d.DISHID " +
            "FROM Orders o " +
            "JOIN ORDER_LIST_OF_DISHES as lod on lod.ORDER_ORDERID = o.ORDERID " +
            "JOIN DISH as d on lod.LIST_OF_DISHES = d.DISHID " +
            "WHERE o.VENDORID = :vendorID " +
            "GROUP BY d.DISHID " +
            "ORDER BY COUNT(*) DESC", nativeQuery = true)
    List<Object> countDishesOccurrencesFromVendor(@Param("vendorID") UUID vendorID);

}