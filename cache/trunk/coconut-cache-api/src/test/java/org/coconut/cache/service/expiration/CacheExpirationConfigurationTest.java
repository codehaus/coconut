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
import org.coconut.predicate.Predicate;
import org.coconut.predicate.Predicates;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests CacheExpirationConfiguration.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheExpirationConfigurationTest {

    static CacheExpirationConfiguration<Integer, String> DEFAULT = new CacheExpirationConfiguration<Integer, String>();

    private final static Predicate<CacheEntry<Integer, String>> DEFAULT_FILTER = Predicates.truePredicate();

    private CacheExpirationConfiguration<Integer, String> conf;

    @Before
    public void setUp() {
        conf = new CacheExpirationConfiguration<Integer, String>();
    }

    /**
     * Test default time to live. The default is that entries never needs to be refreshed.
     */
    @Test
    public void testDefaultTimeToLive() {
        // initial values
        assertEquals(0, conf
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(0, conf
                .getDefaultTimeToLive(TimeUnit.SECONDS));

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

    @Test
    public void testDefaultTimeToLiveXML() throws Exception {
        conf = reloadService(conf);
        assertEquals(0, conf
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(0, conf
                .getDefaultTimeToLive(TimeUnit.SECONDS));

        conf.setDefaultTimeToLive(60, TimeUnit.SECONDS);
        conf = reloadService(conf);
        assertEquals(60 * 1000, conf.getDefaultTimeToLive(TimeUnit.MILLISECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultTimeToLiveIAE() {
        conf.setDefaultTimeToLive(-1, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultTimeToLiveNPE() {
        conf.setDefaultTimeToLive(1, null);
    }

    @Test
    public void testExpirationFilter() {
        assertNull(conf.getExpirationFilter());
        assertEquals(conf, conf.setExpirationFilter(DEFAULT_FILTER));
        assertEquals(DEFAULT_FILTER, conf.getExpirationFilter());
    }

    @Test
    public void testExpirationFilterXML() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getExpirationFilter());

        conf.setExpirationFilter(new LoadableFilter());
        conf = reloadService(conf);
        assertTrue(conf.getExpirationFilter() instanceof LoadableFilter);

        conf.setExpirationFilter(new NonLoadableFilter());
        conf = reloadService(conf);
        assertNull(conf.getExpirationFilter());
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

}
