package nl.tudelft.sem.template.order;

import org.hibernate.collection.internal.PersistentBag;

import java.util.*;

public class PersistentBagMock extends PersistentBag implements List {
    protected List bag;
    public PersistentBagMock() {
        bag = new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(Collection values) {
        bag.addAll(values);
        return values.size() > 0;
    }

    @Override
    public Object[] toArray() {
        return bag.toArray();
    }

}
