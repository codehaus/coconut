/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration_ExpirationTest {

    CacheConfiguration<Number, Collection> conf;

    CacheConfiguration.Expiration e;

    Filter<CacheEntry> f = Filters.TRUE;

    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
        e = conf.expiration();
    }

    @Test
    public void testInitialValues() {
        assertNull(e.getFilter());
        assertNull(e.getRefreshFilter());
        assertTrue(e.getRefreshInterval(TimeUnit.NANOSECONDS) < 0);
        assertEquals(Cache.NEVER_EXPIRE, e.getDefaultTimeout(TimeUnit.NANOSECONDS));
        assertEquals(Cache.NEVER_EXPIRE, e.getDefaultTimeout(TimeUnit.SECONDS));
    }

    @Test
    public void testFilter() {
        assertEquals(e, e.setFilter(f));
        assertEquals(f, e.getFilter());
    }

    @Test
    public void testRefreshFilter() {
        assertEquals(e, e.setRefreshFilter(f));
        assertEquals(f, e.getRefreshFilter());
    }

    @Test(expected = NullPointerException.class)
    public void testRefreshInterval() {

        assertEquals(e, e.setRefreshInterval(0, TimeUnit.SECONDS));
        assertEquals(0, e.getRefreshInterval(TimeUnit.NANOSECONDS));

        e.setRefreshInterval(5, TimeUnit.MICROSECONDS);
        assertEquals(5000, e.getRefreshInterval(TimeUnit.NANOSECONDS));

        e.setRefreshInterval(1000, null);
    }

    @Test
    public void testExpiration() {
        assertEquals(conf, conf.expiration().c());
    }

    /**
     * Test default expiration. The default is that entries never expire.
     */
    @Test
    public void testDefaultExpiration() {
        assertEquals(conf, e.setDefaultTimeout(2, TimeUnit.SECONDS).c());
        assertEquals(2l, e.getDefaultTimeout(TimeUnit.SECONDS));
        assertEquals(2l * 1000, e.getDefaultTimeout(TimeUnit.MILLISECONDS));
        assertEquals(2l * 1000 * 1000, e.getDefaultTimeout(TimeUnit.MICROSECONDS));
        assertEquals(2l * 1000 * 1000 * 1000, e.getDefaultTimeout(TimeUnit.NANOSECONDS));

        e.setDefaultTimeout(Cache.NEVER_EXPIRE, TimeUnit.MICROSECONDS);
        assertEquals(Cache.NEVER_EXPIRE, conf.expiration().getDefaultTimeout(
                TimeUnit.SECONDS));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultExpirationIAE() {
        conf.expiration().setDefaultTimeout(0, TimeUnit.MICROSECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testDefaultExpirationNPE() {
        conf.expiration().setDefaultTimeout(1, null);
    }
}
