/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExpirationConfigurationTest {

    CacheExpirationConfiguration conf;

    static CacheExpirationConfiguration DEFAULT = new CacheExpirationConfiguration();

    @Before
    public void setUp() {
        conf = new CacheExpirationConfiguration();
    }

    Filter<CacheEntry> f = Filters.TRUE;

    @Test
    public void testInitialValues() {
        assertNull(conf.getFilter());
        assertNull(conf.getRefreshFilter());
        assertTrue(conf.getRefreshInterval(TimeUnit.NANOSECONDS) < 0);
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf.getDefaultTimeout(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf.getDefaultTimeout(TimeUnit.SECONDS));
    }

    @Test
    public void testFilter() {
        assertEquals(conf, conf.setFilter(f));
        assertEquals(f, conf.getFilter());
    }

    @Test
    public void testRefreshFilter() {
        assertEquals(conf, conf.setRefreshFilter(f));
        assertEquals(f, conf.getRefreshFilter());
    }

    @Test(expected = NullPointerException.class)
    public void testRefreshInterval() {

        assertEquals(conf, conf.setRefreshInterval(0, TimeUnit.SECONDS));
        assertEquals(0, conf.getRefreshInterval(TimeUnit.NANOSECONDS));

        conf.setRefreshInterval(5, TimeUnit.MICROSECONDS);
        assertEquals(5000, conf.getRefreshInterval(TimeUnit.NANOSECONDS));

        conf.setRefreshInterval(1000, null);
    }

    /**
     * Test default expiration. The default is that entries never expire.
     */
    @Test
    public void testDefaultExpiration() {
        assertEquals(conf, conf.setDefaultTimeout(2, TimeUnit.SECONDS));

        assertEquals(2l, conf.getDefaultTimeout(TimeUnit.SECONDS));
        assertEquals(2l * 1000, conf.getDefaultTimeout(TimeUnit.MILLISECONDS));
        assertEquals(2l * 1000 * 1000, conf.getDefaultTimeout(TimeUnit.MICROSECONDS));
        assertEquals(2l * 1000 * 1000 * 1000, conf
                .getDefaultTimeout(TimeUnit.NANOSECONDS));

        conf.setDefaultTimeout(CacheExpirationService.NEVER_EXPIRE, TimeUnit.MICROSECONDS);
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf.getDefaultTimeout(TimeUnit.SECONDS));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultExpirationIAE() {
        conf.setDefaultTimeout(0, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultExpirationNPE() {
        conf.setDefaultTimeout(1, null);
    }
    
    
    CacheExpirationConfiguration e() {
        return conf;
    }
    
    static CacheExpirationConfiguration rw(CacheExpirationConfiguration conf) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CacheConfiguration cc = CacheConfiguration.create();
        cc.addService(conf);
        XmlConfigurator.getInstance().to(cc, os);
        cc = XmlConfigurator.getInstance().from(
                new ByteArrayInputStream(os.toByteArray()));
        return (CacheExpirationConfiguration) cc
                .getServiceConfiguration(CacheExpirationConfiguration.class);
    }
    
    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertNull(e().getFilter());
        assertNull(e().getRefreshFilter());
        assertTrue(e().getRefreshInterval(TimeUnit.NANOSECONDS) < 0);
        assertEquals(CacheExpirationService.NEVER_EXPIRE, e().getDefaultTimeout(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, e().getDefaultTimeout(TimeUnit.SECONDS));
    }

    @Test
    public void testExpiration() throws Exception {
        e().setDefaultTimeout(60, TimeUnit.SECONDS);
        e().setRefreshInterval(120, TimeUnit.SECONDS);
        e().setFilter(new MyFilter());
        e().setRefreshFilter(new MyFilter2());

        conf = rw(conf);
        assertTrue(e().getFilter() instanceof MyFilter);
        assertTrue(e().getRefreshFilter() instanceof MyFilter2);
        assertEquals(60 * 1000, e().getDefaultTimeout(TimeUnit.MILLISECONDS));
        assertEquals(120 * 1000, e().getRefreshInterval(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testIgnoreFilters() throws Exception {
        e().setFilter(new MyFilter3(""));
        e().setRefreshFilter(new MyFilter4());
        conf = rw(conf);
        assertNull(e().getFilter());
        assertNull(e().getRefreshFilter());
    }

    @Test
    public void testCornerCase() throws Exception {
        // coverage mostly
        e().setDefaultTimeout(30, TimeUnit.SECONDS);
        conf = rw(conf);
        assertNull(e().getFilter());
        assertNull(e().getRefreshFilter());
        assertTrue(e().getRefreshInterval(TimeUnit.NANOSECONDS) < 0);
        assertEquals(30, e().getDefaultTimeout(TimeUnit.SECONDS));
//        conf = CacheConfiguration.create();
//        e().setRefreshInterval(30, TimeUnit.SECONDS);
//        conf = rw(conf);
//        assertEquals(Cache.NEVER_EXPIRE, e().getDefaultTimeout(TimeUnit.SECONDS));
    }

    public static class MyFilter implements Filter {
        public boolean accept(Object element) {
            return false;
        }
    }

    public static class MyFilter2 implements Filter {
        public boolean accept(Object element) {
            return false;
        }
    }

    public static class MyFilter3 implements Filter {
        public MyFilter3(Object foo) {
        }

        public boolean accept(Object element) {
            return false;
        }
    }

    static class MyFilter4 implements Filter {
        private MyFilter4() {
        }
        public boolean accept(Object element) {
            return false;
        }
    }
}
