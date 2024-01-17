package nl.tudelft.sem.template.order.domain.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.domain.helpers.FilteringParam;
import nl.tudelft.sem.template.order.domain.user.repositories.DishRepository;
import nl.tudelft.sem.template.order.domain.user.repositories.OrderRepository;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OrderService {
    private final transient OrderRepository orderRepository;
    private final transient DishRepository dishRepository;
    private final transient UserMicroServiceService userMicroServiceService;

    /**
     * Instantiates a new OrderService.
     *
     * @param orderRepository   the order repository
     * @param userMicroServiceService the user microService
     * @param dishRepository    the dish repository
     */
    @Autowired
    public OrderService(OrderRepository orderRepository, UserMicroServiceService userMicroServiceService,
                        DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.userMicroServiceService = userMicroServiceService;
        this.dishRepository = dishRepository;
    }

    /**
     * Checks whether a certain vendor exists.
     *
     * @param vendorId UUID of the vendor
     * @return Boolean specifying whether the vendor exists or not
     */
    private boolean checkVendorExists(UUID vendorId) {
        return userMicroServiceService.checkVendorExists(vendorId);
    }

    /**
     * Checks whether a certain user exists.
     *
     * @param userId UUID of the vendor
     * @return Boolean specifying whether the user exists or not
     */
    private boolean checkUserExists(UUID userId) {
        return userMicroServiceService.checkUserExists(userId);
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
    public Order createOrder(Order order) throws OrderIdAlreadyInUseException,
            NullFieldException, VendorNotFoundException, CustomerNotFoundException {
        if (order == null) {
            throw new NullFieldException();
        }
        if (checkUUIDIsUnique(order.getOrderID())) {
            throw new OrderIdAlreadyInUseException(order.getOrderID());
        }
        return saveOrder(order);

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
    public Order getOrderById(UUID orderID) throws OrderNotFoundException, NullFieldException {
        verifyNullField(orderID);
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
    public Order editOrderByID(UUID orderID, Order order) throws OrderNotFoundException,
            NullFieldException, VendorNotFoundException, CustomerNotFoundException {

        verifyNullField(orderID);
        if (order == null) {
            throw new NullFieldException();
        }

        verifyOrderIDExistence(orderID);

        return saveOrder(order);

    }

    /**
     * Saves an order to the database.
     *
     * @param order Order to be saved
     * @return Saved order from the database
     * @throws CustomerNotFoundException if the customer could not be found
     * @throws VendorNotFoundException if the vendor could not be found
     */
    private Order saveOrder(Order order) throws CustomerNotFoundException, VendorNotFoundException {
        if (!checkUserExists(order.getCustomerID())) {
            throw new CustomerNotFoundException(order.getOrderID());
        }

        if (!checkVendorExists(order.getVendorID())) {
            throw new VendorNotFoundException(order.getVendorID());
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
        verifyOrderIDExistence(orderID);
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
        verifyOrderIDExistence(orderID);
        Optional<Order> currentOrder = orderRepository.findOrderByOrderID(orderID);

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
        if (!checkVendorExists(vendorID)) {
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
    public List<Order> getOrdersFromCustomerAtVendor(UUID vendorID, UUID customerID)
            throws VendorNotFoundException, CustomerNotFoundException, NoOrdersException {
        if (!checkVendorExists(vendorID)) {
            throw new VendorNotFoundException(vendorID);
        }
        if (!checkUserExists(customerID)) {
            throw new CustomerNotFoundException(vendorID);
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
        if (!checkVendorExists(vendorID)) {
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
            times[hours] += 1;
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
     */
    public List<Dish> getDishesSortedByVolume(UUID vendorID) throws VendorNotFoundException {
        if (!checkVendorExists(vendorID)) {
            throw new VendorNotFoundException(vendorID);
        }
        List<Dish> res = orderRepository.countDishesOccurrencesFromVendor(vendorID);
        for (Dish d : res) {
            d.setListOfIngredients(new ArrayList<>(d.getListOfIngredients()));
            d.setListOfAllergies(new ArrayList<>(d.getListOfAllergies()));
        }
        return res;
    }

    /**
     * The implementation of the orderISPaid put method from the controllers.
     *
     * @param orderID the id of the order to flip the isPaid value
     * @throws OrderNotFoundException when the method cannot find the order in the database
     */
    public Order orderIsPaidUpdate(UUID orderID) throws OrderNotFoundException {
        Optional<Order> currentOrder = orderRepository.findOrderByOrderID(orderID);
        if (currentOrder.isEmpty()) {
            throw new OrderNotFoundException(orderID);
        }
        orderRepository.updateOrderPayment(!currentOrder.get().getOrderPaid(), orderID);

        Order o = currentOrder.get();
        o.setOrderPaid(!o.getOrderPaid());
        return o;
    }

    /**
     * Get all past completed orders of a user based on their ID.
     *
     * @param customerID the id of the customer on which the SQL query is based
     *
     * @param filteringParam the filtering param I want to consider as correct, here
     *                       could be "delivered"
     *
     * @return the list of all delivered orders of a specific customer
     * @throws NoOrdersException when there are no orders in the database or no orders of this specific customerID
     */
    public List<Order> getPastOrdersByCustomerID(UUID customerID,
                                                 FilteringParam<Order> filteringParam) throws NoOrdersException,
            CustomerNotFoundException {
        if (!checkUserExists(customerID)) {
            throw new CustomerNotFoundException(customerID);
        }
        Optional<List<Order>> customerOrders = orderRepository.findOrdersByCustomerID(customerID);
        if (customerOrders.isEmpty()) {
            throw new NoOrdersException();
        }
        List<Order> fromOptional = customerOrders.get();
        fromOptional = fromOptional.stream().filter(filteringParam::filtering).collect(Collectors.toList());
        if (fromOptional.isEmpty()) {
            throw new NoOrdersException();
        }
        return fromOptional;
    }

    /**
     * Adds a dish to an order based on their IDs.
     *
     * @param orderID   the ID of the order to add the dish to
     * @param dishID    the ID of the dish to be added to the order
     * @return the order, now updated with the new dish
     * @throws NullFieldException when orderID or dishID are null
     * @throws OrderNotFoundException when the orderID does not exist
     * @throws DishNotFoundException when the dishID does not exist
     */
    public Order addDishToOrder(UUID orderID, UUID dishID)
            throws NullFieldException, OrderNotFoundException, DishNotFoundException {
        verifyNullField(orderID);
        verifyNullField(dishID);

        verifyOrderIDExistence(orderID);
        verifyDishIDExistence(dishID);

        Optional<Order> order = orderRepository.findOrderByOrderID(orderID);
        if (order.isEmpty()) {
            throw new NullFieldException();
        }
        order.get().addListOfDishesItem(dishID);

        return order.get();
    }

    /**
     * Removes a dish from an order based on their IDs.
     *
     * @param orderID   the ID of the order to remove the dish from
     * @param dishID    the ID of the dish to be removed from the order
     * @return the order, now updated
     * @throws NullFieldException when orderID or dishID are null
     * @throws OrderNotFoundException when the orderID does not exist
     * @throws DishNotFoundException when the dishID does not exist
     */
    public Order removeDishFromOrder(UUID orderID, UUID dishID)
            throws NullFieldException, OrderNotFoundException, DishNotFoundException {
        verifyNullField(orderID);
        verifyNullField(dishID);

        verifyOrderIDExistence(orderID);
        verifyDishIDExistence(dishID);

        Optional<Order> order = orderRepository.findOrderByOrderID(orderID);
        if (order.isEmpty()) {
            throw new NullFieldException();
        }

        order.get().getListOfDishes().remove(dishID);

        return order.get();
    }

    /**
     * Verifies if a UUID id is null.
     *
     * @param uuid the id to be verified
     * @throws NullFieldException if the id is null
     */
    private static void verifyNullField(UUID uuid) throws NullFieldException {
        if (uuid == null) {
            throw new NullFieldException();
        }
    }

    /**
     * Verifies if an id of an order exists.
     *
     * @param orderID the id to be verified
     * @throws OrderNotFoundException if the orderID doesn't exist
     */
    private void verifyOrderIDExistence(UUID orderID) throws OrderNotFoundException {
        if (!orderRepository.existsByOrderID(orderID)) {
            throw new OrderNotFoundException(orderID);
        }
    }

    /**
     * Verifies if an id of a dish exists.
     *
     * @param dishID the id to be verified
     * @throws DishNotFoundException if the dishID doesn't exist
     */
    private void verifyDishIDExistence(UUID dishID) throws DishNotFoundException {
        if (!dishRepository.existsByDishID(dishID)) {
            throw new DishNotFoundException(dishID);
        }
    }

    /**
     * Method for getting the status of a specific Order in the database.
     *
     * @param orderID ID specifying the Order
     * @return StatusEnum corresponding to the status of the specified Order
     * @throws OrderNotFoundException - thrown when the orderID isn't found
     */
    public String getStatusOfOrderById(UUID orderID) throws OrderNotFoundException {
        Optional<Order> order = orderRepository.findOrderByOrderID(orderID);
        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderID);
        }

        return order.get().getStatus().toString();
    }

    /**
     * Method for editing the status of an Order in the database.
     *
     * @param orderID ID specifying the Order
     * @param status New status as String
     * @throws OrderNotFoundException - thrown when the orderID isn't found
     */
    public void updateStatusOfOrderById(UUID orderID, String status)
            throws OrderNotFoundException, InvalidOrderStatusException {
        verifyOrderIDExistence(orderID);

        if (!isValidStatusEnumType(status)) {
            throw new InvalidOrderStatusException(orderID);
        }

        Order.StatusEnum modified = Order.StatusEnum.valueOf(status.toUpperCase(Locale.ENGLISH));

        orderRepository.updateOrderStatus(modified, orderID);

    }

    /**
     * Method for verifying whether a String is a valid status code.
     *
     * @param s String to verify
     * @return return true when s is a valid status, false otherwise
     */
    public boolean isValidStatusEnumType(String s) {
        if (s == null) {
            return false;
        }
        for (Order.StatusEnum e : Order.StatusEnum.values()) {
            if (e.toString().equalsIgnoreCase(s)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method for getting the order of a specific Order in the database.
     *
     * @param orderID ID specifying the Order
     * @return int corresponding to the order of the specified Order
     * @throws OrderNotFoundException - thrown when the orderID isn't found
     */
    public Integer getOrderRatingByID(UUID orderID) throws OrderNotFoundException {

        Optional<Order> order = orderRepository.findOrderByOrderID(orderID);
        if (order.isEmpty()) {
            throw new OrderNotFoundException(orderID);
        }

        return order.get().getRating();

    }

    /**
     * Method for editing the status of an Order in the database.
     *
     * @param orderID ID specifying the Order
     * @param rating New rating as String
     * @return Edited Order
     * @throws OrderNotFoundException - thrown when the orderID isn't found
     */
    public Order editOrderRatingByID(UUID orderID, Integer rating)
            throws OrderNotFoundException, InvalidOrderRatingException {
        if (!checkUUIDIsUnique(orderID)) {
            throw new OrderNotFoundException(orderID);
        }

        if (rating < 1 || rating > 5) {
            throw new InvalidOrderRatingException(orderID);
        }

        Order order = orderRepository.findOrderByOrderID(orderID).get();
        order.setRating(rating);
        order = orderRepository.save(order);

        return order;

    }

}
