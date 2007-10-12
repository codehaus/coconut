package org.coconut.cache.test.util.managed;

import org.coconut.filter.Filter;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;

public class ManagedFilter implements ManagedObject, Filter {
    ManagedGroup g;

    public void manage(ManagedGroup parent) {
        g = parent;
    }

    public boolean accept(Object element) {
        return false;
    }

    public ManagedGroup getManagedGroup() {
        return g;
    }
}
