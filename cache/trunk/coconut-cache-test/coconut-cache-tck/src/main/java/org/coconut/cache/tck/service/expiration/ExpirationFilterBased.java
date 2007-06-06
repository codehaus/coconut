/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.testutil.CacheEntryFilter;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests various scenarios with filter-based expiration
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public class ExpirationFilterBased extends AbstractExpirationTestBundle {

    private CacheEntryFilter f;

    @Before
    public void setUpCaches() {
        f = new CacheEntryFilter();
        c = newCache(newConf().expiration().setExpirationFilter(f).c());
    }

    /**
     * Tests a custom expiration filter with get.
     */
    @Test
    public void testFilterGet() {
        put(M1);
        assertGet(M1);
        f.setAccept(true);
        assertNullGet(M1);
        f.assertLastEquals(M1);
    }

    /**
     * Tests a custom expiration filter with getEntry.
     */
    @Test
    public void testFilterGetEntry() {
        put(M2);
        assertGetEntry(M2);
        f.setAccept(true);
        assertNullGet(M2);
        assertNull(c.getEntry(M2.getKey()));
    }

    /**
     * Tests a custom expiration filter with getAll.
     */
    @Test
    public void testFilterGetAll() {
        putAll(M3, M4, M5);
        assertGetAll(M3, M4, M5);
        f.setAccept(true);
        assertNullGet(M3, M4);
        assertSize(1);
        assertNullGet(M5);
        assertSize(0);
    }

    /**
     * Tests that time based expiration still works even though a filter is
     * defined.
     */
    public void testFilterAndTimeExpiration() {
        put(M1, 5);
        put(M2, 7);
        incTime(5);
        assertNullGet(M1);
        assertGet(M2);
        f.setAccept(true);
        assertNullGet(M2);
    }

    /**
     * Tests that default time based expiration still works even though a filter
     * is defined.
     */
    public void testFilterAndDefaultTimeExpiration() {
        c = newCache(newConf().expiration().setExpirationFilter(f).c().setClock(clock).expiration()
                .setDefaultTimeToLive(5, TimeUnit.NANOSECONDS).c());
        put(M1);
        put(M2);
        assertGet(M1, M2);
        incTime(5);
        assertNullGet(M1);
        f.setAccept(true);
        assertNullGet(M2);
    }
}
