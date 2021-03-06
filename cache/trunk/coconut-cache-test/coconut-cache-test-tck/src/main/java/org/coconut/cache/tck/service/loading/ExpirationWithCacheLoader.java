/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.CacheEntryFilter;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExpirationWithCacheLoader extends AbstractCacheTCKTest {
    @Before
    public void setup() {
        c = newCache(newConf().loading().setLoader(
                new IntegerToStringLoader()).c());
    }

    /**
     * Tests that values are loaded for expired elements if a cache loader is
     * defined.
     */
    @Test
    public void loadingOfExpiredEntries() {
        expiration().put(M1.getKey(), "~A", 1, TimeUnit.MILLISECONDS);
        expiration().put(M2.getKey(), "~B", 1, TimeUnit.MILLISECONDS);
        expiration().put(M3.getKey(), "~C", 1, TimeUnit.MILLISECONDS);
        expiration().put(M4.getKey(), "~D", 1, TimeUnit.MILLISECONDS);
        assertEquals("~A", c.get(M1.getKey()));
        incTime(10);
        assertGet(M1);
        assertGetAll(M2, M3);
        assertGetEntry(M4);
    }

    @Test
    public void testCreationTime() {
        CacheEntryFilter f = new CacheEntryFilter();
        c = newCache(newConf().loading().setLoader(
                new IntegerToStringLoader()).c().expiration().setExpirationFilter(
                f));
        clock.incrementTimestamp();
        c.put(M1.getKey(), "AB");
        long time = getEntry(M1).getCreationTime();
        f.setAccept(true); // entries are evicted, explicity load new ones
        assertEquals(time, getEntry(M1).getCreationTime());
    }
}
