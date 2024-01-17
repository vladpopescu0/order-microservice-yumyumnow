package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class InvalidOrderRatingException extends Exception {

    static final long serialVersionUID = 3551131440816632210L;

    public InvalidOrderRatingException(@Valid UUID orderID) {
        super(orderID.toString());
    }

}
