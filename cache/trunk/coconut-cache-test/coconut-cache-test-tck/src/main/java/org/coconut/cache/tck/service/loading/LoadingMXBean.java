/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.RuntimeMBeanException;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.filter.Predicate;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the CacheLoadingMXBean interface
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadingMXBean extends AbstractLoadingTestBundle {

    private CacheLoadingMXBean mxBean;

    private MBeanServer mbs;

    // TODO
    // We should test for a default objectname
    // a.la. for cache named foo
    // ObjName=org.coconut.cache:name=343434, service=General

    @Before
    public void setup() {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().loading().setLoader(loader).c().management().setEnabled(
                true).setMBeanServer(mbs).c());
        mxBean = findMXBean(mbs, CacheLoadingMXBean.class);
    }

    /**
     * Tests default idle time.
     */
    @Test
    public void defaultIdleTime() {
        assertEquals(Long.MAX_VALUE, mxBean.getDefaultTimeToRefreshMs());
        mxBean.setDefaultTimeToRefreshMs(1000);
        assertEquals(1000, mxBean.getDefaultTimeToRefreshMs());
        assertEquals(1000 * 1000, loading()
                .getDefaultTimeToRefresh(TimeUnit.MICROSECONDS));

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs).c().loading().setLoader(loader)
                .setDefaultTimeToRefresh(1800, TimeUnit.SECONDS));
        mxBean = findMXBean(mbs, CacheLoadingMXBean.class);
        assertEquals(1800 * 1000, mxBean.getDefaultTimeToRefreshMs());

        // Exception
        try {
            mxBean.setDefaultTimeToRefreshMs(-1);
            fail("Did not throw exception");
        } catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /**
     * Tests force load all.
     */
    @Test
    public void testForceLoadAll() {
        mxBean.forceLoadAll();
        assertEquals("A", c.get(1));
        assertEquals("B", c.get(2));
        loader.setBase(1);
        mxBean.forceLoadAll();
        assertEquals("B", c.peek(1));
        assertEquals("C", c.peek(2));
    }

    /**
     * Tests load all.
     */
    @Test
    public void testLoadAll() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().loading().setRefreshFilter(new RefreshFilter()).setLoader(
                loader).c().management().setEnabled(true).setMBeanServer(mbs));
        mxBean = findMXBean(mbs, CacheLoadingMXBean.class);
        mxBean.loadAll();
        assertEquals("A", c.get(1));
        assertEquals("B", c.get(2));
        loader.setBase(2);
        mxBean.loadAll();
        assertEquals("C", c.peek(1));
        assertEquals("B", c.peek(2));
    }

    static class RefreshFilter implements Predicate<CacheEntry<Integer, String>> {
        public boolean evaluate(CacheEntry<Integer, String> element) {
            return element.getKey().equals(1);
        }
    }
}
