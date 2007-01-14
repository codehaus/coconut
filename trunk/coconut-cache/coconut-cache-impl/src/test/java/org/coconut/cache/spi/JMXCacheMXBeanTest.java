/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.defaults.support.JMXCacheService;
import org.coconut.cache.management.CacheMXBean;
import org.coconut.core.EventProcessor;
import org.coconut.event.EventBus;
import org.coconut.test.MockTestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class JMXCacheMXBeanTest extends MockTestCase {

    Cache<Integer, Integer> c;

    CacheMXBean bean;

    EventBus<CacheEvent<Integer, Integer>> eb;

    BlockingQueue<CacheEvent<Integer, Integer>> events;

    EventProcessor<CacheEvent<Integer, Integer>> eventHandler;

    protected void setUp() throws Exception {
        super.setUp();
        c = null; //Caches.newFastMemoryCache(Policies.newClock(), 100);
        JMXCacheService.registerCache(c, "test");
        bean = JMXCacheService.createProxy(ManagementFactory
                .getPlatformMBeanServer(), "test");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        JMXCacheService.unregisterCache("test");
    }

    public void testNoTests() {
        
    }
//    public void testSize() {
//        assertEquals(0, bean.getSize());
//        c.put(1, 1);
//        assertEquals(1, bean.getSize());
//        c.put(2, 1);
//        assertEquals(2, bean.getSize());
//        c.clear();
//        assertEquals(0, bean.getSize());
//    }
//
//    public void testClear() {
//        c.put(1, 1);
//        c.put(2, 1);
//        bean.clear();
//        assertEquals(0, bean.getSize());
//        assertEquals(0, c.size());
//    }
//
//    public void t() {
//        bean.getHitRatio();
//        bean.getNumberOfHits();
//        bean.getNumberOfMisses();
//        bean.resetHitStat();
//    }
//
//    public void testGetHitStat() {
//        assertEquals(-1, bean.getHitRatio(), 0.0001);
//        assertEquals(0, bean.getNumberOfHits());
//        assertEquals(0, bean.getNumberOfMisses());
//
//        c.get(0);
//        assertEquals(0, bean.getHitRatio(), 0.0001);
//        assertEquals(0, bean.getNumberOfHits());
//        assertEquals(1, bean.getNumberOfMisses());
//
//        c.put(0, 1);
//        c.get(0);
//        assertEquals(0.5, bean.getHitRatio(), 0.0001);
//        assertEquals(1, bean.getNumberOfHits());
//        assertEquals(1, bean.getNumberOfMisses());
//    }
//
//    public void testResetHitStat() {
//        c.put(0, 0);
//        c.get(0);
//        c.get(1);
//        bean.resetHitStat();
//        assertEquals(-1, c.getHitStat().getHitRatio(), 0.0001);
//        assertEquals(0, c.getHitStat().getNumberOfHits());
//        assertEquals(0, c.getHitStat().getNumberOfMisses());
//    }

}
