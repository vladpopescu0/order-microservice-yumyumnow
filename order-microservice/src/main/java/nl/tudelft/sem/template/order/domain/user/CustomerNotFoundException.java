package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class CustomerNotFoundException extends Exception {
    static final long serialVersionUID = -2349230984293483094L;

    public CustomerNotFoundException(@Valid UUID customerID) {
        super(customerID.toString());
    }
}
