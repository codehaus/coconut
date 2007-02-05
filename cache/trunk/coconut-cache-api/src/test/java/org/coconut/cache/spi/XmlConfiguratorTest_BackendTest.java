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
import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.filter.Filter;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfiguratorTest_BackendTest {

    XmlConfigurator c;

    CacheConfiguration conf;

    @Before
    public void setup() {
        conf = CacheConfiguration.create();
        c = XmlConfigurator.getInstance();
    }

    protected CacheConfiguration.Backend b() {
        return conf.backend();
    }

    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertNull(b().getBackend());
        assertNull(b().getExtendedBackend());
    }

    @Test
    public void testBackend() throws Exception {
        b().setBackend(new MyBackend1());
        conf = rw(conf);
        assertTrue(b().getBackend() instanceof MyBackend1);
        assertNull(b().getExtendedBackend());
    }

    @Test
    public void testBackendFail() throws Exception {
        b().setBackend(new MyBackend2(""));
        conf = rw(conf);
        assertNull(b().getBackend());
        assertNull(b().getExtendedBackend());
    }

    @Test
    public void testExtendedBackend() throws Exception {
        b().setExtendedBackend(new MyBackend1());
        conf = rw(conf);
        assertNull(b().getBackend());
        assertTrue(b().getExtendedBackend() instanceof MyBackend1);
    }

    @Test
    public void testExtendedBackendFail() throws Exception {
        b().setExtendedBackend(new MyBackend2(""));
        conf = rw(conf);
        assertNull(b().getBackend());
        assertNull(b().getExtendedBackend());
    }

    public static class MyBackend1 extends AbstractCacheLoader {
        /**
         * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
         */
        public Object load(Object key) throws Exception {
            return null;
        }
    }

    public static class MyBackend2 extends MyBackend1 {
        public MyBackend2(Object foo) {
        }
    }
}
