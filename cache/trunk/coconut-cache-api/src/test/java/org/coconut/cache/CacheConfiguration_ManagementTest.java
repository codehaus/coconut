/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Collection;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.coconut.cache.management.CacheMXBean;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration_ManagementTest {
    CacheConfiguration<Number, Collection> conf;

    CacheConfiguration.Management j;

    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
        j = conf.management();
    }

    @Test
    public void testExpiration() {
        assertEquals(conf, conf.management().c());
    }

    @Test(expected = NullPointerException.class)
    public void testDomain() {
        assertEquals(CacheMXBean.DEFAULT_JMX_DOMAIN, j.getDomain());
        assertEquals(j, j.setDomain("mydomain"));
        assertEquals("mydomain", j.getDomain());
        j.setDomain(null);
    }

    @Test(expected = NullPointerException.class)
    public void testMBeanServer() {
        assertEquals(ManagementFactory.getPlatformMBeanServer(), j.getMBeanServer());
        MBeanServer s=MBeanServerFactory.createMBeanServer();
        assertEquals(j, j.setMbeanServer(s));
        assertEquals(s, j.getMBeanServer());
        j.setMbeanServer(null);
    }
    
    @Test
    public void testRegister() {
        assertFalse(j.getAutoRegister());
        assertEquals(j, j.setAutoRegister(true));
        assertTrue(j.getAutoRegister());
    }
}
