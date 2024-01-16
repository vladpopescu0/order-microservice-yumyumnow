package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class InvalidOrderStatusException extends Exception {

    static final long serialVersionUID = 6283086150129137323L;

    public InvalidOrderStatusException(@Valid UUID orderID) {
        super(orderID.toString());
    }

}
