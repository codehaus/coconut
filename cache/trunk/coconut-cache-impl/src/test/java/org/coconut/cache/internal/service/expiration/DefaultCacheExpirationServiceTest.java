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
import org.coconut.cache.internal.DefaultAttributeMap;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
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

    private DefaultCacheExpirationService<Integer, String> s;

    private CacheHelper<Integer, String> helper = new JUnit4Mockery()
            .mock(CacheHelper.class);

    private CacheExceptionHandler<Integer, String> errorHandler;

    private InternalCacheAttributeService attributeFactory = new JUnit4Mockery()
            .mock(InternalCacheAttributeService.class);

    private static final CacheEntry<Integer, String> neverExpire;

    private static final CacheEntry<Integer, String> expireAt10;
    static {
        CacheEntry dd=new JUnit4Mockery().mock(CacheEntry.class);
        
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
        errorHandler = new CacheExceptionHandler<Integer, String>();
        initialize();
    }

    private void initialize() {
        s = new DefaultCacheExpirationService<Integer, String>(helper, conf, clock,
                errorHandler, attributeFactory);
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
        assertFalse(s.isExpired(expireAt10));
        assertFalse(s.isExpired(neverExpire));
        clock.setTimestamp(10);
        assertTrue(s.isExpired(expireAt10));
        assertFalse(s.isExpired(neverExpire));
        clock.setTimestamp(Long.MAX_VALUE);
        assertTrue(s.isExpired(expireAt10));
        assertFalse(s.isExpired(neverExpire));
    }

    @Test
    public void testIsExpired_Filters() {
        s.setExpirationFilter(TRUE);
        assertTrue(s.isExpired(neverExpire));
        s.setExpirationFilter(FALSE);
        assertFalse(s.isExpired(neverExpire));
    }

    @Test
    public void testIsExpired_TimeAndFilter() {
        s.setExpirationFilter(FALSE);
        clock.setTimestamp(400);
        assertTrue(s.isExpired(expireAt10));
    }

    @Test
    public void testGetExpirationTime() {
        assertEquals(Long.MAX_VALUE, s.getExpirationTime(null, null, null));
        assertEquals(Long.MAX_VALUE, s.getExpirationTime(null, null,
                new DefaultAttributeMap()));

        DefaultAttributeMap dam = new DefaultAttributeMap();
        dam.putLong(CacheAttributes.TIME_TO_LIVE_NANO,
                CacheExpirationService.NEVER_EXPIRE);
        assertEquals(Long.MAX_VALUE, s.getExpirationTime(null, null, dam));
        dam
                .putLong(CacheAttributes.TIME_TO_LIVE_NANO, TimeUnit.MILLISECONDS
                        .toNanos(5));
        assertEquals(5l, s.getExpirationTime(null, null, dam));

        s.setDefaultTimeToLive(10, TimeUnit.MILLISECONDS);
        assertEquals(10l, s.getExpirationTime(null, null, new DefaultAttributeMap()));
        assertEquals(5l, s.getExpirationTime(null, null, dam));
        assertEquals(10l, s.getExpirationTime(null, null, null));
        clock.setTimestamp(50);
        assertEquals(60l, s.getExpirationTime(null, null, null));

    }

    @Test
    public void testErrorHandler() {
        final AtomicReference<String> ref = new AtomicReference<String>();
        errorHandler = new CacheExceptionHandler<Integer, String>() {
            public synchronized void warning(String warning) {
                ref.set(warning);
            }
        };
        initialize();
        DefaultAttributeMap dam = new DefaultAttributeMap();
        dam.putLong(CacheAttributes.TIME_TO_LIVE_NANO, -1);
        s.getExpirationTime(123, null, dam);

        assertTrue(ref.get().contains("-1"));
        assertTrue(ref.get().contains("123"));
    }
}