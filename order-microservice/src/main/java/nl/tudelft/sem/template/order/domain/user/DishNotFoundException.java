package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class DishNotFoundException extends Exception {
    static final long serialVersionUID = -2709348723094872L;

    public DishNotFoundException(@Valid UUID dishID) {
        super(dishID.toString());
    }
}
