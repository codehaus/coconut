/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheHelper;
import org.coconut.cache.internal.service.attribute.DefaultCacheAttributeService;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
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

    private static final CacheEntry<Integer, String> NEVER_EXPIRE;

    private static final CacheEntry<Integer, String> EXPIRE_AT_10;
    private final Filter<CacheEntry<Integer, String>> TRUE = Filters.trueFilter();

    private final Filter<CacheEntry<Integer, String>> FALSE = Filters.falseFilter();

    private CacheExpirationConfiguration<Integer, String> conf;

    private Clock.DeterministicClock clock;

    private DefaultCacheExpirationService<Integer, String> s;

    private CacheHelper<Integer, String> helper = new JUnit4Mockery()
            .mock(CacheHelper.class);

    private CacheExceptionHandler<Integer, String> errorHandler;

    private InternalCacheAttributeService attributeFactory = new DefaultCacheAttributeService();

    static {
        CacheEntry dd = new JUnit4Mockery().mock(CacheEntry.class);

        MockTestCase m = new MockTestCase();
        Mock n = m.mock(CacheEntry.class);
        n.stubs().method("getExpirationTime").will(
                m.returnValue(CacheExpirationService.NEVER_EXPIRE));
        NEVER_EXPIRE = (CacheEntry<Integer, String>) n.proxy();
        n = m.mock(CacheEntry.class);
        n.stubs().method("getExpirationTime").will(m.returnValue(10l));
        EXPIRE_AT_10 = (CacheEntry<Integer, String>) n.proxy();
    }

    @Before
    public void setUp() {
        clock = new Clock.DeterministicClock();
        conf = new CacheExpirationConfiguration<Integer, String>();
        errorHandler = new AbstractTester<Integer, String>();
        initialize();
    }

    private void initialize() {
        s = new DefaultCacheExpirationService<Integer, String>(null, clock, helper, conf,
                attributeFactory);
    }

    @Test
    public void testIsExpired_Time() {
        clock.setTimestamp(9);
        assertFalse(s.isExpired(EXPIRE_AT_10));
        assertFalse(s.isExpired(NEVER_EXPIRE));
        clock.setTimestamp(10);
        assertTrue(s.isExpired(EXPIRE_AT_10));
        assertFalse(s.isExpired(NEVER_EXPIRE));
        clock.setTimestamp(Long.MAX_VALUE);
        assertTrue(s.isExpired(EXPIRE_AT_10));
        assertFalse(s.isExpired(NEVER_EXPIRE));
    }

// @Test
// public void testGetExpirationTime() {
// assertEquals(Long.MAX_VALUE, s.innerGetExpirationTime(null, null, null));
// assertEquals(Long.MAX_VALUE, s.innerGetExpirationTime(null, null,
// new AttributeMaps.DefaultAttributeMap()));
//
// AttributeMap dam = new AttributeMaps.DefaultAttributeMap();
// dam.putLong(CacheAttributes.TIME_TO_LIVE_NS,
// CacheExpirationService.NEVER_EXPIRE);
// assertEquals(Long.MAX_VALUE, s.innerGetExpirationTime(null, null, dam));
// dam.putLong(CacheAttributes.TIME_TO_LIVE_NS, TimeUnit.MILLISECONDS.toNanos(5));
// assertEquals(5l, s.innerGetExpirationTime(null, null, dam));
//
// s.setDefaultTimeToLive(10, TimeUnit.MILLISECONDS);
// assertEquals(10l, s.innerGetExpirationTime(null, null,
// new AttributeMaps.DefaultAttributeMap()));
// assertEquals(5l, s.innerGetExpirationTime(null, null, dam));
// assertEquals(10l, s.innerGetExpirationTime(null, null, null));
// clock.setTimestamp(50);
// assertEquals(60l, s.innerGetExpirationTime(null, null, null));
//
// }

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
        // s.innerGetExpirationTime(123, null, dam);

// assertTrue(ref.get().contains("-1"));
// assertTrue(ref.get().contains("123"));
    }

    static class AbstractTester<K, V> extends CacheExceptionHandler<K, V> {

        @Override
        public void handleError(CacheExceptionContext<K, V> context, Error cause) {}

        @Override
        public void handleException(CacheExceptionContext<K, V> context, Exception cause) {}

        @Override
        public void handleRuntimeException(CacheExceptionContext<K, V> context,
                RuntimeException cause) {}

        @Override
        public void handleWarning(CacheExceptionContext<K, V> context, String warning) {}

    }
}
