package nl.tudelft.sem.template.order.domain.user;

import javax.validation.Valid;
import java.util.UUID;

public class OrderNotPaidException extends Exception {
    static final long serialVersionUID = 267685864181180026L;
    public OrderNotPaidException(@Valid UUID orderID) {
        super(orderID.toString());
    }
}
