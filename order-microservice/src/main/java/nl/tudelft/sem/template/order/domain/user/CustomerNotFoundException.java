package nl.tudelft.sem.template.order.domain.user;

import javax.validation.Valid;
import java.util.UUID;

public class CustomerNotFoundException extends Throwable {
    static final long serialVersionUID = -2349230984293483094L;

    public CustomerNotFoundException(@Valid UUID customerID) {
        super(customerID.toString());
    }
}
