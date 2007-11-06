/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import junit.framework.Assert;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheServices;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheHitStat;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.test.util.ThreadServiceTestHelper;
import org.coconut.core.Clock.DeterministicClock;
import org.coconut.management.ManagedGroup;
import org.coconut.test.CollectionUtils;
import org.junit.Before;

/**
 * All Cache tests should
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstractCacheTCKTest extends Assert {
    protected Cache<Integer, String> c;

    protected DeterministicClock clock;

    protected ThreadServiceTestHelper threadHelper;

    @Before
    public void setupClock() {
        clock = new DeterministicClock();
    }

    protected void prestart() {
        c.size();
    }

    protected Cache<Integer, String> newCache() {
        return newCache(0);
    }

    protected Cache<Integer, String> newCache(int entries) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.setClock(clock);
        Cache<Integer, String> c = cc.newCacheInstance(CacheTCKRunner.tt);
        if (entries > 0) {
            c.putAll(createMap(entries));
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    protected CacheConfiguration<Integer, String> newConf() {
        return CacheConfiguration.create();
    }

    protected Cache<Integer, String> newCache(AbstractCacheServiceConfiguration<?, ?> conf) {
        return (Cache) conf.c().newCacheInstance(CacheTCKRunner.tt);
    }

    protected void setCache(AbstractCacheServiceConfiguration<?, ?> conf) {
        c = newCache(conf);
    }

    protected void setCache(CacheConfiguration<?, ?> conf) {
        c = newCache(conf);
    }

    protected Cache<Integer, String> newCache(CacheConfiguration<?, ?> conf) {
        return (Cache) conf.newCacheInstance(CacheTCKRunner.tt);
    }

    protected Cache<Integer, String> newCache(
            AbstractCacheServiceConfiguration<?, ?> conf, int entries) {
        return newCache(conf.c(), entries);
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

    protected void checkLazystart() {
        assertTrue(c.isStarted());
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
    protected Collection<Map.Entry<Integer, String>> putAll(int from, int to) {
        Map<Integer,String> m=new HashMap<Integer,String>();
        for (int i = from; i <= to; i++) {
            m.put(i, "" + (char) (i + 64));
        }
        c.putAll(m);
        return new ArrayList<Map.Entry<Integer, String>>(c.entrySet());
    }
    
    protected String put(Map.Entry<Integer, String> e) {
        return c.put(e.getKey(), e.getValue());
    }

    protected String replace(Map.Entry<Integer, String> e) {
        return c.replace(e.getKey(), e.getValue());
    }

    protected String replace(Integer key, String value) {
        return c.replace(key, value);
    }

    protected boolean replace(Integer key, String oldValue, String newValue) {
        return c.replace(key, oldValue, newValue);
    }

    protected String putIfAbsent(Map.Entry<Integer, String> e) {
        return c.putIfAbsent(e.getKey(), e.getValue());
    }

    protected String putIfAbsent(Integer key, String value) {
        return c.putIfAbsent(key, value);
    }

    protected String put(Integer key, String value) {
        return c.put(key, value);
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

    protected void assertNullPeek(String msg, Map.Entry<Integer, String>... e) {
        for (Map.Entry<Integer, String> entry : e) {
            assertNull(msg, peek(entry));
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

    protected String get(int key) {
        return c.get(key);
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
     */
    protected void assertHitstat(float ratio, long hits, long misses) {
        CacheHitStat hitstat = statistics().getHitStat();
        Assert.assertEquals(hits, hitstat.getNumberOfHits());
        Assert.assertEquals(misses, hitstat.getNumberOfMisses());
        Assert.assertEquals(ratio, hitstat.getHitRatio(), 0.0001);
    }

    protected final CacheExpirationService<Integer, String> expiration() {
        return c.getService(CacheExpirationService.class);
    }

    protected final CacheEventService<Integer, String> event() {
        return c.getService(CacheEventService.class);
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

    protected final CacheServiceManagerService services() {
        return c.getService(CacheServiceManagerService.class);
    }

    protected <T> T findMXBean(Class<T> clazz) {
        return findMXBean(ManagementFactory.getPlatformMBeanServer(), clazz);
    }

    protected <T> T findMXBean(MBeanServer server, Class<T> clazz) {
        Collection<ManagedGroup> found = new ArrayList<ManagedGroup>();
        doFindMXBeans(found, CacheServices.management(c), clazz);
        if (found.size() == 0) {
            throw new IllegalArgumentException("Did not find any service " + clazz);
        } else if (found.size() == 1) {
            ObjectName on = found.iterator().next().getObjectName();
            // System.out.println(on);
            T proxy = MBeanServerInvocationHandler.newProxyInstance(server, on, clazz,
                    false);
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

    protected void loadAndAwait(Map.Entry<Integer, String> e) {
        loading().load(e.getKey());
    }

    public void loadAndAwait(Integer key) {
        loading().load(key);
    }

    protected void shutdownAndAwait() {
        c.shutdown();
        try {
            assertTrue(c.awaitTermination(5, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
    }
    /**
     * Await all loads that currently active.
     */
    protected void awaitAllLoads() {
    // only unsync now
    // when sync hook up with AbstractCacheTCKTestBundle.threadHelper
    }
}