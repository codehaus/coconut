/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.RuntimeMBeanException;

import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Before;
import org.junit.Test;

public class LoadingMXBean extends AbstractLoadingTestBundle {

    static CacheLoadingConfiguration<?, ?> DEFAULT = new CacheLoadingConfiguration();

    CacheLoadingMXBean mxBean;

    MBeanServer mbs;

    // TODO
    // We should test for a default objectname
    // a.la. for cache named foo
    // ObjName=org.coconut.cache:name=343434, service=General

    @Before
    public void setup() {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().loading().setLoader(new IntegerToStringLoader()).c()
                .management().setEnabled(true).setMBeanServer(mbs).c());
        mxBean = findMXBean(mbs, CacheLoadingMXBean.class);
    }

    /**
     * Tests default idle time.
     */
    @Test
    public void defaultIdleTime() {
        assertEquals(Long.MAX_VALUE, mxBean
                .getDefaultTimeToRefreshMs());
        mxBean.setDefaultTimeToRefreshMs(1000);
        assertEquals(1000, mxBean.getDefaultTimeToRefreshMs());
        assertEquals(1000 * 1000, loading()
                .getDefaultTimeToRefresh(TimeUnit.MICROSECONDS));

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs).c().loading().setLoader(new IntegerToStringLoader())
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
}
