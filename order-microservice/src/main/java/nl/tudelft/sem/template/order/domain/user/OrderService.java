package nl.tudelft.sem.template.order.domain.user;

import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderService {
    private final transient OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Checks if the uuid can be found in the database.
     *
     * @param uuid the uuid to look for in the order Repository
     * @return true if the order exists in the database, false otherwise
     */
    public boolean checkUUIDIsUnique(UUID uuid) {
        return orderRepository.existsByOrderID(uuid);
    }

    /**
     * The implementation of the orderISPaid method from the controllers.
     *
     * @param orderID the id of the order to check
     * @return true if the order is paid, false otherwise
     * @throws OrderNotFoundException when the method cannot find the order in the database
     */
    public boolean orderIsPaid(UUID orderID) throws OrderNotFoundException {
        if (!checkUUIDIsUnique(orderID)) {
            throw new OrderNotFoundException(orderID);
        }
        Optional<Order> currentOrder = orderRepository.findOrderByOrderID(orderID);

        assert (currentOrder.isPresent());

        return currentOrder.get().getOrderPaid();
    }





}
