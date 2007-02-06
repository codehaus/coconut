/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.rw;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.filter.Filter;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest_ExpirationTest {

    CacheConfiguration conf;

    @Before
    public void setup() {
        conf = CacheConfiguration.create();
    }

    protected CacheConfiguration.Expiration e() {
        return conf.expiration();
    }

    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertNull(e().getFilter());
        assertNull(e().getRefreshFilter());
        assertTrue(e().getRefreshInterval(TimeUnit.NANOSECONDS) < 0);
        assertEquals(Cache.NEVER_EXPIRE, e().getDefaultTimeout(TimeUnit.NANOSECONDS));
        assertEquals(Cache.NEVER_EXPIRE, e().getDefaultTimeout(TimeUnit.SECONDS));
    }

    @Test
    public void testExpiration() throws Exception {
        e().setDefaultTimeout(60, TimeUnit.SECONDS);
        e().setRefreshInterval(120, TimeUnit.SECONDS);
        e().setFilter(new MyFilter());
        e().setRefreshFilter(new MyFilter2());

        conf = rw(conf);
        assertTrue(e().getFilter() instanceof MyFilter);
        assertTrue(e().getRefreshFilter() instanceof MyFilter2);
        assertEquals(60 * 1000, e().getDefaultTimeout(TimeUnit.MILLISECONDS));
        assertEquals(120 * 1000, e().getRefreshInterval(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testIgnoreFilters() throws Exception {
        e().setFilter(new MyFilter3(""));
        e().setRefreshFilter(new MyFilter4());
        conf = rw(conf);
        assertNull(e().getFilter());
        assertNull(e().getRefreshFilter());
    }

    @Test
    public void testCornerCase() throws Exception {
        // coverage mostly
        e().setDefaultTimeout(30, TimeUnit.SECONDS);
        conf = rw(conf);
        assertNull(e().getFilter());
        assertNull(e().getRefreshFilter());
        assertTrue(e().getRefreshInterval(TimeUnit.NANOSECONDS) < 0);
        assertEquals(30, e().getDefaultTimeout(TimeUnit.SECONDS));
        conf = CacheConfiguration.create();
        e().setRefreshInterval(30, TimeUnit.SECONDS);
        conf = rw(conf);
        assertEquals(Cache.NEVER_EXPIRE, e().getDefaultTimeout(TimeUnit.SECONDS));
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
}
