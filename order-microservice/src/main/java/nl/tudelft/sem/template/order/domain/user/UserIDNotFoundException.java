package nl.tudelft.sem.template.order.domain.user;

import javax.validation.Valid;
import java.util.UUID;

public class UserIDNotFoundException extends Exception{

    static final long serialVersionUID = 619864618761L;

    public UserIDNotFoundException(@Valid UUID userID){
        super(userID.toString());
    }

}
