/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.management;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.junit.Before;
import org.junit.Test;
@RequireService( { CacheManagementService.class })
public class ManagementCacheMXBean extends AbstractCacheTCKTest {

    private CacheMXBean mxBean;

    private MBeanServer mbs;

    @Before
    public void setup() throws Exception {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().setName("managementtest").management().setEnabled(true)
                .setMBeanServer(mbs).c());
        ObjectName on = new ObjectName("org.coconut.cache:name=managementtest,service="
                + CacheMXBean.MANAGED_SERVICE_NAME);
        prestart();
        mxBean = (CacheMXBean) MBeanServerInvocationHandler.newProxyInstance(mbs, on,
                CacheMXBean.class, false);
    }

    @Test
    public void getName() {
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs));
        mxBean = findMXBean(mbs, CacheMXBean.class);
        assertEquals("foo", mxBean.getName());
        assertEquals(c.getName(), mxBean.getName());
    }
    
    /**
     * Hmm tests what?
     */
    @Test
    public void getNameUninitialized() {
        assertEquals(c.getName(), mxBean.getName());
    }

    @Test
    public void getCapacity() {
        assertEquals(c.volume(), mxBean.getVolume());
        put(M1);
        put(M2);
        assertEquals(2, mxBean.getVolume());
        assertEquals(c.volume(), mxBean.getVolume());
    }

    @Test
    public void getSize() {
        assertEquals(c.size(), mxBean.getSize());
        put(M1);
        put(M2);
        assertEquals(2, mxBean.getSize());
        assertEquals(c.size(), mxBean.getSize());
    }

    @Test
    public void clear() {
        assertSize(0);
        put(M1);
        put(M2);
        assertSize(2);
        mxBean.clear();
        assertSize(0);
    }

}
