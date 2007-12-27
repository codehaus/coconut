/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Predicate;
import org.junit.Before;
import org.junit.Test;

public class CacheLoadingConfigurationTest {

    static CacheLoadingConfiguration<Integer, String> DEFAULT = new CacheLoadingConfiguration<Integer, String>();

    private final static Predicate<CacheEntry<Integer, String>> DEFAULT_FILTER = Predicates
            .truePredicate();

    private final static CacheLoader<Integer, String> DEFAULT_LOADER = new LoadableCacheLoader();

    private CacheLoadingConfiguration<Integer, String> conf;

    @Before
    public void setUp() {
        conf = new CacheLoadingConfiguration<Integer, String>();
    }

    /**
     * Test default time to live. The default is that entries never needs to be refreshed.
     */
    @Test
    public void testDefaultTimeToRefresh() {
        // initial values
        assertEquals(0, conf.getDefaultTimeToRefresh(TimeUnit.NANOSECONDS));
        assertEquals(0, conf.getDefaultTimeToRefresh(TimeUnit.SECONDS));

        assertEquals(conf, conf.setDefaultTimeToRefresh(2, TimeUnit.SECONDS));

        assertEquals(2l, conf.getDefaultTimeToRefresh(TimeUnit.SECONDS));
        assertEquals(2l * 1000, conf.getDefaultTimeToRefresh(TimeUnit.MILLISECONDS));
        assertEquals(2l * 1000 * 1000, conf
                .getDefaultTimeToRefresh(TimeUnit.MICROSECONDS));
        assertEquals(2l * 1000 * 1000 * 1000, conf
                .getDefaultTimeToRefresh(TimeUnit.NANOSECONDS));

        conf.setDefaultTimeToRefresh(Long.MAX_VALUE, TimeUnit.MICROSECONDS);
        assertEquals(Long.MAX_VALUE, conf.getDefaultTimeToRefresh(TimeUnit.SECONDS));
    }

    @Test
    public void testDefaultTimeToRefreshXML() throws Exception {
        conf = reloadService(conf);
        assertEquals(0, conf.getDefaultTimeToRefresh(TimeUnit.NANOSECONDS));
        assertEquals(0, conf.getDefaultTimeToRefresh(TimeUnit.SECONDS));

        conf.setDefaultTimeToRefresh(60, TimeUnit.SECONDS);
        conf = reloadService(conf);
        assertEquals(60 * 1000, conf.getDefaultTimeToRefresh(TimeUnit.MILLISECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultTimeToLiveIAE() {
        conf.setDefaultTimeToRefresh(-1, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultTimeToLiveNPE() {
        conf.setDefaultTimeToRefresh(1, null);
    }

    @Test
    public void testExpirationFilter() {
        assertNull(conf.getRefreshFilter());
        assertEquals(conf, conf.setRefreshFilter(DEFAULT_FILTER));
        assertEquals(DEFAULT_FILTER, conf.getRefreshFilter());
    }

    @Test
    public void testExpirationFilterXML() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getRefreshFilter());

        conf.setRefreshFilter(new LoadableFilter());
        conf = reloadService(conf);
        assertTrue(conf.getRefreshFilter() instanceof LoadableFilter);

        conf.setRefreshFilter(new NonLoadableFilter());
        conf = reloadService(conf);
        assertNull(conf.getRefreshFilter());
    }

    @Test
    public void testLoader() {
        assertNull(conf.getLoader());
        assertEquals(conf, conf.setLoader(DEFAULT_LOADER));
        assertEquals(DEFAULT_LOADER, conf.getLoader());
    }

    @Test
    public void testLoaderXML() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getLoader());

        conf.setLoader(new LoadableCacheLoader());
        conf = reloadService(conf);
        assertTrue(conf.getLoader() instanceof LoadableCacheLoader);

        conf.setLoader(new NonLoadableCacheLoader());
        conf = reloadService(conf);
        assertNull(conf.getLoader());
    }

    public static class LoadableFilter implements Predicate<CacheEntry<Integer, String>> {
        public boolean evaluate(CacheEntry<Integer, String> element) {
            return false;
        }
    }

    public class NonLoadableFilter implements Predicate<CacheEntry<Integer, String>> {
        public boolean evaluate(CacheEntry<Integer, String> element) {
            return false;
        }
    }

    public static class LoadableCacheLoader extends AbstractCacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            return null;
        }
    }

    public class NonLoadableCacheLoader extends AbstractCacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            return null;
        }
    }
}
