/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.eventbus;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * A cache that does not support an event bus should be tested against this
 * feature.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoEventBusSupport extends CacheTestBundle {

    /**
     * Asserts that a getEventBus on a cache throws an
     * UnsupportedOperationException.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetEventBus() {
        c0.getEventBus();
    }
}