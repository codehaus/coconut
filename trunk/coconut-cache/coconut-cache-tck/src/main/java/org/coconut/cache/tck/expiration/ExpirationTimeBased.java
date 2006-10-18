/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_KEY_SET;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.AsyncIntegerToStringLoader;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExpirationTimeBased extends CacheTestBundle {
    @Before
    public void setUpCaches() {
        c = newCache(newConf().setClock(clock));
        c0 = newCache(newConf().setClock(clock).expiration().setDefaultTimeout(10,
                TimeUnit.NANOSECONDS).c());
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
    }

    /**
     * Tests that the expiration of puts (with timeout) are done according to
     * the relative time at insertation and not from 0. That is, an element with
     * a 10 s expiration inserted at time 5 should expire at time 15 and not
     * time 10.
     */
    @Test
    public void relativePut() {
        c = c0;
        put(M1, 10);

        incTime(5);
        put(M2, 10);

        incTime(5);
        evict();
        assertNullGet(M1);
        assertGet(M2);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M2);
    }

    /**
     * Tests that the expiration of putAlls (with timeout) are done according to
     * the relative time at insertation and not from 0.
     */
    @Test
    public void relativePutAll() {
        c = c0;
        putAll(10, M1, M2);

        incTime(5);
        putAll(10, M3, M4);

        incTime(5);
        evict();
        assertNullGet(M1, M2);
        assertGet(M3, M4);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M3, M4);
    }

    /**
     * Tests that the expiration of puts (with default timeout) are done
     * according to the relative time at insertation and not from 0.
     */
    @Test
    public void relativePutDefaultTimeOut() {
        c = c0;
        put(M1);

        incTime(5);
        put(M2);

        incTime(5);
        evict();
        assertNullGet("Cache did not expire M1 element", M1);
        assertGet(M2);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M2);
    }

    /**
     * Tests that the expiration of putAll (with default timeout) are done
     * according to the relative time at insertation and not from 0.
     */
    @Test
    public void relativePutAllDefaultTimeOut() {
        c = c0;
        putAll(M1, M2);

        incTime(5);
        putAll(M3, M4);

        incTime(5);
        evict();
        assertNullGet(M1, M2);
        assertGet(M3, M4);

        incTime(5);
        evict();

        incTime(1);
        assertNullGet(M3, M4);
    }

    /**
     * Tests that when inserting (put) an element that already exist in the
     * cache the expiration time of that element is overridden.
     */
    @Test
    public void overrideEviction() {
        put(M3, 100);
        // M3,M4 expires as time=4
        incTime(4);
        evict();
        assertGet(M3);
        assertNullGet(M4);
        incTime(96);
        evict();
        assertNullGet(M3);
    }

    /**
     * Tests that when inserting (putAll) an element that already exist in the
     * cache the expiration time of that element is overridden.
     */
    @Test
    public void overrideEvictionPutAll() {
        put(M3, 100, TimeUnit.NANOSECONDS);
        // M3,M4 expires as time=4
        incTime(4);
        evict();
        assertGet(M3);
        assertNullGet(M4);
        incTime(96);
        evict();
        assertNull(get(M3));
    }

    /**
     * Tests that when inserting (putAll) an element that already exist in the
     * cache (inserted with the default expiration timeout) the expiration time
     * of that element is overridden.
     */
    @Test
    public void overrideEvictionDefaultExpiration() {
        c = c0;
        put(M1);
        put(M2);
        incTime(5);
        put(M1);

        incTime(5);
        evict();
        assertGet(M1);
        assertNullGet(M2);

        incTime(5);
        evict();
        assertNullGet(M1);
    }
    
    /**
     * Test that at time 2 M1 is expired.
     */
    @Test
    public void singleElementExpiration() {
        incTime();
        assertGet(M1);
        assertSize(5);

        incTime();
        assertNullGet("Element M1 was not expired and removed", M1);
        assertSize(4);
    }

    /**
     * Tests that peek has no side effecs, peek should not check for expiration
     * of elements.
     */
    @Test
    public void singlePeek() {
        incTime();
        assertPeek(M1);

        incTime();
        assertPeek(M1); // now you see it, no check of expiration
        assertNullGet("Element M1 was not expired and removed", M1);
        assertNullPeek(M1); // now you don't (get should have removed the item)
    }

    /**
     * Simple tests that just tests a lot of elements each expiring at different
     * times. size() is normally a constant time operation so we need to 'touch'
     * all elements by calling getAll(all elements) before they expired.
     */
    @Test
    public void manyElements() {
        assertSize(5);

        incTime(); // time1
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(5);

        incTime(); // time2
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(4);

        incTime(); // time3
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(3);

        incTime(); // time4
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(1);

        incTime(); // time5
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(1);

        incTime(); // time6
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(0);
    }

    /**
     * Make sure evict still removes item.
     */
    @Test
    public void evictStrict() {
        incTime(4);
        evict();
        assertSize(1);
    }


    /**
     * Tests that values are loaded for expired elements if a cache loader is
     * defined.
     */
    @Test
    public void strictLoading() {
        ExpirationFilter f = new ExpirationFilter();
        c = newCache(newConf().setClock(clock).backend().setLoader(
                new IntegerToStringLoader()).c().expiration().setFilter(f).c());
        c.put(M1.getKey(), "AA");
        c.put(M2.getKey(), "AB");
        c.put(M3.getKey(), "AC");
        c.put(M4.getKey(), "AD");
        c.put(M5.getKey(), "AE");
        c.put(M6.getKey(), "AF");
        assertEquals("AA", c.get(M1.getKey()));
        f.isExpired = true; // entries are evicted, explicity load new ones
        assertGet(M1);
        assertGetAll(M2, M3, M4, M5);
        assertNullGet(M6);
    }

    @Test
    public void strictCacheEntry() {
        ExpirationFilter f = new ExpirationFilter();
        c = newCache(newConf().setClock(clock).backend().setLoader(
                new IntegerToStringLoader()).c().expiration().setFilter(f).c());
        clock.incrementAbsolutTime();
        c.put(M1.getKey(), "AB");
        CacheEntry<Integer, String> ce = getEntry(M1);
        assertEquals("AB", ce.getValue());
        long creationTime = ce.getCreationTime();
        f.isExpired = true; // entries are evicted, explicity load new ones
        CacheEntry<Integer, String> ce2 = getEntry(M1);
        assertEquals(creationTime, ce2.getCreationTime());
        assertEquals(M1.getValue(), ce2.getValue());
    }

    @Test
    public void evictionOrderTest() {
        // TODO test that eviction does not change when we
        // need to update values
    }

    /**
     * Test refresh window
     */
    @SuppressWarnings("unchecked")
    @Test
    public void refreshWindowSingleElementEvict() throws Exception {
        AsyncIntegerToStringLoader loader = new AsyncIntegerToStringLoader();
        c = newCache(newConf().setClock(clock).expiration().setRefreshInterval(2,
                TimeUnit.NANOSECONDS).c().backend().setLoader(loader).c());
        c.put(M1.getKey(), "AB1", 2, TimeUnit.NANOSECONDS);
        c.put(M2.getKey(), "AB2", 3, TimeUnit.NANOSECONDS);
        c.put(M3.getKey(), "AB3", 4, TimeUnit.NANOSECONDS);
        c.put(M4.getKey(), "AB4", 7, TimeUnit.NANOSECONDS);

        incTime(); // time is one
        // test no refresh on get
        c.evict();
        assertEquals(2, loader.getLoadedKeys().size());
        waitAndAssertGet(M1, M2);

        assertEquals("AB3", get(M3));
        assertEquals("AB4", get(M4));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void refreshWindowSingleElementGet() throws Exception {
        AsyncIntegerToStringLoader loader = new AsyncIntegerToStringLoader();
        c = newCache(newConf().setClock(clock).expiration().setRefreshInterval(2,
                TimeUnit.NANOSECONDS).c().backend().setLoader(loader).c());
        c.put(M1.getKey(), "AB1", 2, TimeUnit.NANOSECONDS);
        c.put(M2.getKey(), "AB2", 3, TimeUnit.NANOSECONDS);
        c.put(M3.getKey(), "AB3", 4, TimeUnit.NANOSECONDS);
        c.put(M4.getKey(), "AB4", 7, TimeUnit.NANOSECONDS);

        incTime(); // time is one
        // test no refresh on get
        getAll(M1, M2, M3, M4);
        assertEquals(2, loader.getLoadedKeys().size());
        waitAndAssertGet(M1, M2);
        assertEquals("AB3", get(M3));
        assertEquals("AB4", get(M4));
    }
    // TODO tests these, actually we should be able to only tests these for the
    // strict protocol, other strategies are "sub" strategy of strict

    // map operations
    // containsValue
    // entrySet
    // getAll
    // keySet
    // peek
    // put -- returns null if element has been evicted??
    // putAll
    // putIfAbsent
    // remove, remove2
    // replace1, replace2
    // size
    //

    // Reload
    // equals
    // hashCode
}
