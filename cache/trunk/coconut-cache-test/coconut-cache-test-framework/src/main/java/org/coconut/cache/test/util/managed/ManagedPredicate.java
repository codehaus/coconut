/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util.managed;

import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.operations.Ops.Predicate;

public class ManagedPredicate implements ManagedLifecycle, Predicate {
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
