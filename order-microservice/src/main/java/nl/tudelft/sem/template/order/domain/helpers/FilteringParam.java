package nl.tudelft.sem.template.order.domain.helpers;

//this can be used for both orders and dishes when using the generic operator
public interface FilteringParam<T> {
    /**
     * The field of the class T on which to do specific filtering.
     * Can be used on lists using the stream().filter(...) methods.
     *
     * @param item the class on which the filtering is applied
     *
     * @return should return true if the item has the given properties
     */
    boolean filtering(T item);
}
