/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.CacheEntryFilter;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public class ExpirationWithCacheLoader extends CacheTestBundle {
    @Before
    public void setup() {
        c = newCache(newConf().setClock(clock).backend().setBackend(
                new IntegerToStringLoader()).c());
    }

    /**
     * Tests that values are loaded for expired elements if a cache loader is
     * defined.
     */
    @Test
    public void loadingOfExpiredEntries() {
        c.put(M1.getKey(), "~A", 1, TimeUnit.NANOSECONDS);
        c.put(M2.getKey(), "~B", 1, TimeUnit.NANOSECONDS);
        c.put(M3.getKey(), "~C", 1, TimeUnit.NANOSECONDS);
        c.put(M4.getKey(), "~D", 1, TimeUnit.NANOSECONDS);
        assertEquals("~A", c.get(M1.getKey()));
        assertGet(M1);
        assertGetAll(M2, M3);
        assertGetEntry(M4);
    }

    @Test
    public void testCreationTime() {
        CacheEntryFilter f = new CacheEntryFilter();
        c = newCache(newConf().setClock(clock).backend().setBackend(
                new IntegerToStringLoader()).c().expiration().setFilter(f).c());
        clock.incrementTimestamp();
        c.put(M1.getKey(), "AB");
        long time = getEntry(M1).getCreationTime();
        f.setAccept(true); // entries are evicted, explicity load new ones
        assertEquals(time, getEntry(M1).getCreationTime());
    }
}
