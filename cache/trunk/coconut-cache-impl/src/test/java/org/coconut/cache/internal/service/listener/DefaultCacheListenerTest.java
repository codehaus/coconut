/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.listener;

import org.junit.Test;

public class DefaultCacheListenerTest {

    @Test
    public void toString_() {
        new DefaultCacheListener(null).toString();
    }
}
