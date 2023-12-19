package nl.tudelft.sem.template.order.domain.user;

import javax.validation.Valid;
import java.util.UUID;

public class DishNotFoundException extends Exception {
    static final long serialVersionUID = -2709348723094872L;
    public DishNotFoundException(@Valid UUID dishID) {
        super(dishID.toString());
    }
}
