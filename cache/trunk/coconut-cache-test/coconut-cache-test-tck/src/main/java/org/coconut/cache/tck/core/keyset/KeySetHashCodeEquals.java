/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.keyset;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests non modifying actions for a caches value set
 * {@link org.coconut.cache.Cache#keySet()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: KeySet.java 392 2007-10-07 11:24:10Z kasper $
 */
public class KeySetHashCodeEquals extends AbstractCacheTCKTest {

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        // assertTrue(c5.values().equals(c5.values()));
        c = newCache();
        assertFalse(c.keySet().equals(null));
        assertFalse(c.keySet().equals(newCache(1).keySet()));
        c = newCache(5);
        assertFalse(c.keySet().equals(null));
        assertFalse(c.keySet().equals(newCache(4).keySet()));
        assertFalse(c.keySet().equals(newCache(6).keySet()));
    }

    @Test
    public void testHashCode() {
    // assertEquals(c5.values().hashCode(), c5.values().hashCode());
    }

}
