/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.statistics;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import junit.framework.Assert;

import org.coconut.cache.Cache;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsMXBean;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class StatisticsMXBean extends AbstractCacheTCKTest {

    private CacheStatisticsMXBean mxBean;

    private MBeanServer mbs;
    
    @Override
    protected Cache<Integer, String> newCache(int size) {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().setClock(clock).setName("managementtest").management()
                .setEnabled(true).setMBeanServer(mbs).c(), size);
        ObjectName on;
        try {
            on = new ObjectName("org.coconut.cache:name=managementtest,service="
                    + CacheStatisticsConfiguration.SERVICE_NAME);
        } catch (MalformedObjectNameException e) {
            throw new AssertionError(e);
        }
        prestart();
        mxBean = MBeanServerInvocationHandler.newProxyInstance(mbs, on,
                CacheStatisticsMXBean.class, false);
        return c;
    }

    /**
     * Tests that getAll affects the hit statistics of a cache.
     */
    @Test
    public void getAllHitStat() {
        c = newCache(0);
        getAll(M1, M2, M3, M4);
        assertHitstat(0, 0, 4);
        c = newCache(2);
        getAll(M1, M2, M3, M4);
        assertHitstat(0.50f, 2, 2);
        c = newCache(4);
        getAll(M1, M2, M3, M4);
        assertHitstat(1, 4, 0);
    }

    /**
     * Tests of a simple hit on the cache and a simple miss on the cache.
     */
    @Test
    public void getHitStat() {
        c = newCache(1);
        get(M2);
        assertHitstat(0, 0, 1);

        get(M1);
        assertHitstat(0.5f, 1, 1);
    }

    /**
     * Tests that the initial hit stat is 0 misses, 0 hits and -1 for the ratio.
     */
    @Test
    public void initialHitStat() {
        c = newCache(0);
        assertHitstat(Float.NaN, 0, 0);
        c = newCache(5);
        assertHitstat(Float.NaN, 0, 0);
    }

    /**
     * Tests that reset hitstat works.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void resetHitstat() {
        c = newCache(1);
        assertGet(M1); // hit
        assertNullGet(M2); // miss
        Assert.assertEquals(0.5f, mxBean.getHitRatio(), 0.0001);
        assertHitstat(0.5f, 1, 1);
        mxBean.resetStatistics();
        assertHitstat(Float.NaN, 0, 0);
    }

    @Override
    protected void assertHitstat(float ratio, long hits, long misses) {
        Assert.assertEquals(ratio, mxBean.getHitRatio(), 0.0001);
        Assert.assertEquals(hits, mxBean.getNumberOfHits());
        Assert.assertEquals(misses, mxBean.getNumberOfMisses());
    }
}
