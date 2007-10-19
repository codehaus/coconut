/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.values;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests non modifying actions for a caches value set
 * {@link org.coconut.cache.Cache#keySet()}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: KeySet.java 392 2007-10-07 11:24:10Z kasper $
 */
public class ValuesHashCodeEquals extends AbstractCacheTCKTest {

    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        // assertTrue(c5.values().equals(c5.values()));
        c = newCache();
        assertFalse(c.values().equals(null));
        assertFalse(c.values().equals(newCache(1).values()));
        c = newCache(5);
        assertFalse(c.values().equals(null));
        assertFalse(c.values().equals(newCache(4).values()));
        assertFalse(c.values().equals(newCache(6).values()));
    }

    @Test
    public void testHashCode() {
    // assertEquals(c5.values().hashCode(), c5.values().hashCode());
    }

}
