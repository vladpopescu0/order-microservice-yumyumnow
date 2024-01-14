package nl.tudelft.sem.template.order.domain.helpers;

import nl.tudelft.sem.template.order.commons.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FilteringByStatusTests {

    @Test
    public void testFilteringWithMatchingStatus() {
        Order.StatusEnum status = Order.StatusEnum.PENDING;
        FilteringByStatus filteringByStatus = new FilteringByStatus(status);
        Order order = new Order();
        order.setStatus(Order.StatusEnum.PENDING);

        boolean result = filteringByStatus.filtering(order);

        Assertions.assertTrue(result);
    }

    @Test
    public void testFilteringWithNonMatchingStatus() {
        Order.StatusEnum expectedStatus = Order.StatusEnum.ON_TRANSIT;
        Order.StatusEnum differentStatus = Order.StatusEnum.PENDING;
        FilteringByStatus filteringByStatus = new FilteringByStatus(expectedStatus);
        Order order = new Order();
        order.setStatus(differentStatus);

        boolean result = filteringByStatus.filtering(order);

        Assertions.assertFalse(result);
    }

}
