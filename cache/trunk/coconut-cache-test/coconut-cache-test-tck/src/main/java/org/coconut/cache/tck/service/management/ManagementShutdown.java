package org.coconut.cache.tck.service.management;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.management.ManagedGroup;
import org.coconut.management.annotation.ManagedOperation;
import org.junit.Before;
import org.junit.Test;

public class ManagementShutdown extends AbstractCacheTCKTest {

    private MBeanServer mbs;

    @Before
    public void setup() throws Exception {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().setName("managementtest").management().setEnabled(true)
                .setMBeanServer(mbs).c());
        ObjectName on = new ObjectName("org.coconut.cache:name=managementtest,service="
                + CacheMXBean.MANAGED_SERVICE_NAME);
        prestart();
    }

    @Test
    public void test() {
        assertNotNull(management().getDescription());
        assertNotNull(management().getName());
    }

    @Test(expected = IllegalStateException.class)
    public void addShutdownISE() {
        c.shutdown();
        management().add(new Op());
    }

    @Test(expected = IllegalStateException.class)
    public void addChildShutdownISE() {
        c.shutdown();
        // generate unique name
        management().addChild("name" + System.nanoTime(), "description");
    }

    @Test
    public void getChildrenShutdown() throws InterruptedException {
        Collection<ManagedGroup> col = management().getChildren();
        c.shutdown();
        assertEquals(col, management().getChildren());
        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(col, management().getChildren());
    }

    @Test
    public void getDescriptionShutdown() throws InterruptedException {
        String desc = management().getDescription();
        c.shutdown();
        assertEquals(desc, management().getDescription());
        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(desc, management().getDescription());
    }

    @Test
    public void getNameShutdown() throws InterruptedException {
        String name = management().getName();
        c.shutdown();
        assertEquals(name, management().getName());
        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(name, management().getName());
    }

    @Test
    public void getObjectNameShutdown() throws InterruptedException {
        c.shutdown();
        assertNull(management().getObjectName());
        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        assertNull(management().getObjectName());
    }

    @Test
    public void getParentShutdown() throws InterruptedException {
        c.shutdown();
        assertNull(management().getParent());
        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        assertNull(management().getParent());
    }

    @Test
    public void getServerShutdown() throws InterruptedException {
        c.shutdown();
        assertNull(management().getServer());
        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        assertNull(management().getServer());
    }

    @Test
    public void getIsRegisteredShutdown() throws InterruptedException {
        c.shutdown();
        assertFalse(management().isRegistered());
        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));
        assertFalse(management().isRegistered());
    }

    @Test(expected = IllegalStateException.class)
    public void getRegisterShutdownISE() throws Exception {
        c.shutdown();
        management().register(mbs, new ObjectName("foo:name=foo"));
    }

    @Test(expected = IllegalStateException.class)
    public void getRemoveShutdownISE() throws Exception {
        c.shutdown();
        management().remove();
    }

    @Test(expected = IllegalStateException.class)
    public void getUnregisterShutdownISE() throws Exception {
        c.shutdown();
        management().unregister();
    }

    public static class Op {
        @ManagedOperation
        public void foo() {

        }
    }
}
