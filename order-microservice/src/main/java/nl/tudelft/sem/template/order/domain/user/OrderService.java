package nl.tudelft.sem.template.order.domain.user;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.*;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.repositories.DishRepository;
import nl.tudelft.sem.template.order.domain.user.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;


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

    private List<Order> getOrdersFromVendor(UUID vendorID) throws VendorNotFoundException, NoOrdersException {
        if(!orderRepository.existsByVendorID(vendorID)){
            throw new VendorNotFoundException(vendorID);
        }
        Optional<List<Order>> orders = orderRepository.findOrdersByVendorID(vendorID);
        return getOrders(orders);
    }

    private List<Order> getOrders(Optional<List<Order>> orders) throws NoOrdersException {
        if(orders.isEmpty()){
            throw new NoOrdersException();
        }
        List<Order> result = orders.get();
        for (Order o : result) {
            o.setListOfDishes(new ArrayList<>(o.getListOfDishes()));
        }
        return result;
    }

    public List<Order> getOrdersFromCostumerAtVendor(UUID vendorID, UUID customerID) throws VendorNotFoundException, CustomerNotFoundException, NoOrdersException {
        if(!orderRepository.existsByVendorID(vendorID)){
            throw new VendorNotFoundException(vendorID);
        }
        if(!orderRepository.existsByCustomerID(customerID)){
            throw new CustomerNotFoundException(customerID);
        }
        Optional<List<Order>> orders = orderRepository.findOrdersByVendorIDAndCustomerID(vendorID, customerID);
        return getOrders(orders);
    }

    public Integer getOrderVolume(UUID vendorID) throws VendorNotFoundException, NoOrdersException {
        if(!orderRepository.existsByVendorID(vendorID)){
            throw new VendorNotFoundException(vendorID);
        }
        Optional<Integer> res = orderRepository.countOrderByVendorID(vendorID);
        if(res.isEmpty()){
            throw new NoOrdersException();
        }
        return res.get();
    }

    public List<Integer> getOrderVolumeByTime(UUID vendorID) throws VendorNotFoundException, NoOrdersException {
        List<Order> orders = getOrdersFromVendor(vendorID);
        int[] times = new int[24];
        for(Order o : orders){
            long t = o.getDate().longValueExact();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(t);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            times[hours]++;
        }
        return Arrays.stream(times).boxed().collect(Collectors.toList());
    }


    public List<Dish> getDishesSortedByVolume(UUID vendorID) throws VendorNotFoundException, DishNotFoundException {
        if(!orderRepository.existsByVendorID(vendorID)){
            throw new VendorNotFoundException(vendorID);
        }
        List<UUID> queryResult = orderRepository.countDishesOccurrencesFromVendor(vendorID)
                .stream().map(o -> (byte[]) o)
                .map(bytes -> ByteBuffer.wrap(bytes).asLongBuffer())
                .map(buff -> new UUID(buff.get(),buff.get()))
                .collect(Collectors.toList());
        List<Dish> result = new ArrayList<>();
        for (UUID row : queryResult) {
            Optional<Dish> cur = dishRepository.findDishByDishID(row);
            if(cur.isEmpty()){
                throw new DishNotFoundException(row);
            }
            result.add(cur.get());
        }
        return result;
    }
}
