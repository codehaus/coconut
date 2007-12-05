/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.RuntimeMBeanException;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.RequireService;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.cache.test.util.managed.ManagedFilter;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.operations.Ops.Predicate;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the CacheLoadingMXBean interface
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RequireService( { CacheManagementService.class })
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
        c = newCache(newConf().loading().setLoader(loader).c().management().setEnabled(true)
                .setMBeanServer(mbs).c());
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
        assertEquals(1000 * 1000, loading().getDefaultTimeToRefresh(TimeUnit.MICROSECONDS));

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true).setMBeanServer(mbs).c()
                .loading().setLoader(loader).setDefaultTimeToRefresh(1800, TimeUnit.SECONDS));
        mxBean = findMXBean(mbs, CacheLoadingMXBean.class);
        assertEquals(1800 * 1000, mxBean.getDefaultTimeToRefreshMs());

        // Exception
        try {
            mxBean.setDefaultTimeToRefreshMs(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {} catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /**
     * Tests force load all.
     */
    @Test
    public void testForceLoadAll() {
        mxBean.forceLoadAll();
        awaitAllLoads();
        assertEquals("A", c.get(1));
        assertEquals("B", c.get(2));
        loader.setBase(1);
        mxBean.forceLoadAll();
        awaitAllLoads();
        assertEquals("B", c.peek(1));
        assertEquals("C", c.peek(2));
    }

    /**
     * Tests load all.
     */
    @Test
    public void testLoadAll() {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(newConf().loading().setRefreshPredicate(new RefreshFilter()).setLoader(loader)
                .c().management().setEnabled(true).setMBeanServer(mbs));
        mxBean = findMXBean(mbs, CacheLoadingMXBean.class);
        mxBean.loadAll();
        awaitAllLoads();
        assertEquals("A", c.get(1));
        assertEquals("B", c.get(2));
        loader.setBase(2);
        mxBean.loadAll();
        awaitAllLoads();
        assertEquals("C", c.peek(1));
        assertEquals("B", c.peek(2));
    }

    static class RefreshFilter implements Predicate<CacheEntry<Integer, String>> {
        public boolean evaluate(CacheEntry<Integer, String> element) {
            return element.getKey().equals(1);
        }
    }

    @Test
    public void loadingManagement() {
        CacheConfiguration<Integer, String> cc = newConf();
        cc.management().setEnabled(true);
        MyLoader loader = new MyLoader();
        c = newCache(cc.loading().setLoader(loader).c());
        loading().load(1);
        assertNotNull(loader.g);
    }

    @Test
    public void filterManagement() {
        CacheConfiguration<Integer, String> cc = newConf();
        cc.management().setEnabled(true);
        ManagedFilter filter = new ManagedFilter();
        c = newCache(cc.loading().setRefreshPredicate(filter)
                .setLoader(new IntegerToStringLoader()).c());
        prestart();
        assertNotNull(
                "The Filter extends ManagedObject, and its manage() method should have been invoked",
                filter.getManagedGroup());
    }

    static class MyLoader extends IntegerToStringLoader implements ManagedLifecycle {
        ManagedGroup g;

        public void manage(ManagedGroup parent) {
            g = parent;
        }
    }

}
