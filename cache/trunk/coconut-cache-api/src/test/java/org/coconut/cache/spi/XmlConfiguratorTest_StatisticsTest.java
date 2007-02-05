/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static org.coconut.cache.spi.XmlConfiguratorTest.rw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServerFactory;
import javax.swing.DefaultBoundedRangeModel;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.management.CacheMXBean;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest_StatisticsTest {

    XmlConfigurator c;

    CacheConfiguration conf;

    static CacheConfiguration DEFAULT = CacheConfiguration.create();

    @Before
    public void setup() {
        conf = CacheConfiguration.create();
        c = XmlConfigurator.getInstance();
    }

    protected CacheConfiguration.Statistics s() {
        return conf.statistics();
    }

    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertEquals(DEFAULT.statistics().isEnabled(), s().isEnabled());
    }

    @Test
    public void testEnabled() throws Exception {
        conf.statistics().setEnabled(!DEFAULT.statistics().isEnabled());
        conf = rw(conf);
        assertEquals(!DEFAULT.statistics().isEnabled(), s().isEnabled());
    }

    @Test
    public void testEnabled2() throws Exception {
        //check that use can set the enabled attribute to true
        conf.statistics().setEnabled(!DEFAULT.statistics().isEnabled());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        c.to(conf, os);
        String ss = os.toString().replace("enabled=\"false\"", "");
        conf = c.from(new ByteArrayInputStream(ss.getBytes()));
        assertEquals(DEFAULT.statistics().isEnabled(), s().isEnabled());
    }
}
