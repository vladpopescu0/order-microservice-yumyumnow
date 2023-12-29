package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class OrderIdAlreadyInUseException extends Exception {
    public  OrderIdAlreadyInUseException(@Valid UUID orderID) {
        super(orderID.toString());
    }

}
