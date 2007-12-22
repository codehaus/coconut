/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.cache.tck.CacheTCKImplementationSpecifier;
import org.coconut.cache.tck.CacheTCKRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests {@link SynchronizedCache}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RunWith(CacheTCKRunner.class)
@CacheTCKImplementationSpecifier(SynchronizedCache.class)
public class SynchronizedCacheTest {

    @Test
    public void prestart() {
        SynchronizedCache c = new SynchronizedCache();
        assertFalse(c.isStarted());
        c.prestart();
        assertTrue(c.isStarted());
    }
}