package nl.tudelft.sem.template.order.domain.user;

import javax.validation.Valid;
import java.util.UUID;

public class VendorNotFoundException extends Exception {
    static final long serialVersionUID = -98071923123323L;
    public VendorNotFoundException(@Valid UUID dishID) {
        super(dishID.toString());
    }
}
