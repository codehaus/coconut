/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.util;

import static org.junit.Assert.*;

import org.coconut.cache.Cache;
import org.coconut.cache.policy.CostSizeObject;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheEntryTest {

    @Test(expected = NullPointerException.class)
    public void testNullKey() {
        new DefaultCacheEntry(null, "");
    }

    @Test(expected = NullPointerException.class)
    public void testNullValue() {
        new DefaultCacheEntry("", null);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDefaultCacheEntry() {
        DefaultCacheEntry dce = new DefaultCacheEntry("A", "B");
        assertEquals(CostSizeObject.DEFAULT_COST, dce.getCost());
        assertEquals(0l, dce.getCreationTime());
        assertEquals(Cache.DEFAULT_EXPIRATION, dce.getExpirationTime());
        assertEquals(-1l, dce.getHits());
        assertEquals("A", dce.getKey());
        assertEquals(0l, dce.getLastAccessTime());
        assertEquals(0l, dce.getLastUpdateTime());
        assertEquals(CostSizeObject.DEFAULT_SIZE, dce.getSize());
        assertEquals("B", dce.getValue());
        assertEquals(0l, dce.getVersion());
        
        dce.setValue("C");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDefaultCacheEntryExpiration() {
        DefaultCacheEntry dce = DefaultCacheEntry.entryWithExpiration("A", "B", 100);
        assertEquals(CostSizeObject.DEFAULT_COST, dce.getCost());
        assertEquals(100l, dce.getExpirationTime());
        assertEquals("A", dce.getKey());
        assertEquals("B", dce.getValue());
        
        DefaultCacheEntry.entryWithExpiration("A", "B", -1);
    }
}
