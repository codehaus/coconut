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

    static CacheExpirationConfiguration DEFAULT = new CacheExpirationConfiguration();

    CacheExpirationConfiguration conf;

    Filter<CacheEntry> f = Filters.TRUE;

    @Before
    public void setUp() {
        conf = new CacheExpirationConfiguration();
    }

//    @Test
//    public void testCornerCase() throws Exception {
//        // coverage mostly
//        e().setDefaultTimeout(30, TimeUnit.SECONDS);
//        conf = rw(conf);
//        assertNull(e().getFilter());
//        assertNull(e().getReloadFilter());
//        assertTrue(e().getReloadInterval(TimeUnit.NANOSECONDS) < 0);
//        assertEquals(30, e().getDefaultTimeout(TimeUnit.SECONDS));
////        conf = CacheConfiguration.create();
////        e().setRefreshInterval(30, TimeUnit.SECONDS);
////        conf = rw(conf);
////        assertEquals(Cache.NEVER_EXPIRE, e().getDefaultTimeout(TimeUnit.SECONDS));
//    }

    /**
     * Test default expiration. The default is that entries never expire.
     */
    @Test
    public void testDefaultExpiration() {
        assertEquals(conf, conf.setDefaultTimeToLive(2, TimeUnit.SECONDS));

        assertEquals(2l, conf.getDefaultTimeToLive(TimeUnit.SECONDS));
        assertEquals(2l * 1000, conf.getDefaultTimeToLive(TimeUnit.MILLISECONDS));
        assertEquals(2l * 1000 * 1000, conf.getDefaultTimeToLive(TimeUnit.MICROSECONDS));
        assertEquals(2l * 1000 * 1000 * 1000, conf
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));

        conf.setDefaultTimeToLive(CacheExpirationService.NEVER_EXPIRE, TimeUnit.MICROSECONDS);
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf.getDefaultTimeToLive(TimeUnit.SECONDS));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultExpirationIAE() {
        conf.setDefaultTimeToLive(0, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultExpirationNPE() {
        conf.setDefaultTimeToLive(1, null);
    }

    @Test
    public void testExpiration() throws Exception {
        e().setDefaultTimeToLive(60, TimeUnit.SECONDS);
        e().setExpirationFilter(new MyFilter());
        
        conf = rw(conf);
        assertTrue(e().getExpirationFilter() instanceof MyFilter);
        
        assertEquals(60 * 1000, e().getDefaultTimeToLive(TimeUnit.MILLISECONDS));
        
    }

    @Test
    public void testFilter() {
        assertEquals(conf, conf.setExpirationFilter(f));
        assertEquals(f, conf.getExpirationFilter());
    }

    @Test
    public void testIgnoreFilters() throws Exception {
        e().setExpirationFilter(new MyFilter3(""));
        conf = rw(conf);
        assertNull(e().getExpirationFilter());
    }
    
    
    @Test
    public void testInitialValues() {
        assertNull(conf.getExpirationFilter());
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf.getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf.getDefaultTimeToLive(TimeUnit.SECONDS));
    }
    
    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertNull(e().getExpirationFilter());
        assertEquals(CacheExpirationService.NEVER_EXPIRE, e().getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, e().getDefaultTimeToLive(TimeUnit.SECONDS));
    }

    CacheExpirationConfiguration e() {
        return conf;
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
}
