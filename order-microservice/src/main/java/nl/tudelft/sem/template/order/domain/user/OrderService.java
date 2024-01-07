package nl.tudelft.sem.template.order.domain.user;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.repositories.DishRepository;
import nl.tudelft.sem.template.order.domain.user.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderService {
    private final transient OrderRepository orderRepository;
    private final transient DishRepository dishRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
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
     * Getter of orders from a vendor.
     *
     * @param vendorID UUID of the vendor
     * @return list of orders from the vendor
     * @throws VendorNotFoundException if the vendor does not exist
     * @throws NoOrdersException if no orders were found
     */
    private List<Order> getOrdersFromVendor(UUID vendorID) throws VendorNotFoundException, NoOrdersException {
        if (!orderRepository.existsByVendorID(vendorID)) {
            throw new VendorNotFoundException(vendorID);
        }
        Optional<List<Order>> orders = orderRepository.findOrdersByVendorID(vendorID);
        return handleDatabaseOrders(orders);
    }

    /**
     * Handles the orders retrieved from the database to ensure that the list of dishes is an arraylist.
     *
     * @param orders Optional containing a list of orders
     * @return List of orders with a list of dishes that is an arraylist
     * @throws NoOrdersException if the optional is empty
     */
    private List<Order> handleDatabaseOrders(Optional<List<Order>> orders) throws NoOrdersException {
        if (orders.isEmpty()) {
            throw new NoOrdersException();
        }
        List<Order> result = orders.get();
        for (Order o : result) {
            o.setListOfDishes(new ArrayList<>(o.getListOfDishes()));
        }
        return result;
    }

    /**
     * Getter for all the orders from a customer at a specific vendor.
     *
     * @param vendorID the UUID of the vendor where the orders have been placed
     * @param customerID the UUID of the customer who placed the orders
     * @return list of orders from a customer at a certain vendor if they both exist
     * @throws VendorNotFoundException if the vendor does not exist
     * @throws CustomerNotFoundException if the customer does not exist
     * @throws NoOrdersException if no orders were found
     */
    public List<Order> getOrdersFromCostumerAtVendor(UUID vendorID, UUID customerID)
            throws VendorNotFoundException, CustomerNotFoundException, NoOrdersException {
        if (!orderRepository.existsByVendorID(vendorID)) {
            throw new VendorNotFoundException(vendorID);
        }
        if (!orderRepository.existsByCustomerID(customerID)) {
            throw new CustomerNotFoundException(customerID);
        }
        Optional<List<Order>> orders = orderRepository.findOrdersByVendorIDAndCustomerID(vendorID, customerID);
        return handleDatabaseOrders(orders);
    }

    /**
     * Getter for the total number of orders made at a vendor.
     *
     * @param vendorID the UUID of the vendor from whom the total number of orders is returned
     * @return Integer of the total number of orders made at a vendor
     * @throws VendorNotFoundException if the vendor does not exist
     * @throws NoOrdersException if no orders were found
     */
    public Integer getOrderVolume(UUID vendorID) throws VendorNotFoundException, NoOrdersException {
        if (!orderRepository.existsByVendorID(vendorID)) {
            throw new VendorNotFoundException(vendorID);
        }
        Optional<Integer> res = orderRepository.countOrderByVendorID(vendorID);
        if (res.isEmpty()) {
            throw new NoOrdersException();
        }
        return res.get();
    }

    /**
     * Getter for a list containing the volume of orders divided over each hour of the day.
     *
     * @param vendorID the UUID of the vendor from whom the volumes divided over the hours are retrieved
     * @return List of integers that represent the volume of orders divided over each hour of the day, where index
     *             0 is for 00:00 till 01:00, 1 is for 01:00 till 02:00, ... 23 is for 23:00 till 00:00
     * @throws VendorNotFoundException if the vendor does not exist
     * @throws NoOrdersException if no orders were found
     */
    public List<Integer> getOrderVolumeByTime(UUID vendorID) throws VendorNotFoundException, NoOrdersException {
        List<Order> orders = getOrdersFromVendor(vendorID);
        int[] times = new int[24];
        for (Order o : orders) {
            long t = o.getDate().longValueExact();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(t);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            times[hours]++;
        }
        return Arrays.stream(times).boxed().collect(Collectors.toList());
    }


    /**
     * Getter for a list of dishes offered by a vendor ordered by how often they have been ordered.
     * The list only contain dishes that have been ordered at least once
     *
     * @param vendorID UUID of the vendor from which the popular dishes will be retrieved
     * @return list with the dishes ordered by how often they have been ordered, only including dishes
     *             that have been ordered before
     * @throws VendorNotFoundException if the vendor does not exist
     * @throws NoOrdersException if no orders were found
     */
    public List<Dish> getDishesSortedByVolume(UUID vendorID) throws VendorNotFoundException, DishNotFoundException {
        if (!orderRepository.existsByVendorID(vendorID)) {
            throw new VendorNotFoundException(vendorID);
        }
        return orderRepository.countDishesOccurrencesFromVendor(vendorID);
    }
}
