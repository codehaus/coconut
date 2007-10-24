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
import org.coconut.management.ManagedVisitor;
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
        ManagedVisitor mgv = new LoadableManagedGroupVisitor();
        assertNull(m.getRegistrant());
        assertSame(m, m.setRegistrant(mgv));
        assertSame(mgv, m.getRegistrant());
    }

    @Test
    public void testRegistrantXML() throws Exception {
        ManagedVisitor lmg = new LoadableManagedGroupVisitor();
        ManagedVisitor nlmg = new NonLoadableManagedGroupVisitor();
        m = reloadService(m);// root should still be null
        assertNull(m.getRegistrant());

        assertSame(m, m.setRegistrant(lmg));
        m = reloadService(m);// root should be an instance of LoadableManagedGroup
        assertTrue(m.getRegistrant() instanceof LoadableManagedGroupVisitor);

        assertSame(m, m.setRegistrant(nlmg));
        m = reloadService(m);// root should be null, can't save the mocked class
        assertNull(m.getRegistrant());
    }


    public static class LoadableManagedGroupVisitor implements ManagedVisitor {

        public void visitManagedGroup(ManagedGroup mg) throws JMException {}

        public void visitManagedObject(ManagedGroup group, Object o) throws JMException {}

    }


    public class NonLoadableManagedGroupVisitor implements ManagedVisitor {

        public void visitManagedGroup(ManagedGroup mg) throws JMException {}

        public void visitManagedObject(ManagedGroup group, Object o) throws JMException {}

    }
}
