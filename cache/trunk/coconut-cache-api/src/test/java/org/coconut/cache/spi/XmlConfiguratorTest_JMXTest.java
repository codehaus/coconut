/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static org.coconut.cache.spi.XmlConfiguratorTest.rw;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServerFactory;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.management.CacheMXBean;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest_JMXTest {

    CacheConfiguration conf;
    static CacheConfiguration DEFAULT=CacheConfiguration.create();
    @Before
    public void setup() {
        conf = CacheConfiguration.create();
    }

    protected CacheConfiguration.JMX j() {
        return conf.jmx();
    }

    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertEquals(CacheMXBean.DEFAULT_JMX_DOMAIN, j().getDomain());
        assertEquals(ManagementFactory.getPlatformMBeanServer(), j().getMBeanServer());
        assertEquals(DEFAULT.jmx().getAutoRegister(), j().getAutoRegister());
    }

    @Test
    public void testJMX() throws Exception {
        j().setDomain("foo.bar");
        j().setAutoRegister(!DEFAULT.jmx().getAutoRegister());
        j().setMbeanServer(MBeanServerFactory.createMBeanServer());
        conf = rw(conf);
        assertEquals("foo.bar", j().getDomain());
        assertEquals(!DEFAULT.jmx().getAutoRegister(), j().getAutoRegister());
    }

    @Test
    public void testCornerCase() throws Exception {
        // coverage mostly
        j().setDomain("foo.foo");
        conf = rw(conf);
        assertEquals("foo.foo", j().getDomain());
        assertEquals(DEFAULT.jmx().getAutoRegister(), j().getAutoRegister());

        conf = CacheConfiguration.create();
        j().setAutoRegister(!DEFAULT.jmx().getAutoRegister());
        conf = rw(conf);
        assertEquals(!DEFAULT.jmx().getAutoRegister(), j().getAutoRegister());
    }

}
