/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.test.util.CacheEntryFilter;
import org.coconut.cache.test.util.lifecycle.LifecyclePredicate;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests various scenarios with filter-based expiration.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExpirationFilter extends AbstractExpirationTestBundle {

    private CacheEntryFilter f;

    @Before
    public void setUpCaches() {
        f = new CacheEntryFilter();
        c = newCache(newConf().expiration().setExpirationFilter(f));
    }

    /**
     * Tests a custom expiration filter with get.
     */
    @Test
    public void testFilterGet() {
        put(M1);
        assertGet(M1);
        f.setAccept(true);// All entries have expired
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
        f.setAccept(true);// All entries have expired
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
        f.setAccept(false);
        assertGet(M5);
        assertSize(1);
        f.setAccept(true);
        assertNullGet(M5);
        assertSize(0);
    }

    /**
     * Tests that time based expiration still works even though a filter is defined.
     */
    @Test
    public void testFilterAndTimeExpiration() {
        c = newCache(newConf().expiration().setExpirationFilter(f));
        put(M1, 5);
        put(M2, 7);
        incTime(5);
        assertNullGet(M1);
        assertGet(M2);
        f.setAccept(true);
        assertNullGet(M2);
    }

    /**
     * Tests that default time based expiration still works even though a filter is
     * defined.
     */
    @Test
    public void testFilterAndDefaultTimeExpiration() {
        c = newCache(newConf().expiration().setExpirationFilter(f)
                .setDefaultTimeToLive(5, TimeUnit.MILLISECONDS).c());
        put(M1);
        put(M2);
        assertGet(M1, M2);
        incTime(5);
        assertNullGet(M1);
        f.setAccept(true);
        assertNullGet(M2);
    }

    /**
     * Tests that a expiration filter implementing CacheLifecycle is attached to the
     * cache's lifecycle.
     */
    @Test
    public void lifecycle() {
        CacheConfiguration<Integer, String> cc = newConf();
        LifecyclePredicate filter = new LifecyclePredicate();
        c = newCache(cc.expiration().setExpirationFilter(filter));
        filter.assertInitializedButNotStarted();
        prestart();
        filter.assertInStartedPhase();
        filter.shutdownAndAssert(c);
    }
}
