/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util.lifecycle;

import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.management.ManagedLifecycle;

public class LifecycleManagedVerifier extends LifecycleVerifier implements
        ManagedLifecycle {

    LifecycleManagedVerifier(LifecycleVerifierContext context, CacheLifecycle decorator, int id) {
        super(context, decorator, id);
    }
}
