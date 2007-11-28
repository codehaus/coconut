/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util.lifecycle;

import org.coconut.predicate.Predicate;

public class LifecycleFilter extends AbstractLifecycleVerifier implements Predicate {
    public boolean evaluate(Object element) {
        return false;
    }
}
