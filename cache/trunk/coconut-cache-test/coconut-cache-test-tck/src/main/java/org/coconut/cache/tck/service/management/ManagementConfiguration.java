package org.coconut.cache.tck.service.management;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class ManagementConfiguration extends AbstractCacheTCKTest {

    @Test
    public void testDomain() throws Exception {
        MBeanServer mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().setName("managementtest").management().setEnabled(true)
                .setMBeanServer(mbs).setDomain("com.acme"));
        ObjectName on = new ObjectName("com.acme:name=managementtest,service="
                + CacheMXBean.MANAGED_SERVICE_NAME);
        prestart();
        CacheMXBean mxBean = MBeanServerInvocationHandler.newProxyInstance(mbs, on,
                CacheMXBean.class, false);
        assertEquals(0, mxBean.getSize());
    }

}
