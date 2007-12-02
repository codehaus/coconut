/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.RequireService;
import org.coconut.cache.test.util.managed.ManagedFilter;
import org.junit.Before;
import org.junit.Test;

@RequireService( { CacheManagementService.class })
public class ExpirationMXBean extends AbstractExpirationTestBundle {

    private CacheExpirationMXBean mxBean;

    private MBeanServer mbs;

    @Before
    public void setup() throws Exception {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().setClock(clock).setName("managementtest").management()
                .setEnabled(true).setMBeanServer(mbs).c());
        ObjectName on = new ObjectName("org.coconut.cache:name=managementtest,service="
                + CacheExpirationConfiguration.SERVICE_NAME);
        prestart();
        mxBean = MBeanServerInvocationHandler.newProxyInstance(mbs, on,
                CacheExpirationMXBean.class, false);
    }

    /**
     * Tests default idle time.
     */
    @Test
    public void timeToLive() {
        assertEquals(Long.MAX_VALUE, mxBean.getDefaultTimeToLiveMs());
        mxBean.setDefaultTimeToLiveMs(1000);
        assertEquals(1000, mxBean.getDefaultTimeToLiveMs());
        assertEquals(1000 * 1000, expiration()
                .getDefaultTimeToLive(TimeUnit.MICROSECONDS));
    }

    /**
     * Test purgeExpired.
     */
    @Test
    public void purge() {
        put(M1, 2);
        incTime();
        mxBean.purgeExpired();
        assertGet(M1);

        incTime();
        mxBean.purgeExpired();
        assertNullPeek("Element M1 was not expired and removed", M1);
        assertSize(0);
    }

    /**
     * Tests default idle time.
     */
    @Test
    public void timeToLive0() {
        mxBean.setDefaultTimeToLiveMs(0);
        assertEquals(Long.MAX_VALUE, mxBean.getDefaultTimeToLiveMs());
    }

    @Test
    public void withStartValue() {
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs).c().expiration().setDefaultTimeToLive(1800,
                        TimeUnit.SECONDS));
        mxBean = findMXBean(mbs, CacheExpirationMXBean.class);
        assertEquals(1800 * 1000, mxBean.getDefaultTimeToLiveMs());
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalTimeToLive() throws Throwable {
        try {
            mxBean.setDefaultTimeToLiveMs(-1);
        } catch (RuntimeMBeanException e) {
            throw e.getCause();
        }
    }
    
    /**
     * Tests that a expiration filter implementing ManagedObject is managed.
     */
    @Test
    public void managedObject() {
        CacheConfiguration<Integer, String> cc = newConf();
        cc.management().setEnabled(true);
        ManagedFilter filter = new ManagedFilter();
        c = newCache(cc.expiration().setExpirationFilter(filter).c());
        prestart();
        assertNotNull(filter.getManagedGroup());
    }
}
