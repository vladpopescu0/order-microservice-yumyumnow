package nl.tudelft.sem.template.order.domain.user;

import javax.validation.Valid;
import java.util.UUID;

public class OrderIdAlreadyInUseException extends Exception{
    public  OrderIdAlreadyInUseException(@Valid UUID orderID){
        super(orderID.toString());
    }

}
