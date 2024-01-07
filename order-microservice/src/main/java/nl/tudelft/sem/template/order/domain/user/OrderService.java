package nl.tudelft.sem.template.order.domain.user;

import java.util.ArrayList;
import java.util.List;
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
     * Method for adding a new Order to the database.
     *
     * @param order new Order to be added to the database
     * @return Order that has been created and added to the database
     * @throws OrderIdAlreadyInUseException - thrown when the provided orderID is not unique
     */
    public Order createOrder(Order order) throws OrderIdAlreadyInUseException {

        if (checkUUIDIsUnique(order.getOrderID())) {
            throw new OrderIdAlreadyInUseException(order.getOrderID());
        }

        order = orderRepository.save(order);
        order.setListOfDishes(new ArrayList<>(order.getListOfDishes()));

        return order;

    }

    /**
     * Method for returning all Orders stored in the database.
     *
     * @return List of Orders in the database
     * @throws NoOrdersException - thrown when there are no Orders in the database
     */
    public List<Order> getAllOrders() throws NoOrdersException {

        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NoOrdersException();
        }

        for (Order o : orders) {
            o.setListOfDishes(new ArrayList<>(o.getListOfDishes()));
        }

        return orders;
    }

    /**
     * Method for returning specific Order.
     *
     * @param orderID Provided ID of Order to be returned
     * @return Order with specified ID
     * @throws OrderNotFoundException - thrown when the orderID isn't found
     */
    public Order getOrderById(UUID orderID) throws OrderNotFoundException {

        Optional<Order> o = orderRepository.findOrderByOrderID(orderID);
        if (o.isEmpty()) {
            throw new OrderNotFoundException(orderID);
        }

        Order orderToRet = o.get();
        orderToRet.setListOfDishes(new ArrayList<>(orderToRet.getListOfDishes()));
        return orderToRet;

    }

    /**
     * Method for editing an Order in the database.
     *
     * @param orderID ID specifying the Order to be deleted
     * @param order The edited Order to put into the database
     * @return Edited Order
     * @throws OrderNotFoundException - thrown when the orderID isn't found
     */
    public Order editOrderByID(UUID orderID, Order order) throws OrderNotFoundException {
        if (!checkUUIDIsUnique(orderID)) {
            throw new OrderNotFoundException(orderID);
        }

        order = orderRepository.save(order);
        order.setListOfDishes(new ArrayList<>(order.getListOfDishes()));

        return order;

    }

    /**
     * Method for deleting specific Order from the database.
     *
     * @param orderID ID specifying the Order to be deleted
     * @throws OrderNotFoundException - thrown when the orderID isn't found
     */
    public void deleteOrderByID(UUID orderID) throws OrderNotFoundException {
        if (!checkUUIDIsUnique(orderID)) {
            throw new OrderNotFoundException(orderID);
        }
        orderRepository.deleteById(orderID);
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
    /**
     * The implementation of the orderISPaid put method from the controllers.
     *
     * @param orderID the id of the order to flip the isPaid value
     * @throws OrderNotFoundException when the method cannot find the order in the database
     */

    public Order orderIsPaidUpdate(UUID orderID) throws OrderNotFoundException {
        if (!checkUUIDIsUnique(orderID)) {
            throw new OrderNotFoundException(orderID);
        }
        Optional<Order> currentOrder = orderRepository.findOrderByOrderID(orderID);

        assert (currentOrder.isPresent());

        orderRepository.updateOrderPayment(!currentOrder.get().getOrderPaid(), orderID);

        Order o = currentOrder.get();
        o.setOrderPaid(!o.getOrderPaid());
        return o;
    }

    public List<Order> getOrdersByCustomerID(UUID customerID) throws NoOrdersException {
        Optional<List<Order>> customerOrders = orderRepository.findOrdersByCustomerID(customerID);
        if (customerOrders.isEmpty()) {
            throw new NoOrdersException();
        }
        return customerOrders.get();
    }
}
