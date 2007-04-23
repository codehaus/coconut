/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import static junit.framework.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.XmlConfigurator;
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
        assertEquals(ManagementFactory.getPlatformMBeanServer(), m.getMBeanServer());
        MBeanServer s = MBeanServerFactory.createMBeanServer();
        assertEquals(m, m.setMbeanServer(s));
        assertEquals(s, m.getMBeanServer());
    }

    @Test(expected = NullPointerException.class)
    public void testMBeanServerNPE() {
        m.setMbeanServer(null);
    }

    @Test
    public void testXmlNoop() throws Exception {
        m = rw(m);
        assertEquals(DEFAULT.getDomain(), m.getDomain());
        assertEquals(DEFAULT.getMBeanServer(), m.getMBeanServer());
    }

    @Test
    public void testXmlJMX() throws Exception {
        m.setDomain("foo.bar");
        m.setMbeanServer(MBeanServerFactory.createMBeanServer());
        m = rw(m);
        assertEquals("foo.bar", m.getDomain());
        assertEquals(DEFAULT.getMBeanServer(), m.getMBeanServer());
    }

    static CacheManagementConfiguration rw(CacheManagementConfiguration conf)
            throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CacheConfiguration cc = CacheConfiguration.create();
        cc.addConfiguration(conf);
        XmlConfigurator.getInstance().to(cc, os);
        cc = XmlConfigurator.getInstance().from(
                new ByteArrayInputStream(os.toByteArray()));
        return (CacheManagementConfiguration) cc
                .getConfiguration(CacheManagementConfiguration.class);
    }
}
