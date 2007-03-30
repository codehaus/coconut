/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.util;

import static org.junit.Assert.assertEquals;

import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.service.expiration.CacheExpirationService;
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
        DefaultCacheEntry dce = DefaultCacheEntry.create("A", "B");
        assertEquals(ReplacementPolicy.DEFAULT_COST, dce.getCost());
        assertEquals(0l, dce.getCreationTime());
        assertEquals(CacheExpirationService.DEFAULT_EXPIRATION, dce.getExpirationTime());
        assertEquals(-1l, dce.getHits());
        assertEquals("A", dce.getKey());
        assertEquals(0l, dce.getLastAccessTime());
        assertEquals(0l, dce.getLastUpdateTime());
        assertEquals(ReplacementPolicy.DEFAULT_SIZE, dce.getSize());
        assertEquals("B", dce.getValue());
        assertEquals(0l, dce.getVersion());
        
        dce.setValue("C");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDefaultCacheEntryExpiration() {
        DefaultCacheEntry dce = DefaultCacheEntry.createWithExpiration("A", "B", 100);
        assertEquals(ReplacementPolicy.DEFAULT_COST, dce.getCost());
        assertEquals(100l, dce.getExpirationTime());
        assertEquals("A", dce.getKey());
        assertEquals("B", dce.getValue());
        
        DefaultCacheEntry.createWithExpiration("A", "B", -1);
    }
}
