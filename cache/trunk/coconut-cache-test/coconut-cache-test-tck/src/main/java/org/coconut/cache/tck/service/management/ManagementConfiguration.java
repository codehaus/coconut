/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.management;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.coconut.cache.CacheException;
import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.management.ManagedVisitor;
import org.junit.Test;

@RequireService( { CacheManagementService.class })
public class ManagementConfiguration extends AbstractCacheTCKTest {

    @Test
    public void domain() throws Exception {
        MBeanServer mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().setName("managementtest").management().setEnabled(true)
                .setMBeanServer(mbs).setDomain("com.acme"));
        ObjectName on = new ObjectName("com.acme:name=managementtest,service="
                + CacheMXBean.MANAGED_SERVICE_NAME);
        prestart();
        CacheMXBean mxBean = (CacheMXBean) MBeanServerInvocationHandler.newProxyInstance(mbs, on,
                CacheMXBean.class, false);
        assertEquals(0, mxBean.getSize());
    }

    boolean wasCalled;

    @Test
    public void registrant() throws Exception {
        MBeanServer mbs = MBeanServerFactory.createMBeanServer();
        int count = mbs.getMBeanCount();
        conf.serviceManager().add(new ManagedLifecycle() {
            public void manage(ManagedGroup parent) {
                parent.addChild("foo1", "foodesc").addChild("foo2", "foodesc2");
            }
        });
        c = newCache(conf.setName("managementtest").management().setEnabled(true).setMBeanServer(
                mbs).setRegistrant(new ManagedVisitor() {
            public Object traverse(Object node) throws JMException {
                assertTrue(node instanceof ManagedGroup);
                ManagedGroup mg = (ManagedGroup) node;
                for (ManagedGroup m : mg.getChildren()) {
                    if (m.getName().equals("foo1")) {
                        assertEquals("foodesc", m.getDescription());
                        assertEquals(1, m.getChildren().size());
                        assertEquals("foo2", m.getChildren().iterator().next().getName());
                        assertEquals("foodesc2", m.getChildren().iterator().next().getDescription());
                        wasCalled = true;
                    }
                }
                return Void.TYPE;
            }

            public void visitManagedGroup(ManagedGroup mg) throws JMException {
                throw new AssertionError("Should not have been called");
            }

            public void visitManagedObject(Object o) throws JMException {
                throw new AssertionError("Should not have been called");
            }
        }));
        prestart();
        assertTrue(wasCalled);
        assertEquals(count, mbs.getMBeanCount().intValue());// nothing registred
    }

    @Test(expected = CacheException.class)
    public void registrantFailed() throws Exception {
        c = newCache(conf.setName("managementtest").management().setEnabled(true).setRegistrant(
                new ManagedVisitor() {
                    public Object traverse(Object node) throws JMException {
                        throw new JMException();
                    }

                    public void visitManagedGroup(ManagedGroup mg) throws JMException {
                        throw new AssertionError("Should not have been called");
                    }

                    public void visitManagedObject(Object o) throws JMException {
                        throw new AssertionError("Should not have been called");
                    }
                }));
        prestart();
    }

}
