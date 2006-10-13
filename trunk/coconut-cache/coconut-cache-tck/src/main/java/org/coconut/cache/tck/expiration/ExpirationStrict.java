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

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheConfiguration.ExpirationStrategy;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.AsyncIntegerToStringLoader;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * These test bundle tests the expiration strategy <tt>Strict</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExpirationStrict extends CacheTestBundle {

    @Before
    public void setUpCaches() {
        c = newCache(newConf().setClock(clock).expiration().setStrategy(getStrategy())
                .c());
        fillItUp();
    }

    protected ExpirationStrategy getStrategy() {
        return ExpirationStrategy.STRICT;
    }

    private void fillItUp() {
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
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
     * Tests a custom expiration filter.
     */
    @Test
    public void customExpirationFilter() {
        ExpirationFilter f = new ExpirationFilter();
        c = newCache(newConf().setClock(clock).expiration().setFilter(f).setStrategy(
                getStrategy()).c());
        fillItUp();

        incTime(3);
        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(3); // time still has influence

        f.doAccept = true;
        assertNullGet(M3);
        assertEquals(M3.getKey(), f.lastEntry.getKey());
        assertEquals(M3.getValue(), f.lastEntry.getValue());

        c.getAll(M1_TO_M5_KEY_SET);
        assertSize(0); // time has no influence any more

        // TODO f.lastEntry.equals(c.getEntry(M1.getKey));
    }

    /**
     * Tests that values are loaded for expired elements if a cache loader is
     * defined.
     */
    @Test
    public void strictLoading() {
        ExpirationFilter f = new ExpirationFilter();
        c = newCache(newConf().setClock(clock).backend().setLoader(
                new IntegerToStringLoader()).c().expiration().setFilter(f).setStrategy(
                getStrategy()).c());
        c.put(M1.getKey(), "AA");
        c.put(M2.getKey(), "AB");
        c.put(M3.getKey(), "AC");
        c.put(M4.getKey(), "AD");
        c.put(M5.getKey(), "AE");
        c.put(M6.getKey(), "AF");
        assertEquals("AA", c.get(M1.getKey()));
        f.doAccept = true; // entries are evicted, explicity load new ones
        assertGet(M1);
        assertGetAll(M2, M3, M4, M5);
        assertNullGet(M6);
    }

    @Test
    public void strictCacheEntry() {
        ExpirationFilter f = new ExpirationFilter();
        c = newCache(newConf().setClock(clock).backend().setLoader(
                new IntegerToStringLoader()).c().expiration().setFilter(f).setStrategy(
                getStrategy()).c());
        clock.incrementAbsolutTime();
        c.put(M1.getKey(), "AB");
        CacheEntry<Integer, String> ce = getEntry(M1);
        assertEquals("AB", ce.getValue());
        long creationTime = ce.getCreationTime();
        f.doAccept = true; // entries are evicted, explicity load new ones
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
        c = newCache(newConf().setClock(clock).expiration().setStrategy(
                ExpirationStrategy.STRICT).setPreExpirationRefreshTime(2, TimeUnit.NANOSECONDS).c()
                .backend().setLoader(loader).c());
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
        c = newCache(newConf().setClock(clock).expiration().setStrategy(
                ExpirationStrategy.STRICT).setPreExpirationRefreshTime(2, TimeUnit.NANOSECONDS).c()
                .backend().setLoader(loader).c());
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
