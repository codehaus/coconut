package org.coconut.cache.test.util.managed;

import org.coconut.filter.Predicate;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;

public class ManagedFilter implements ManagedObject, Predicate {
    ManagedGroup g;

    public void manage(ManagedGroup parent) {
        g = parent;
    }

    public boolean evaluate(Object element) {
        return false;
    }

    public ManagedGroup getManagedGroup() {
        return g;
    }
}
