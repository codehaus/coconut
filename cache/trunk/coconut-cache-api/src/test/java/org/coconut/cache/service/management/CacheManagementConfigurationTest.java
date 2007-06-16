/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import java.lang.management.ManagementFactory;
import java.util.Collection;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedGroupVisitor;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheManagementConfigurationTest {

    static CacheManagementConfiguration DEFAULT = new CacheManagementConfiguration();

    CacheManagementConfiguration m;

    @Before
    public void setUp() {
        m = new CacheManagementConfiguration();
    }

    @Test
    public void testDomain() {
        assertNull(m.getDomain());
        assertEquals(m, m.setDomain("mydomain"));
        assertEquals("mydomain", m.getDomain());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDomainInvalid1() {
        m.setDomain("foo\n");
    }

    @Test
    public void testDomainXML() throws Exception {
        m = reloadService(m);// domain should still be null
        assertNull(m.getDomain());
        assertEquals(m, m.setDomain("mydomain2"));
        m = reloadService(m);
        assertEquals("mydomain2", m.getDomain());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDomainInvalid2() {
        m.setDomain(":");
    }

    @Test
    public void testEnabled() {
        assertFalse(m.isEnabled());
        assertEquals(m, m.setEnabled(true));
        assertTrue(m.isEnabled());
    }

    @Test
    public void testEnabledXML() throws Exception {
        m = reloadService(m);
        assertFalse(m.isEnabled());
        assertEquals(m, m.setEnabled(true));
        m = reloadService(m);
        assertTrue(m.isEnabled());
        assertEquals(m, m.setEnabled(false));
        m = reloadService(m);
        assertFalse(m.isEnabled());
    }

    @Test
    public void testMBeanServer() {
        assertNull(m.getMBeanServer());
        MBeanServer s = MBeanServerFactory.createMBeanServer();
        assertEquals(m, m.setMBeanServer(s));
        assertEquals(s, m.getMBeanServer());
    }

    @Test
    public void testMBeanServerXML() throws Exception {
        m = reloadService(m);
        assertNull(m.getMBeanServer());
        m.setMBeanServer(MBeanServerFactory.createMBeanServer());
        m = reloadService(m);
        assertNull(m.getMBeanServer());
        m.setMBeanServer(ManagementFactory.getPlatformMBeanServer());
        m = reloadService(m);
        assertSame(ManagementFactory.getPlatformMBeanServer(), m.getMBeanServer());
    }

    @Test
    public void testRegistrant() {
        ManagedGroupVisitor mgv = new LoadableManagedGroupVisitor();
        assertNull(m.getRegistrant());
        assertSame(m, m.setRegistrant(mgv));
        assertSame(mgv, m.getRegistrant());
    }

    @Test
    public void testRegistrantXML() throws Exception {
        ManagedGroupVisitor lmg = new LoadableManagedGroupVisitor();
        ManagedGroupVisitor nlmg = new NonLoadableManagedGroupVisitor();
        m = reloadService(m);// root should still be null
        assertNull(m.getRegistrant());

        assertSame(m, m.setRegistrant(lmg));
        m = reloadService(m);// root should be an instance of LoadableManagedGroup
        assertTrue(m.getRegistrant() instanceof LoadableManagedGroupVisitor);

        assertSame(m, m.setRegistrant(nlmg));
        m = reloadService(m);// root should be null, can't save the mocked class
        assertNull(m.getRegistrant());
    }

    @Test
    public void testRoot() throws Exception {
        ManagedGroup mg = new LoadableManagedGroup();
        assertNull(m.getRoot());
        assertSame(m, m.setRoot(mg));
        assertSame(mg, m.getRoot());
    }

    @Test
    public void testRootXML() throws Exception {
        ManagedGroup lmg = new LoadableManagedGroup();
        ManagedGroup nlmg = new NonLoadableManagedGroup();
        m = reloadService(m);// root should still be null
        assertNull(m.getRoot());

        assertSame(m, m.setRoot(lmg));
        m = reloadService(m);// root should be an instance of LoadableManagedGroup
        assertTrue(m.getRoot() instanceof LoadableManagedGroup);

        assertSame(m, m.setRoot(nlmg));
        m = reloadService(m);// root should be null, can't save the mocked class
        assertNull(m.getRoot());
    }

    public static class LoadableManagedGroup implements ManagedGroup {

        public ManagedGroup add(Object o) {
            return null;
        }

        public ManagedGroup addChild(String name, String description) {
            return null;
        }

        public Collection<ManagedGroup> getChildren() {
            return null;
        }

        public String getDescription() {
            return null;
        }

        public String getName() {
            return null;
        }

        public ObjectName getObjectName() {
            return null;
        }

        public Collection<?> getObjects() {
            return null;
        }

        public ManagedGroup getParent() {
            return null;
        }

        public MBeanServer getServer() {
            return null;
        }

        public boolean isRegistered() {
            return false;
        }

        public void register(MBeanServer service, ObjectName objectName)
                throws JMException {}

        public void remove() {}

        public void unregister() throws JMException {}

    }

    public static class LoadableManagedGroupVisitor implements ManagedGroupVisitor {

        public void visitManagedGroup(ManagedGroup mg) throws JMException {}

        public void visitManagedObject(ManagedGroup group, Object o) throws JMException {}

    }

    public class NonLoadableManagedGroup implements ManagedGroup {

        public ManagedGroup add(Object o) {
            return null;
        }

        public ManagedGroup addChild(String name, String description) {
            return null;
        }

        public Collection<ManagedGroup> getChildren() {
            return null;
        }

        public String getDescription() {
            return null;
        }

        public String getName() {
            return null;
        }

        public ObjectName getObjectName() {
            return null;
        }

        public Collection<?> getObjects() {
            return null;
        }

        public ManagedGroup getParent() {
            return null;
        }

        public MBeanServer getServer() {
            return null;
        }

        public boolean isRegistered() {
            return false;
        }

        public void register(MBeanServer service, ObjectName objectName)
                throws JMException {}

        public void remove() {}

        public void unregister() throws JMException {}

    }

    public class NonLoadableManagedGroupVisitor implements ManagedGroupVisitor {

        public void visitManagedGroup(ManagedGroup mg) throws JMException {}

        public void visitManagedObject(ManagedGroup group, Object o) throws JMException {}

    }
}
