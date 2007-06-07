/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheExpirationServiceTest {

    private final Filter<CacheEntry<Integer, String>> TRUE = Filters.trueFilter();

    private final Filter<CacheEntry<Integer, String>> FALSE = Filters.falseFilter();

    private CacheExpirationConfiguration<Integer, String> conf;

    private Clock.DeterministicClock clock;

    private UnsynchronizedCacheExpirationService<Integer, String> s;

    private CacheHelper<Integer, String> helper = new JUnit4Mockery()
            .mock(CacheHelper.class);

    private AbstractCacheExceptionHandler<Integer, String> errorHandler;

    private InternalCacheAttributeService attributeFactory = new JUnit4Mockery()
            .mock(InternalCacheAttributeService.class);

    private static final CacheEntry<Integer, String> neverExpire;

    private static final CacheEntry<Integer, String> expireAt10;
    static {
        CacheEntry dd = new JUnit4Mockery().mock(CacheEntry.class);

        MockTestCase m = new MockTestCase();
        Mock n = m.mock(CacheEntry.class);
        n.stubs().method("getExpirationTime").will(
                m.returnValue(CacheExpirationService.NEVER_EXPIRE));
        neverExpire = (CacheEntry<Integer, String>) n.proxy();
        n = m.mock(CacheEntry.class);
        n.stubs().method("getExpirationTime").will(m.returnValue(10l));
        expireAt10 = (CacheEntry<Integer, String>) n.proxy();
    }

    @Before
    public void setup() {
        clock = new Clock.DeterministicClock();
        conf = new CacheExpirationConfiguration<Integer, String>();
        errorHandler = new AbstractTester<Integer, String>();
        initialize();
    }

    private void initialize() {
        CacheExceptionHandlingConfiguration<Integer, String> econf = new CacheExceptionHandlingConfiguration<Integer, String>();
        econf.setExceptionHandler(errorHandler);
        s = new UnsynchronizedCacheExpirationService<Integer, String>(helper, conf,
                clock, econf, attributeFactory);
    }

    @Test
    public void testSetGetFilter() {
        assertNull(s.getExpirationFilter());
        s.setExpirationFilter(TRUE);
        assertSame(TRUE, s.getExpirationFilter());
        conf = new CacheExpirationConfiguration<Integer, String>()
                .setExpirationFilter(FALSE);
        initialize();
        assertSame(FALSE, s.getExpirationFilter());
    }

    @Test
    public void testSetGetDefaultTimeToLive() {
        assertEquals(CacheExpirationService.NEVER_EXPIRE, s
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, s
                .getDefaultTimeToLive(TimeUnit.SECONDS));
        assertEquals(CacheExpirationService.NEVER_EXPIRE, s.getDefaultTimeToLiveMs());

        s.setDefaultTimeToLive(2 * 1000, TimeUnit.MILLISECONDS);
        assertEquals(2 * 1000 * 1000 * 1000l, s
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(2l, s.getDefaultTimeToLive(TimeUnit.SECONDS));
        assertEquals(2000l, s.getDefaultTimeToLiveMs());

        conf = new CacheExpirationConfiguration<Integer, String>().setDefaultTimeToLive(
                5, TimeUnit.SECONDS);
        initialize();
        assertEquals(5 * 1000 * 1000 * 1000l, s
                .getDefaultTimeToLive(TimeUnit.NANOSECONDS));
        assertEquals(5l, s.getDefaultTimeToLive(TimeUnit.SECONDS));
        assertEquals(5000l, s.getDefaultTimeToLiveMs());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDefaultTimeToLiveIAE() {
        s.setDefaultTimeToLive(0, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testSetDefaultTimeToLiveNPE() {
        s.setDefaultTimeToLive(123, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDefaultTimeToLiveMsIAE() {
        s.setDefaultTimeToLiveMs(0);
    }

    @Test
    public void testIsExpired_Time() {
        clock.setTimestamp(9);
        assertFalse(s.innerIsExpired(expireAt10));
        assertFalse(s.innerIsExpired(neverExpire));
        clock.setTimestamp(10);
        assertTrue(s.innerIsExpired(expireAt10));
        assertFalse(s.innerIsExpired(neverExpire));
        clock.setTimestamp(Long.MAX_VALUE);
        assertTrue(s.innerIsExpired(expireAt10));
        assertFalse(s.innerIsExpired(neverExpire));
    }

    @Test
    public void testIsExpired_Filters() {
        s.setExpirationFilter(TRUE);
        assertTrue(s.innerIsExpired(neverExpire));
        s.setExpirationFilter(FALSE);
        assertFalse(s.innerIsExpired(neverExpire));
    }

    @Test
    public void testIsExpired_TimeAndFilter() {
        s.setExpirationFilter(FALSE);
        clock.setTimestamp(400);
        assertTrue(s.innerIsExpired(expireAt10));
    }

    @Test
    public void testGetExpirationTime() {
        assertEquals(Long.MAX_VALUE, s.innerGetExpirationTime(null, null, null));
        assertEquals(Long.MAX_VALUE, s.innerGetExpirationTime(null, null,
                new AttributeMaps.DefaultAttributeMap()));

        AttributeMap dam = new AttributeMaps.DefaultAttributeMap();
        dam.putLong(CacheAttributes.TIME_TO_LIVE_NS,
                CacheExpirationService.NEVER_EXPIRE);
        assertEquals(Long.MAX_VALUE, s.innerGetExpirationTime(null, null, dam));
        dam.putLong(CacheAttributes.TIME_TO_LIVE_NS, TimeUnit.MILLISECONDS.toNanos(5));
        assertEquals(5l, s.innerGetExpirationTime(null, null, dam));

        s.setDefaultTimeToLive(10, TimeUnit.MILLISECONDS);
        assertEquals(10l, s.innerGetExpirationTime(null, null,
                new AttributeMaps.DefaultAttributeMap()));
        assertEquals(5l, s.innerGetExpirationTime(null, null, dam));
        assertEquals(10l, s.innerGetExpirationTime(null, null, null));
        clock.setTimestamp(50);
        assertEquals(60l, s.innerGetExpirationTime(null, null, null));

    }

    @Test
    public void testErrorHandler() {
        final AtomicReference<String> ref = new AtomicReference<String>();
        errorHandler = new AbstractTester<Integer, String>() {
            public synchronized void warning(String warning) {
                ref.set(warning);
            }
        };
        initialize();
        AttributeMap dam = new AttributeMaps.DefaultAttributeMap();
        dam.putLong(CacheAttributes.TIME_TO_LIVE_NS, -1);
        s.innerGetExpirationTime(123, null, dam);

        assertTrue(ref.get().contains("-1"));
        assertTrue(ref.get().contains("123"));
    }

    static class AbstractTester<K, V> extends AbstractCacheExceptionHandler<K, V> {

        /**
         * @see org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#warning(java.lang.String)
         */
        @Override
        public void warning(String warning) {
        // TODO Auto-generated method stub

        }

    }
}
