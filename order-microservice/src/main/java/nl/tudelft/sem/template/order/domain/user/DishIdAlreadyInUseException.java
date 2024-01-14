package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class DishIdAlreadyInUseException extends Exception {
    static final long serialVersionUID = -981720398172309871L;

    public DishIdAlreadyInUseException(@Valid UUID dishID) {
        super(dishID.toString());
    }
}
