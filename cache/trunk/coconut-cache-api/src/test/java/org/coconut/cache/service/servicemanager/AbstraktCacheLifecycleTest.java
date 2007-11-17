/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class AbstraktCacheLifecycleTest {

    @Test
    public void defaultName() {
        assertEquals("MyLifecycle", new MyLifecycle().getName());
    }

    static class MyLifecycle extends AbstractCacheLifecycle {

    }
}
