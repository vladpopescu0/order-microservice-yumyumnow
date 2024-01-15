package nl.tudelft.sem.template.order.domain.user;

import java.util.UUID;
import javax.validation.Valid;

public class UserIDNotFoundException extends Exception {

    static final long serialVersionUID = 619864618761L;

    public UserIDNotFoundException(@Valid UUID userID) {
        super(userID.toString());
    }

}
