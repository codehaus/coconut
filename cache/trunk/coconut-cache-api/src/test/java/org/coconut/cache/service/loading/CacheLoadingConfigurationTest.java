/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfigurator.reloadService;
import static org.coconut.test.MockTestCase.mockDummy;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.expiration.CacheExpirationConfigurationTest.MyFilter2;
import org.coconut.core.AttributeMap;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

@SuppressWarnings("unchecked")
public class CacheLoadingConfigurationTest {

    CacheLoadingConfiguration conf;

    static CacheLoadingConfiguration DEFAULT = new CacheLoadingConfiguration();

    @Before
    public void setUp() {
        conf = new CacheLoadingConfiguration();
    }

    @Test
    public void testLoader() {
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);

        assertNull(conf.getLoader());
        // assertFalse(conf.backend().hasBackend());

        assertEquals(conf, conf.setLoader(cl));

        assertEquals(cl, conf.getLoader());
        // assertTrue(conf.hasBackend());

        // narrow bounds
        CacheLoader<Number, List> clI = mockDummy(CacheLoader.class);

        assertEquals(conf, conf.setLoader(clI));

        assertEquals(clI, conf.getLoader());
    }

    // assertTrue(conf.hasBackend());

    @Test
    public void testNoop() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getLoader());
        assertEquals(Long.MAX_VALUE, conf.getDefaultRefreshTime(TimeUnit.MILLISECONDS));
        assertNull(conf.getRefreshFilter());
    }

    @Test
    public void testBackend() throws Exception {
        conf.setLoader(new MyBackend1());
        // System.out.println(conf);
        conf = reloadService(conf);
        assertTrue(conf.getLoader() instanceof MyBackend1);
    }

    @Test
    public void testBackendFail() throws Exception {
        conf.setLoader(new MyBackend2(""));
        conf = reloadService(conf);
        assertNull(conf.getLoader());
    }

    @Test
    public void testIgnoreFilters() throws Exception {
        conf.setRefreshFilter(new MyFilter4());
        conf = reloadService(conf);
        assertNull(conf.getRefreshFilter());
    }

    @Test
    public void testInitialValues() {
        assertNull(conf.getRefreshFilter());
        assertEquals(Long.MAX_VALUE, conf.getDefaultRefreshTime(TimeUnit.NANOSECONDS));
    }

    Filter<CacheEntry> f = Filters.TRUE;

    @Test
    public void testReload() throws Exception {
        conf.setDefaultRefreshTime(120, TimeUnit.SECONDS);
        conf.setRefreshFilter(new MyFilter2());
        conf = reloadService(conf);
        assertTrue(conf.getRefreshFilter() instanceof MyFilter2);
        assertEquals(120 * 1000, conf.getDefaultRefreshTime(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testNoop2() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getRefreshFilter());
        assertEquals(Long.MAX_VALUE, conf.getDefaultRefreshTime(TimeUnit.NANOSECONDS));
    }

    @Test
    public void testReloadFilter() {
        assertEquals(conf, conf.setRefreshFilter(f));
        assertEquals(f, conf.getRefreshFilter());
    }

    @Test
    public void testReloadInterval() {

        assertEquals(conf, conf.setDefaultRefreshTime(1, TimeUnit.SECONDS));
        assertEquals(1 * 1000 * 1000 * 1000, conf
                .getDefaultRefreshTime(TimeUnit.NANOSECONDS));

        conf.setDefaultRefreshTime(5, TimeUnit.MICROSECONDS);
        assertEquals(5000, conf.getDefaultRefreshTime(TimeUnit.NANOSECONDS));

    }

    @Test(expected = NullPointerException.class)
    public void testReloadIntervalNPE() {
        conf.setDefaultRefreshTime(1000, null);
    }

    public static class MyBackend1 implements CacheLoader<Integer, String> {
        /**
         * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
         */
        public String load(Integer key, AttributeMap attributes) throws Exception {
            return null;
        }
    }

    public static class MyBackend2 extends MyBackend1 {
        public MyBackend2(Object foo) {}
    }

    static class MyFilter4 implements Filter {
        private MyFilter4() {}

        public boolean accept(Object element) {
            return false;
        }
    }
}
