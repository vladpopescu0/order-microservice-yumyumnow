package nl.tudelft.sem.template.order.domain.user;

import java.util.ArrayList;
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
     * @return false if the order exists in the database, true otherwise
     */
    public boolean checkUUIDIsUnique(UUID uuid) {
        return !orderRepository.existsByOrderID(uuid);
    }

    /**
     * Method for adding a new Order to the database
     *
     * @param order new Order to be added to the database
     * @return Order that has been created and added to the database
     * @throws OrderIdAlreadyInUseException - thrown when the provided orderID is not unique
     */
    public Order createOrder(Order order) throws OrderIdAlreadyInUseException {

        if(!checkUUIDIsUnique(order.getOrderID())){
            throw new OrderIdAlreadyInUseException(order.getOrderID());
        }

        order = orderRepository.save(order);
        order.setListOfDishes(new ArrayList<>(order.getListOfDishes()));

        return order;

    }

    /**
     * The implementation of the orderISPaid method from the controllers.
     *
     * @param orderID the id of the order to check
     * @return true if the order is paid, false otherwise
     * @throws OrderNotFoundException when the method cannot find the order in the database
     */
    public boolean orderIsPaid(UUID orderID) throws OrderNotFoundException {
        if (checkUUIDIsUnique(orderID)) {
            throw new OrderNotFoundException(orderID);
        }
        Optional<Order> currentOrder = orderRepository.findOrderByOrderID(orderID);

        assert (currentOrder.isPresent());

        return currentOrder.get().getOrderPaid();
    }





}
