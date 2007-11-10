/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util.managed;

import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;
import org.coconut.predicate.Predicate;

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
