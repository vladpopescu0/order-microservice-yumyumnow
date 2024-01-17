package nl.tudelft.sem.template.order.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import nl.tudelft.sem.template.model.Address;
import nl.tudelft.sem.template.model.Dish;
import nl.tudelft.sem.template.model.Order;
import nl.tudelft.sem.template.order.PersistentBagMock;
import nl.tudelft.sem.template.order.domain.helpers.FilteringParam;
import nl.tudelft.sem.template.order.domain.user.repositories.DishRepository;
import nl.tudelft.sem.template.order.domain.user.repositories.OrderRepository;
import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.hibernate.collection.internal.PersistentBag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    transient Dish d1;
    transient Dish d1CopyResult;
    transient Dish d2;
    transient Address a1;
    transient Address a2;
    transient Order order1;
    transient Order order1CopyResult;
    transient Order order2;
    transient List<String> ingredients = new ArrayList<>();
    transient Order order3;
    transient List<Order> orders;
    @Mock
    private transient OrderRepository orderRepository;
    @Mock
    private transient DishRepository dishRepository;
    @Mock
    private transient UserMicroServiceService userMicroServiceService;
    @InjectMocks
    private transient OrderService orderService;

    @BeforeEach
    void setup() {
        d1 = new Dish();
        d1.setDishID(UUID.randomUUID());
        d1.setDescription("very tasty");
        d1.setImage("img");
        d1.setName("Pizza");
        d1.setPrice(5.0f);
        List<String> allergies = new ArrayList<>();
        allergies.add("lactose");
        PersistentBag pbAllergies = new PersistentBagMock();
        pbAllergies.addAll(allergies);
        d1.setListOfAllergies(pbAllergies);
        ingredients = new ArrayList<>();
        ingredients.add("Cheese");
        ingredients.add("Salami");
        ingredients.add("Tomato Sauce");
        PersistentBag pbIngredients = new PersistentBagMock();
        pbIngredients.addAll(ingredients);
        d1.setListOfIngredients(pbIngredients);
        d1.setVendorID(UUID.randomUUID());

        d1CopyResult = new Dish();
        d1CopyResult.setDishID(d1.getDishID());
        d1CopyResult.setDescription("very tasty");
        d1CopyResult.setImage("img");
        d1CopyResult.setName("Pizza");
        d1CopyResult.setPrice(5.0f);
        d1CopyResult.setListOfAllergies(allergies);
        ingredients = new ArrayList<>();
        ingredients.add("Cheese");
        ingredients.add("Salami");
        ingredients.add("Tomato Sauce");
        d1CopyResult.setListOfIngredients(ingredients);
        d1CopyResult.setVendorID(d1.getVendorID());

        d2 = new Dish();
        d2.setDishID(d1.getVendorID());
        d2.setDescription("very tasty");
        d2.setImage("img");
        d2.setName("Lasagna");
        d2.setPrice(10.0f);
        List<String> allergies2 = new ArrayList<>();
        allergies2.add("lactose");
        allergies2.add("gluten");
        d2.setListOfAllergies(allergies2);
        List<String> ingredients2 = new ArrayList<>();
        ingredients2.add("Gluten");
        ingredients2.add("Cheese");
        ingredients2.add("Tomato Sauce");
        d2.setListOfIngredients(ingredients2);
        d2.setVendorID(UUID.randomUUID());

        Address a1 = new Address();
        a1.setStreet("Mekelweg 5");
        a1.setCity("Delft");
        a1.setCountry("Netherlands");
        a1.setZip("2628CC");

        List<UUID> listOfDishes1 = List.of(d1.getDishID(), d2.getDishID());
        PersistentBag pbDishes = new PersistentBagMock();
        pbDishes.addAll(listOfDishes1);

        order1 = new Order();
        order1.setOrderID(UUID.randomUUID());
        order1.setVendorID(d1.getVendorID());
        order1.setCustomerID(UUID.randomUUID());
        order1.setAddress(a1);
        order1.setDate(new BigDecimal("1700007405000"));
        order1.setListOfDishes(pbDishes);
        order1.setSpecialRequirements("Knock on the door");
        order1.setOrderPaid(true);
        order1.setStatus(Order.StatusEnum.DELIVERED);
        order1.setRating(4);

        order1CopyResult = new Order();
        order1CopyResult.setOrderID(order1.getOrderID());
        order1CopyResult.setVendorID(order1.getVendorID());
        order1CopyResult.setCustomerID(order1.getCustomerID());
        order1CopyResult.setAddress(a1);
        order1CopyResult.setDate(new BigDecimal("1700007405000"));
        order1CopyResult.setListOfDishes(listOfDishes1);
        order1CopyResult.setSpecialRequirements("Knock on the door");
        order1CopyResult.setOrderPaid(true);
        order1CopyResult.setStatus(Order.StatusEnum.DELIVERED);
        order1CopyResult.setRating(4);

        Address a2 = new Address();
        a2.setStreet("Mekelweg 9");
        a2.setCity("Delft");
        a2.setCountry("Netherlands");
        a2.setZip("2628CD");

        order2 = new Order();
        order2.setOrderID(UUID.randomUUID());
        order2.setVendorID(d1.getVendorID());
        order2.setCustomerID(UUID.randomUUID());
        order2.setAddress(a1);
        order2.setDate(new BigDecimal("1790006416060"));
        order2.setListOfDishes(Collections.singletonList(d1.getDishID()));
        order2.setSpecialRequirements("Knock on the door");
        order2.setOrderPaid(true);
        order2.setStatus(Order.StatusEnum.ACCEPTED);
        order2.setRating(4);
        order2.setRating(3);

        order3 = new Order();
        order3.setOrderID(UUID.randomUUID());
        order3.setVendorID(UUID.randomUUID());
        order3.setCustomerID(order1.getCustomerID());
        order3.setAddress(a2);
        order3.setDate(new BigDecimal("1700006405030"));
        order3.setListOfDishes(List.of(UUID.randomUUID()));
        order3.setSpecialRequirements("The bell doesn't work");
        order3.setOrderPaid(false);
        order3.setStatus(Order.StatusEnum.DELIVERED);
        order3.setRating(3);
        orders = new ArrayList<>();
    }

    @Test
    void testCheckUUIDIsUnique_WhenExists() {
        UUID existingUUID = UUID.randomUUID();
        when(orderRepository.existsByOrderID(existingUUID)).thenReturn(true);

        boolean isUnique = orderService.checkUUIDIsUnique(existingUUID);

        Assertions.assertTrue(isUnique);
    }

    @Test
    void testCheckUUIDIsUnique_WhenNotExists() {
        UUID nonExistingUUID = UUID.randomUUID();
        when(orderRepository.existsByOrderID(nonExistingUUID)).thenReturn(false);

        boolean isUnique = orderService.checkUUIDIsUnique(nonExistingUUID);

        Assertions.assertFalse(isUnique);
    }

    @Test
    void testCreateOrderSuccessful() throws OrderIdAlreadyInUseException,
            NullFieldException, VendorNotFoundException, CustomerNotFoundException {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(orderRepository.save(order1)).thenReturn(order1);

        Order savedOrder = orderService.createOrder(order1);

        Assertions.assertEquals(savedOrder, order1CopyResult);

    }

    @Test
    void testCreateOrderIdTaken() throws OrderIdAlreadyInUseException {

        UUID takenId = order1.getOrderID();
        when(orderService.checkUUIDIsUnique(takenId)).thenReturn(true);

        Assertions.assertThrows(OrderIdAlreadyInUseException.class,
                () -> orderService.createOrder(order1));

    }

    @Test
    void testCreateVendorDoesNotExist() {
        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(false);

        Assertions.assertThrows(VendorNotFoundException.class,
                () -> orderService.createOrder(order1));
    }

    @Test
    void testCreateCustomerDoesNotExist() throws OrderIdAlreadyInUseException {
        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(false);

        Assertions.assertThrows(CustomerNotFoundException.class,
                () -> orderService.createOrder(order1));
    }

    @Test
    void testCreateOrderNullOrder() {

        Assertions.assertThrows(NullFieldException.class,
                () -> orderService.createOrder(null));

    }

    @Test
    void testCreateOrderNullField() {

        Assertions.assertThrows(NullFieldException.class, () -> orderService.createOrder(null));

    }

    @Test
    void testGetOrderByIdNullId() {

        Assertions.assertThrows(NullFieldException.class, () -> orderService.getOrderById(null));

    }

    @Test
    void testGetAllOrdersSuccessful() throws NoOrdersException {

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orderList = orderService.getAllOrders();

        Assertions.assertTrue(orderList.contains(order1CopyResult));
        Assertions.assertTrue(orderList.contains(order2));
        Assertions.assertEquals(2, orderList.size());

    }

    @Test
    void testGetAllOrdersNoOrders() {

        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        Assertions.assertThrows(NoOrdersException.class, () -> orderService.getAllOrders());
    }

    @Test
    void testGetOrderByIdSuccessful() throws OrderNotFoundException, NullFieldException {

        when(orderRepository.findOrderByOrderID(order1.getOrderID()))
                .thenReturn(Optional.of(order1));

        Order returned = orderService.getOrderById(order1.getOrderID());

        Assertions.assertEquals(returned, order1CopyResult);
    }

    @Test
    void testGetOrderByIdNull() {

        Assertions.assertThrows(NullFieldException.class,
                () -> orderService.getOrderById(null));

    }

    @Test
    void testGetOrderByIdNotFound() throws OrderNotFoundException {

        when(orderRepository.findOrderByOrderID(order1.getOrderID()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrderById(order1.getOrderID()));

    }

    @Test
    void testEditOrderByIDSuccessful() throws OrderNotFoundException, NullFieldException,
            VendorNotFoundException, CustomerNotFoundException {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        order1.setRating(2);

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        when(orderRepository.save(order1)).thenReturn(order1);

        Order edited = orderService.editOrderByID(order1.getOrderID(), order1);
        order1CopyResult.setRating(2);
        Assertions.assertEquals(edited, order1CopyResult);

    }

    @Test
    void testEditOrderByIDVendorDoesNotExist() {
        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(false);

        Assertions.assertThrows(VendorNotFoundException.class,
                () -> orderService.createOrder(order1));
    }

    @Test
    void testEditOrderByIDCustomerDoesNotExist() throws OrderIdAlreadyInUseException {
        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(false);

        Assertions.assertThrows(CustomerNotFoundException.class,
                () -> orderService.createOrder(order1));
    }

    @Test
    void testEditOrderByIDNotFound() {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);

        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.editOrderByID(order1.getOrderID(), order1));

    }

    @Test
    void testEditOrderByIDNullId() {

        Assertions.assertThrows(NullFieldException.class,
                () -> orderService.editOrderByID(null, order1));

    }

    @Test
    void testEditOrderByIDNullOrder() {

        Assertions.assertThrows(NullFieldException.class,
                () -> orderService.editOrderByID(order1.getOrderID(), null));

    }

    @Test
    void testDeleteOrderByIDSuccessful() throws OrderNotFoundException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);

        orderService.deleteOrderByID(order1.getOrderID());

        Mockito.verify(orderRepository, Mockito.times(1)).deleteById(order1.getOrderID());

    }

    @Test
    void testDeleteOrderByIDNotFound() {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrderByID(order1.getOrderID()));

        Mockito.verify(orderRepository, Mockito.never()).deleteById(order1.getOrderID());

    }

    @Test
    void testOrderIsPaid_WhenOrderExistsAndIsPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Order paidOrder = new Order();
        paidOrder.setOrderID(orderID);
        paidOrder.setOrderPaid(true);

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.of(paidOrder));

        boolean isPaid = orderService.orderIsPaid(orderID);

        Assertions.assertTrue(isPaid);
    }

    @Test
    void testOrderIsPaid_WhenOrderExistsAndIsNotPaid() throws OrderNotFoundException {
        UUID orderID = UUID.randomUUID();
        Order unpaidOrder = new Order();
        unpaidOrder.setOrderID(orderID);
        unpaidOrder.setOrderPaid(false);

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.of(unpaidOrder));

        boolean isPaid = orderService.orderIsPaid(orderID);

        Assertions.assertFalse(isPaid);
    }

    @Test
    void testOrderIsPaid_WhenOrderDoesNotExist() {
        UUID nonExistingOrderID = UUID.randomUUID();

        when(orderRepository.existsByOrderID(nonExistingOrderID)).thenReturn(false);

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.orderIsPaid(nonExistingOrderID));
    }

    @Test
    void getOrdersFromCostumerAtVendor_VendorDoesNotExist() {
        UUID nonExistingVendorID = UUID.randomUUID();
        when(userMicroServiceService.checkVendorExists(nonExistingVendorID)).thenReturn(false);

        Assertions.assertThrows(VendorNotFoundException.class,
                () -> orderService.getOrdersFromCustomerAtVendor(nonExistingVendorID, order1.getCustomerID()));
    }

    @Test
    void getOrdersFromCostumerAtVendor_CustomerDoesNotExist() {
        UUID nonExistingCustomerID = UUID.randomUUID();
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(nonExistingCustomerID)).thenReturn(false);

        Assertions.assertThrows(CustomerNotFoundException.class,
                () -> orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), nonExistingCustomerID));
    }

    @Test
    void getOrdersFromCostumerAtVendor_EmptyOrder() {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);

        when(orderRepository.findOrdersByVendorIDAndCustomerID(order1.getVendorID(),
                order1.getCustomerID())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoOrdersException.class,
                () -> orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), order1.getCustomerID()));
    }

    @Test
    void getOrdersFromCostumerAtVendor_NonEmptyResult()
            throws VendorNotFoundException, CustomerNotFoundException, NoOrdersException {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(orderRepository.findOrdersByVendorIDAndCustomerID(order1.getVendorID(),
                order1.getCustomerID())).thenReturn(Optional.of(List.of(order1, order2)));

        List<Order> result = orderService.getOrdersFromCustomerAtVendor(order1.getVendorID(), order1.getCustomerID());

        assertThat(result).isEqualTo(List.of(order1CopyResult, order2));
    }

    @Test
    void getOrderVolume_VendorDoesNotExist() {
        UUID nonExistingVendorID = UUID.randomUUID();
        when(userMicroServiceService.checkVendorExists(nonExistingVendorID)).thenReturn(false);

        Assertions.assertThrows(VendorNotFoundException.class, () -> orderService.getOrderVolume(nonExistingVendorID));
    }

    @Test
    void getOrderVolume_NoOrders() {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(orderRepository.countOrderByVendorID(order1.getVendorID())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoOrdersException.class, () -> orderService.getOrderVolume(order1.getVendorID()));
    }

    @Test
    void getOrderVolume_WithOrders() throws VendorNotFoundException, NoOrdersException {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(orderRepository.countOrderByVendorID(order1.getVendorID()))
                .thenReturn(Optional.of(21));

        assertThat(orderService.getOrderVolume(order1.getVendorID())).isEqualTo(21);
    }

    @Test
    void getDishesSortedByVolume_VendorDoesNotExist() {
        UUID nonExistingVendorID = UUID.randomUUID();

        when(userMicroServiceService.checkVendorExists(nonExistingVendorID)).thenReturn(false);

        Assertions.assertThrows(VendorNotFoundException.class,
                () -> orderService.getDishesSortedByVolume(nonExistingVendorID));
    }

    @Test
    void getDishesSortedByVolume_DishesFound() throws VendorNotFoundException, DishNotFoundException {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(orderRepository.countDishesOccurrencesFromVendor(order1.getVendorID())).thenReturn(List.of(d2, d1));

        List<Dish> res = orderService.getDishesSortedByVolume(order1.getVendorID());
        assertThat(res.size()).isEqualTo(2);
        assertThat(res.get(0)).isEqualTo(d2);
        assertThat(res.get(1)).isEqualTo(d1CopyResult);
    }

    @Test
    void getOrderVolumeByTime_VendorNotFound() {
        UUID nonExistingVendorID = UUID.randomUUID();
        when(userMicroServiceService.checkVendorExists(nonExistingVendorID)).thenReturn(false);

        Assertions.assertThrows(VendorNotFoundException.class, () -> orderService.getOrderVolumeByTime(nonExistingVendorID));
    }

    @Test
    void getOrderVolumeByTime_NoOrders() throws VendorNotFoundException, NoOrdersException {
        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(orderRepository.findOrdersByVendorID(order1.getVendorID())).thenReturn(Optional.empty());

        Assertions.assertThrows(NoOrdersException.class, () -> orderService.getOrderVolumeByTime(order1.getVendorID()));
    }

    @Test
    void getOrderVolumeByTime_LargeTest() throws VendorNotFoundException, NoOrdersException {
        int[] time = new int[24];
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Random r = new Random();
            long cur = r.nextInt();
            Order o = new Order();
            o.setDate(BigDecimal.valueOf(cur));
            o.setListOfDishes(new ArrayList<>());
            orders.add(o);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cur);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            time[hours] += 1;
        }
        List<Integer> correctTime = new ArrayList<>();
        for (int j : time) {
            correctTime.add(j);
        }

        when(userMicroServiceService.checkVendorExists(order1.getVendorID())).thenReturn(true);
        when(orderRepository.findOrdersByVendorID(order1.getVendorID())).thenReturn(Optional.of(orders));

        List<Integer> volume = orderService.getOrderVolumeByTime(order1.getVendorID());
        assertThat(volume).isEqualTo(correctTime);
    }

    @Test
    void testOrderHistoryCustomerDoesNotExist() {
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(false);
        Assertions.assertThrows(CustomerNotFoundException.class,
                () -> orderService.getPastOrdersByCustomerID(order1.getCustomerID(), Mockito.mock(FilteringParam.class)));
    }

    @Test
    void testOrderHistoryContainsValues() throws NoOrdersException, CustomerNotFoundException {
        when(userMicroServiceService.checkUserExists(order1.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order3.getCustomerID())).thenReturn(true);

        orders.add(order1);
        orders.add(order3);

        when(orderRepository.findOrdersByCustomerID(order1.getCustomerID())).thenReturn(Optional.of(orders));

        FilteringParam<Order> filteringParam = Mockito.mock(FilteringParam.class);
        when(filteringParam.filtering(order3)).thenReturn(true);
        when(filteringParam.filtering(order1)).thenReturn(false);

        List<Order> assertion = orderService.getPastOrdersByCustomerID(order1.getCustomerID(), filteringParam);
        Assertions.assertEquals(assertion.get(0), order3);
        Assertions.assertEquals(assertion.size(), 1);

    }

    @Test
    void testOrderHistoryHasNoAvailablePastOrders() {
        when(userMicroServiceService.checkUserExists(order3.getCustomerID())).thenReturn(true);
        when(userMicroServiceService.checkUserExists(order3.getCustomerID())).thenReturn(true);

        order3.setStatus(Order.StatusEnum.PENDING);
        orders.add(order1);
        orders.add(order3);

        when(orderRepository.findOrdersByCustomerID(order1.getCustomerID())).thenReturn(Optional.of(orders));

        FilteringParam<Order> filteringParam = Mockito.mock(FilteringParam.class);
        when(filteringParam.filtering(order3)).thenReturn(false);
        when(filteringParam.filtering(order1)).thenReturn(false);

        Assertions.assertThrows(NoOrdersException.class,
                () -> orderService.getPastOrdersByCustomerID(order1.getCustomerID(), filteringParam));

    }

    @Test
    void testOrderHistoryNotInDatabase() {
        UUID randomUUID = UUID.randomUUID();
        when(userMicroServiceService.checkUserExists(randomUUID)).thenReturn(true);
        when(orderRepository.findOrdersByCustomerID(randomUUID)).thenReturn(Optional.empty());

        FilteringParam<Order> filteringParam = Mockito.mock(FilteringParam.class);

        Assertions.assertThrows(NoOrdersException.class,
                () -> orderService.getPastOrdersByCustomerID(randomUUID, filteringParam));
        verifyNoInteractions(filteringParam);
    }

    @Test
    void testOrderIsPaidUpdateIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(orderRepository.findOrderByOrderID(randomId)).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.orderIsPaidUpdate(randomId));
    }

    @Test
    void testOrderIsPaidUpdateTrueToFalseCase() throws OrderNotFoundException {
        when(orderRepository.findOrderByOrderID(order1.getOrderID())).thenReturn(Optional.of(order1));
        Order o1 = orderService.orderIsPaidUpdate(order1.getOrderID());
        Assertions.assertFalse(o1.getOrderPaid());
        Mockito.verify(orderRepository, Mockito.times(1)).updateOrderPayment(false, order1.getOrderID());
    }

    @Test
    void testOrderIsPaidUpdateFalseToTrueCase() throws OrderNotFoundException {
        order1.setOrderPaid(false);
        when(orderRepository.findOrderByOrderID(order1.getOrderID())).thenReturn(Optional.of(order1));
        Order o1 = orderService.orderIsPaidUpdate(order1.getOrderID());
        Assertions.assertTrue(o1.getOrderPaid());
        Mockito.verify(orderRepository, Mockito.times(1)).updateOrderPayment(true, order1.getOrderID());
    }

    @Test
    void testAddDishToOrder_addSuccessfully() throws OrderNotFoundException, NullFieldException, DishNotFoundException {

        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        order1.setListOfDishes(new ArrayList<>());
        List<UUID> result = new ArrayList<>();
        result.add(dishID);

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(dishRepository.existsByDishID(dishID)).thenReturn(true);
        when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.of(order1));

        Order order = orderService.addDishToOrder(orderID, dishID);
        Assertions.assertEquals(order.getListOfDishes(), result);

    }

    @Test
    void testAddDishToOrder_nullOrderID() {

        Assertions.assertThrows(NullFieldException.class,
                () -> orderService.addDishToOrder(null, d1.getDishID()));

    }

    @Test
    void testAddDishToOrder_orderNotFound() {

        UUID orderID = order1.getOrderID();

        when(orderRepository.existsByOrderID(orderID)).thenReturn(false);

        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.addDishToOrder(orderID, d1.getDishID()));

    }

    @Test
    void testAddDishToOrder_dishNotFound() {

        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(dishRepository.existsByDishID(dishID)).thenReturn(false);

        Assertions.assertThrows(DishNotFoundException.class, () -> orderService.addDishToOrder(orderID, dishID));

    }

    @Test
    void testAddDishToOrder_orderOptionEmpty() {

        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(dishRepository.existsByDishID(dishID)).thenReturn(true);
        when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.empty());

        Assertions.assertThrows(NullFieldException.class, () -> orderService.addDishToOrder(orderID, dishID));

    }

    @Test
    void testRemoveDishFromOrder_removeSuccessfully()
            throws OrderNotFoundException, NullFieldException, DishNotFoundException {

        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        order1.setListOfDishes(new ArrayList<>(Collections.singletonList(dishID)));

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(dishRepository.existsByDishID(dishID)).thenReturn(true);
        when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.of(order1));

        Order order = orderService.removeDishFromOrder(orderID, dishID);
        List<UUID> result = new ArrayList<>();
        Assertions.assertEquals(order.getListOfDishes(), result);

    }

    @Test
    void testRemoveDishFromOrder_nullOrderID() {

        Assertions.assertThrows(NullFieldException.class,
                () -> orderService.removeDishFromOrder(null, d1.getDishID()));

    }

    @Test
    void testRemoveDishFromOrder_nullDishId() {

        Assertions.assertThrows(NullFieldException.class,
                () -> orderService.removeDishFromOrder(order1.getOrderID(), null));

    }

    @Test
    void testRemoveDishFromOrder_orderNotFound() {

        UUID orderID = order1.getOrderID();

        when(orderRepository.existsByOrderID(orderID)).thenReturn(false);

        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.removeDishFromOrder(orderID, d1.getDishID()));

    }

    @Test
    void testRemoveDishFromOrder_dishNotFound() {

        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(dishRepository.existsByDishID(dishID)).thenReturn(false);

        Assertions.assertThrows(DishNotFoundException.class, () -> orderService.removeDishFromOrder(orderID, dishID));

    }

    @Test
    void testRemoveDishFromOrder_orderOptionNull() {

        UUID orderID = order1.getOrderID();
        UUID dishID = d1.getDishID();

        order1.setListOfDishes(null);

        when(orderRepository.existsByOrderID(orderID)).thenReturn(true);
        when(dishRepository.existsByDishID(dishID)).thenReturn(true);
        when(orderRepository.findOrderByOrderID(orderID)).thenReturn(Optional.empty());

        Assertions.assertThrows(NullFieldException.class, () -> orderService.removeDishFromOrder(orderID, dishID));

    }

    @Test
    public void getStatusOfOrderSuccessful() throws OrderNotFoundException {

        when(orderRepository.findOrderByOrderID(order1.getOrderID())).thenReturn(Optional.of(order1));
        String status = orderService.getStatusOfOrderById(order1.getOrderID());
        Assertions.assertEquals("delivered", status);

    }

    @Test
    public void getStatusOfOrderNotFound() {

        when(orderRepository.findOrderByOrderID(order1.getOrderID())).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.getStatusOfOrderById(order1.getOrderID()));

    }

    @Test
    public void updateStatusOfOrderSuccessful() throws OrderNotFoundException, InvalidOrderStatusException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        orderService.updateStatusOfOrderById(order1.getOrderID(), "ACCEPTED");

        Mockito.verify(orderRepository, Mockito.times(1)).updateOrderStatus(Order.StatusEnum.ACCEPTED, order1.getOrderID());
    }

    @Test
    public void updateStatusOfOrderSuccessfulLowerCase() throws OrderNotFoundException, InvalidOrderStatusException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        orderService.updateStatusOfOrderById(order1.getOrderID(), "rejected");

        Mockito.verify(orderRepository, Mockito.times(1)).updateOrderStatus(Order.StatusEnum.REJECTED, order1.getOrderID());
    }

    @Test
    public void updateStatusOfOrderSuccessfulMixedCase() throws OrderNotFoundException, InvalidOrderStatusException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        orderService.updateStatusOfOrderById(order1.getOrderID(), "PendinG");

        Mockito.verify(orderRepository, Mockito.times(1)).updateOrderStatus(Order.StatusEnum.PENDING, order1.getOrderID());

    }


    @Test
    public void updateStatusOfOrderNotFound() {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);
        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.updateStatusOfOrderById(order1.getOrderID(), "REJECTED"));

    }

    @Test
    public void updateStatusOfOrderInvalidStatus() {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        Assertions.assertThrows(InvalidOrderStatusException.class,
                () -> orderService.updateStatusOfOrderById(order1.getOrderID(), "GREEN"));

    }

    @Test
    public void allStatusesAreValid() {

        Assertions.assertTrue(orderService.isValidStatusEnumType("pending"));
        Assertions.assertTrue(orderService.isValidStatusEnumType("accepted"));
        Assertions.assertTrue(orderService.isValidStatusEnumType("REJECTED"));
        Assertions.assertTrue(orderService.isValidStatusEnumType("PREPARING"));
        Assertions.assertTrue(orderService.isValidStatusEnumType("Given to courier"));
        Assertions.assertTrue(orderService.isValidStatusEnumType("On-transit"));
        Assertions.assertTrue(orderService.isValidStatusEnumType("Delivered"));

    }

    @Test
    public void isValidStatusEnumFalse() {

        Assertions.assertFalse(orderService.isValidStatusEnumType(null));
        Assertions.assertFalse(orderService.isValidStatusEnumType("random string"));

    }

    @Test
    void getRatingOfOrderSuccessful() throws OrderNotFoundException {

        when(orderRepository.findOrderByOrderID(order1.getOrderID())).thenReturn(Optional.of(order1));
        Integer rating = orderService.getOrderRatingByID(order1.getOrderID());
        Assertions.assertEquals(4, rating);

    }

    @Test
    void getRatingOfOrderNotFound() {

        when(orderRepository.findOrderByOrderID(order1.getOrderID())).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.getOrderRatingByID(order1.getOrderID()));

    }

    @Test
    void editOrderRatingSuccessful() throws OrderNotFoundException, InvalidOrderRatingException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        when(orderRepository.findOrderByOrderID(order1.getOrderID())).thenReturn(Optional.of(order1));
        when(orderRepository.save(order1)).thenReturn(order1);
        Order edited = orderService.editOrderRatingByID(order1.getOrderID(), 3);
        Assertions.assertEquals(3, edited.getRating());
        Mockito.verify(orderRepository, Mockito.times(1)).save(order1);

    }

    @Test
    void editOrderRatingInvalidRating() throws OrderNotFoundException, InvalidOrderRatingException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        Assertions.assertThrows(InvalidOrderRatingException.class, ()
                -> orderService.editOrderRatingByID(order1.getOrderID(), 0));
        Mockito.verify(orderRepository, Mockito.never()).save(order1);

    }

    @Test
    void editOrderRatingInvalidRating2() throws OrderNotFoundException, InvalidOrderRatingException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(true);
        Assertions.assertThrows(InvalidOrderRatingException.class, ()
                -> orderService.editOrderRatingByID(order1.getOrderID(), 10));
        Mockito.verify(orderRepository, Mockito.never()).save(order1);

    }

    @Test
    void editOrderRatingNotFound() throws OrderNotFoundException, InvalidOrderRatingException {

        when(orderService.checkUUIDIsUnique(order1.getOrderID())).thenReturn(false);
        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.editOrderRatingByID(order1.getOrderID(), 3));
        Mockito.verify(orderRepository, Mockito.never()).save(order1);

    }


}
