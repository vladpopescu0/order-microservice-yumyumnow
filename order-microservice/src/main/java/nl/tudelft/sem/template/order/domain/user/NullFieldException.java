package nl.tudelft.sem.template.order.domain.user;

public class NullFieldException extends Exception {

    static final long serialVersionUID = 7015446391377980156L;

    public NullFieldException() {
        super("Null field");
    }
}
