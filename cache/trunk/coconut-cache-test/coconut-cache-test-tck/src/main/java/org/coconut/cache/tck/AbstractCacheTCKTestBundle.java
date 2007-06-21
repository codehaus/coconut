package org.coconut.cache.tck;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;

import junit.framework.Assert;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheServices;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.statistics.CacheHitStat;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.core.Clock.DeterministicClock;
import org.coconut.management.ManagedGroup;
import org.coconut.test.CollectionUtils;
import org.junit.Before;

public class AbstractCacheTCKTestBundle extends Assert {
    protected Cache<Integer, String> c;

    protected DeterministicClock clock;

    @Before
    public void setupClock() throws Exception {
        clock = new DeterministicClock();
    }

    protected Cache<Integer, String> newCache() {
        return newCache(0);
    }

    protected final Cache<Integer, String> newCache(int entries) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.setClock(clock);
        Cache<Integer, String> c = cc.newInstance(CacheTCKRunner.tt);
        c.putAll(createMap(entries));
        return c;
    }

    @SuppressWarnings("unchecked")
    protected CacheConfiguration<Integer, String> newConf() {
        return CacheConfiguration.create();
    }

    protected Cache<Integer, String> newCache(AbstractCacheServiceConfiguration<?, ?> conf) {
        return (Cache) conf.c().newInstance(CacheTCKRunner.tt);
    }

    protected void setCache(AbstractCacheServiceConfiguration<?, ?> conf) {
        c = newCache(conf);
    }

    protected void setCache(CacheConfiguration<?, ?> conf) {
        c = newCache(conf);
    }

    protected Cache<Integer, String> newCache(CacheConfiguration<?, ?> conf) {
        return (Cache) conf.newInstance(CacheTCKRunner.tt);
    }

    protected Cache<Integer, String> newCache(CacheConfiguration<?, ?> conf, int entries) {
        Cache<Integer, String> cache = newCache(conf);
        cache.putAll(createMap(entries));
        return cache;
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

    protected String peek(Integer e) {
        return c.peek(e);
    }

    protected String peek(Map.Entry<Integer, String> e) {
        return c.peek(e.getKey());
    }

    protected Collection<Map.Entry<Integer, String>> put(int to) {
        return put(1, to);
    }

    protected Collection<Map.Entry<Integer, String>> put(int from, int to) {
        for (int i = from; i <= to; i++) {
            c.put(i, "" + (char) (i + 64));
        }
        return new ArrayList<Map.Entry<Integer, String>>(c.entrySet());
    }

    protected String put(Map.Entry<Integer, String> e) {
        return c.put(e.getKey(), e.getValue());
    }

    protected String put(Integer key, String value) {
        return c.put(key, value);
    }

    protected final void evict() {
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

    protected void touch(Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            c.get(entry.getKey());
        }
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
            CacheHitStat hitstat) {
        Assert.assertEquals(ratio, hitstat.getHitRatio(), 0.0001);
        Assert.assertEquals(hits, hitstat.getNumberOfHits());
        Assert.assertEquals(misses, hitstat.getNumberOfMisses());
    }

    protected final CacheExpirationService<Integer, String> expiration() {
        return c.getService(CacheExpirationService.class);
    }

    protected final CacheEvictionService<Integer, String> eviction() {
        return c.getService(CacheEvictionService.class);
    }
    protected final CacheStatisticsService statistics() {
        return c.getService(CacheStatisticsService.class);
    }
    protected final CacheLoadingService<Integer, String> loading() {
        return c.getService(CacheLoadingService.class);
    }

    protected final CacheManagementService management() {
        return c.getService(CacheManagementService.class);
    }

    protected <T> T findMXBean(Class<T> clazz) {
        return findMXBean(ManagementFactory.getPlatformMBeanServer(), clazz);
    }

    protected <T> T findMXBean(MBeanServer server, Class<T> clazz) {
        Collection<ManagedGroup> found = new ArrayList<ManagedGroup>();
        doFindMXBeans(found, CacheServices.management(c).getRoot(), clazz);
        if (found.size() == 0) {
            throw new IllegalArgumentException("Did not find any service " + clazz);
        } else if (found.size() == 1) {
            T proxy = MBeanServerInvocationHandler.newProxyInstance(server, found
                    .iterator().next().getObjectName(), clazz, false);
            return proxy;
        } else {
            throw new IllegalArgumentException("Duplicate service " + clazz);
        }
    }

    private static <T> void doFindMXBeans(Collection<ManagedGroup> col,
            ManagedGroup group, Class<T> c) {
        for (ManagedGroup mg : group.getChildren()) {
            for (Object o : mg.getObjects()) {
                if (c.isAssignableFrom(o.getClass())) {
                    col.add(mg);
                }
            }
            doFindMXBeans(col, mg, c);
        }
    }
}
