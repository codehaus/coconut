/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheManagementConfigurationTest {

    CacheManagementConfiguration m;

    static CacheManagementConfiguration DEFAULT = new CacheManagementConfiguration();

    @Before
    public void setUp() {
        m = new CacheManagementConfiguration();
    }

    @Test
    public void testDomain() {
        assertEquals(CacheMXBean.DEFAULT_JMX_DOMAIN, m.getDomain());
        assertEquals(m, m.setDomain("mydomain"));
        assertEquals("mydomain", m.getDomain());
    }

    @Test(expected = NullPointerException.class)
    public void testDomainNPE() {
        m.setDomain(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDomainIAE() {
        m.setDomain(":");
    }

    @Test
    public void testMBeanServer() {
        assertNull(m.getMBeanServer());
        MBeanServer s = MBeanServerFactory.createMBeanServer();
        assertEquals(m, m.setMBeanServer(s));
        assertEquals(s, m.getMBeanServer());
    }

    @Test
    public void testEnabled() {
        assertFalse(m.isEnabled());
        assertEquals(m, m.setEnabled(true));
        assertTrue(m.isEnabled());
    }

    @Test
    public void testXmlNoop() throws Exception {
        m = reloadService(m);
        assertEquals(DEFAULT.getDomain(), m.getDomain());
        assertEquals(DEFAULT.getMBeanServer(), m.getMBeanServer());
        assertFalse(m.isEnabled());
    }

    @Test
    public void testXmlJMX() throws Exception {
        m.setDomain("foo.bar");
        m.setMBeanServer(MBeanServerFactory.createMBeanServer());
        m.setEnabled(true);
        m = reloadService(m);
        assertEquals("foo.bar", m.getDomain());
        assertEquals(DEFAULT.getMBeanServer(), m.getMBeanServer());
        assertTrue(m.isEnabled());
    }

}
