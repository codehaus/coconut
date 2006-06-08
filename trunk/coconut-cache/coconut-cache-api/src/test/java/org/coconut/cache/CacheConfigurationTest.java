package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.test.MockTestCase.mockDummy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.CacheConfiguration.ExpirationStrategy;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class CacheConfigurationTest {

    CacheConfiguration<Number, Collection> conf;

    @Before
    public void setUp() {
        conf = CacheConfiguration.newConf();
    }

    @Test
    public void testLoader() {
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);

        assertNull(conf.backend().getLoader());
        assertFalse(conf.backend().hasLoader());

        assertTrue(conf.backend().setLoader(cl) instanceof CacheConfiguration.Backend);

        assertEquals(cl, conf.backend().getLoader());
        assertTrue(conf.backend().hasLoader());

        // narrow bounds
        CacheLoader<Number, List> clI = mockDummy(CacheLoader.class);

        assertTrue(conf.backend().setLoader(clI) instanceof CacheConfiguration.Backend);

        assertEquals(clI, conf.backend().getLoader());
    }

    @Test
    public void testInitialMap() {
        Map<Number, Collection> map = mockDummy(Map.class);
        assertNull(conf.getInitialMap());

        assertEquals(conf, conf.setInitialMap(map));

        assertEquals(map, conf.getInitialMap());

        // narrow bounds
        Map<Integer, List> map2 = mockDummy(Map.class);

        assertEquals(conf, conf.setInitialMap(map2));
        assertEquals(map2, conf.getInitialMap());
    }

    /**
     * Test default expiration. The default is that entries never expire.
     */
    @Test
    public void testDefaultExpiration() {
        assertEquals(Cache.NEVER_EXPIRE, conf.expiration()
                .getDefaultTimeout(TimeUnit.NANOSECONDS));
        assertEquals(Cache.NEVER_EXPIRE, conf.expiration()
                .getDefaultTimeout(TimeUnit.SECONDS));

        assertEquals(conf, conf.expiration()
                .setDefaultTimeout(2, TimeUnit.SECONDS).c());
        assertEquals(2l, conf.expiration().getDefaultTimeout(TimeUnit.SECONDS));
        assertEquals(2l * 1000, conf.expiration()
                .getDefaultTimeout(TimeUnit.MILLISECONDS));
        assertEquals(2l * 1000 * 1000, conf.expiration()
                .getDefaultTimeout(TimeUnit.MICROSECONDS));
        assertEquals(2l * 1000 * 1000 * 1000, conf.expiration()
                .getDefaultTimeout(TimeUnit.NANOSECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultExpirationIAE() {
        conf.expiration().setDefaultTimeout(0, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultExpirationNPE() {
        conf.expiration().setDefaultTimeout(1, null);
    }

    @Test
    public void testExpirationStrategy() {
        assertEquals(ExpirationStrategy.ON_EVICT, conf.expiration().getStrategy());

        assertTrue(conf.expiration().setStrategy(ExpirationStrategy.LAZY) instanceof CacheConfiguration.Expiration);

        assertEquals(ExpirationStrategy.LAZY, conf.expiration().getStrategy());

        assertTrue(conf.expiration().setStrategy(ExpirationStrategy.STRICT) instanceof CacheConfiguration.Expiration);

        assertEquals(ExpirationStrategy.STRICT, conf.expiration().getStrategy());

    }

    @Test(expected = NullPointerException.class)
    public void testExpirationStrategyNullPointer() {
        conf.expiration().setStrategy(null);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CacheConfigurationTest.class);
    }
}