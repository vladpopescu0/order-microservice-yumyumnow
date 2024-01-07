package nl.tudelft.sem.template.order.domain.helpers;

import nl.tudelft.sem.template.order.commons.Order;

public class FilteringByStatus implements FilteringParam<Order> {
    private final Order.StatusEnum status;

    /**
     * constructor.
     *
     * @param status needed status to filter on
     */
    public FilteringByStatus(Order.StatusEnum status) {
        this.status = status;
    }

    /**
     * Filtering method for any status parameter.
     *
     * @param order the order on which to apply the filtering
     *
     * @return true if the order has that status
     */
    @Override
    public boolean filtering(Order order) {
        return order.getStatus() == status;
    }
}
