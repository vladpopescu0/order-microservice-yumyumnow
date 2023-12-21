package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;


public class OrderNotFoundException extends Exception {

    static final long serialVersionUID = -2351302066197971681L;

    public OrderNotFoundException(@Valid UUID orderID) {
        super(orderID.toString());
    }
}
