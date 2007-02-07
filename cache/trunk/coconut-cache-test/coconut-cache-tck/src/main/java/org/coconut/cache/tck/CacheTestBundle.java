/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.coconut.core.Clock.DeterministicClock;
import org.coconut.test.CollectionUtils;
import org.junit.Before;

/**
 * This is base class that all test bundle should extend.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Header$
 */
public abstract class CacheTestBundle extends Assert {

    protected DeterministicClock clock;

    public static final IntegerToStringLoader DEFAULT_LOADER = new IntegerToStringLoader();

    protected Cache<Integer, String> c;

    protected Cache<Integer, String> loadableEmptyCache;

    protected Cache<Integer, String> c0;

    protected Cache<Integer, String> c1;

    protected Cache<Integer, String> c2;

    protected Cache<Integer, String> c3;

    protected Cache<Integer, String> c4;

    protected Cache<Integer, String> c5;

    protected Cache<Integer, String> c6;

    @Before
    public void setUp() throws Exception {
        clock = new DeterministicClock();
        c0 = newCache(0);
        c1 = newCache(1);
        c2 = newCache(2);
        c3 = newCache(3);
        c4 = newCache(4);
        c5 = newCache(5);
        c6 = newCache(6);
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        loadableEmptyCache = newCache(cc.backend()
                .setBackend(new IntegerToStringLoader()).c());
    }

    final Cache<Integer, String> newCache(int entries) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.setInitialMap(createMap(entries)).setClock(clock);
        return cc.newInstance(TCKRunner.tt);
    }

    @SuppressWarnings("unchecked")
    protected CacheConfiguration<Integer, String> newConf() {
        return CacheConfiguration.create();
    }

    protected Cache<Integer, String> newCache(CacheConfiguration<Integer, String> conf) {
        return conf.newInstance(TCKRunner.tt);
    }

    public static Map<Integer, String> createMap(int entries) {
        if (entries < 0 || entries > 26) {
            throw new IllegalArgumentException();
        }
        Map<Integer, String> map = new HashMap<Integer, String>(entries);
        for (int i = 1; i <= entries; i++) {
            map.put(i, "" + (char) (i + 64));
        }

        return map;
    }

    protected boolean containsKey(Map.Entry<Integer, String> e) {
        return c.containsKey(e.getKey());
    }

    protected boolean containsValue(Map.Entry<Integer, String> e) {
        return c.containsValue(e.getValue());
    }

    protected String peek(Map.Entry<Integer, String> e) {
        return c.peek(e.getKey());
    }

    protected String put(Map.Entry<Integer, String> e) {
        return c.put(e.getKey(), e.getValue());
    }

    protected String put(Map.Entry<Integer, String> e, long timeout, TimeUnit unit) {
        return c.put(e.getKey(), e.getValue(), timeout, unit);
    }

    protected String put(Map.Entry<Integer, String> e, long timeout) {
        return c.put(e.getKey(), e.getValue(), timeout, TimeUnit.MILLISECONDS);
    }

    protected void evict() {
        c.evict();
    }

    protected String remove(Map.Entry<Integer, String> e) {
        return c.remove(e.getKey());
    }

    protected void assertSize(int size) {
        assertEquals(size, c.size());
    }

    protected void assertNullGet(String msg, Map.Entry<Integer, String> e) {
        assertNull(msg, get(e));
    }

    protected void assertNullGet(Map.Entry<Integer, String> e) {
        assertNullGet(new Map.Entry[] { e });
    }

    protected void assertNullGet(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertNull(get(entry));
        }
    }

    protected void assertNullPeek(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertNull(peek(entry));
        }
    }

    protected void assertPeek(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertEquals(entry.getValue(), c.peek(entry.getKey()));
        }
    }

    protected void assertPeekEntry(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            CacheEntry<Integer, String> ee = c.peekEntry(entry.getKey());
            assertEquals(ee.getValue(), entry.getValue());
            assertEquals(ee.getKey(), entry.getKey());
        }
    }

    protected void assertGet(Map.Entry<Integer, String> e) {
        assertEquals(e.getValue(), c.get(e.getKey()));
    }

    protected void assertGetEntry(Map.Entry<Integer, String> e) {
        CacheEntry<Integer, String> ee = c.getEntry(e.getKey());
        assertEquals(ee.getValue(), e.getValue());
        assertEquals(ee.getKey(), e.getKey());
    }

    protected void assertGet(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertEquals(entry.getValue(), c.get(entry.getKey()));
        }
    }

    protected void assertGetAll(Map.Entry<Integer, String>... e) {
        Map<Integer, String> all = getAll(e);
        for (Map.Entry<Integer, String> entry : all.entrySet()) {
            assertEquals(entry.getValue(), CollectionUtils.getValue(entry.getKey()));
        }
    }

    protected String get(Map.Entry<Integer, String> e) {
        return c.get(e.getKey());
    }

    protected CacheEntry<Integer, String> peekEntry(Map.Entry<Integer, String> e) {
        return c.peekEntry(e.getKey());
    }

    protected CacheEntry<Integer, String> getEntry(Map.Entry<Integer, String> e) {
        return c.getEntry(e.getKey());
    }

    protected Map<Integer, String> getAll(Map.Entry<Integer, String>... e) {
        return c.getAll(CollectionUtils.asMap(e).keySet());
    }

    protected void waitAndAssertGet(Map.Entry<Integer, String>... e)
            throws InterruptedException {
        for (Map.Entry<Integer, String> m : e) {
            for (int i = 0; i < 100; i++) {
                if (c.get(m.getKey()).equals(m.getValue())) {
                    break;
                } else {
                    Thread.sleep(15);
                }
                if (i == 99) {
                    throw new AssertionError("Value did not change");
                }
            }
        }
    }

    protected void putAll(Map.Entry<Integer, String>... entries) {
        c.putAll(CollectionUtils.asMap(entries));
    }

//    protected CacheQuery<Integer, String> keyQuery(Filter<Integer> filter) {
//        return CacheFilters.queryByKey(c, filter);
//    }

    protected void putAll(long timeout, TimeUnit unit,
            Map.Entry<Integer, String>... entries) {
        c.putAll(CollectionUtils.asMap(entries), timeout, unit);
    }

    protected void putAll(long timeout, Map.Entry<Integer, String>... entries) {
        putAll(timeout, TimeUnit.NANOSECONDS, entries);
    }

    protected void incTime() {
        clock.incrementTimestamp(1);
    }

    protected void incTime(int amount) {
        clock.incrementTimestamp(amount);
    }

    /**
     * Assert method for hit statistics.
     * 
     * @param ratio
     *            the expected ratio of the cache
     * @param hits
     *            the expected number of hits
     * @param misses
     *            the expected number of misses
     * @param hitstat
     *            the HitStat to compare against
     */
    protected static void assertHitstat(float ratio, long hits, long misses,
            Cache.HitStat hitstat) {
        Assert.assertEquals(ratio, hitstat.getHitRatio(), 0.0001);
        Assert.assertEquals(hits, hitstat.getNumberOfHits());
        Assert.assertEquals(misses, hitstat.getNumberOfMisses());
    }
}
