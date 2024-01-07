package nl.tudelft.sem.template.order.domain.helpers;

import nl.tudelft.sem.template.order.commons.Order;

public class FilteringByStatus implements FilteringParam<Order> {
    private final Order.StatusEnum status;

    public FilteringByStatus(Order.StatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean filtering(Order order) {
        return order.getStatus() == status;
    }
}
