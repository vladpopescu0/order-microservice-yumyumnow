package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class OrderIdAlreadyInUseException extends Exception {

    static final long serialVersionUID = 3266498579723033976L;

    public  OrderIdAlreadyInUseException(@Valid UUID orderID) {
        super(orderID.toString());
    }

}
