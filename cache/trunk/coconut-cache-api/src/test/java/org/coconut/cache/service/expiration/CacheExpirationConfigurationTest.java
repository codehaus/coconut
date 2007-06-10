/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests CacheExpirationConfiguration.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExpirationConfigurationTest {

    static CacheExpirationConfiguration<Integer, String> DEFAULT = new CacheExpirationConfiguration<Integer, String>();

    private CacheExpirationConfiguration<Integer, String> conf;

    private Filter<CacheEntry<Integer, String>> f = Filters.trueFilter();

    @Before
    public void setUp() {
        conf = new CacheExpirationConfiguration<Integer, String>();
    }

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

        conf.setDefaultTimeToLive(CacheExpirationService.NEVER_EXPIRE,
                TimeUnit.MICROSECONDS);
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf
                .getDefaultTimeToLive(TimeUnit.SECONDS));

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
    public void testExpirationFilter() {
        assertNull(conf.getExpirationFilter());
        assertEquals(conf, conf.setExpirationFilter(f));
        assertEquals(f, conf.getExpirationFilter());
    }
    
    @Test
    public void testExpiration() throws Exception {
        conf.setDefaultTimeToLive(60, TimeUnit.SECONDS);
        conf.setExpirationFilter(new MyFilter());

        conf = reloadService(conf);
        assertTrue(conf.getExpirationFilter() instanceof MyFilter);

        assertEquals(60 * 1000, conf.getDefaultTimeToLive(TimeUnit.MILLISECONDS));

    }

    @Test
    public void testFilter() {
        assertEquals(conf, conf.setExpirationFilter(f));
        assertEquals(f, conf.getExpirationFilter());
    }

    @Test
    public void testIgnoreFilters() throws Exception {
        conf.setExpirationFilter(new MyFilter3(""));
        conf = reloadService(conf);
        assertNull(conf.getExpirationFilter());
    }

    @Test
    public void testInitialValues() {
        assertNull(conf.getExpirationFilter());
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf
                .getDefaultTimeToLive(TimeUnit.SECONDS));
    }

    @Test
    public void testNoop() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getExpirationFilter());
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, conf
                .getDefaultTimeToLive(TimeUnit.SECONDS));
    }

    public static class MyFilter implements Filter<CacheEntry<Integer, String>> {
        public boolean accept(CacheEntry<Integer, String> element) {
            return false;
        }
    }

    public static class MyFilter2 implements Filter<CacheEntry<Integer, String>> {
        public boolean accept(CacheEntry<Integer, String> element) {
            return false;
        }
    }

    public static class MyFilter3 implements Filter<CacheEntry<Integer, String>> {
        public MyFilter3(Object foo) {}

        public boolean accept(CacheEntry<Integer, String> element) {
            return false;
        }
    }

    static class MyFilter4 implements Filter<CacheEntry<Integer, String>> {
        private MyFilter4() {}

        public boolean accept(CacheEntry<Integer, String> element) {
            return false;
        }
    }

}
