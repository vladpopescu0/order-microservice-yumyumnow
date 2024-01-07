package nl.tudelft.sem.template.order.domain.helpers;

//this can be used for both orders and dishes when using the generic operator
public interface FilteringParam<T> {
    boolean filtering(T item);
}
