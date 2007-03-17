/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.test.MockTestCase.mockDummy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.XmlConfigurator;
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

    static CacheLoadingConfiguration rw(CacheLoadingConfiguration conf) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CacheConfiguration cc = CacheConfiguration.create();
        cc.addService(conf);
        XmlConfigurator.getInstance().to(cc, os);
        cc = XmlConfigurator.getInstance().from(
                new ByteArrayInputStream(os.toByteArray()));
        return (CacheLoadingConfiguration) cc
                .getServiceConfiguration(CacheLoadingConfiguration.class);
    }

    @Test
    public void testLoader() {
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);

        assertNull(conf.getBackend());
        // assertFalse(conf.backend().hasBackend());

        assertEquals(conf, conf.setBackend(cl));

        assertEquals(cl, conf.getBackend());
        // assertTrue(conf.hasBackend());

        // narrow bounds
        CacheLoader<Number, List> clI = mockDummy(CacheLoader.class);

        assertEquals(conf, conf.setBackend(clI));

        assertEquals(clI, conf.getBackend());
    }

    @Test
    public void testExtendedLoader() {
        CacheLoader<Number, CacheEntry<Number, Collection>> ecl = mockDummy(CacheLoader.class);

        assertNull(conf.getExtendedBackend());

        assertEquals(conf, conf.setExtendedBackend(ecl));

        assertEquals(ecl, conf.getExtendedBackend());
    }

    // assertTrue(conf.hasBackend());

    @Test(expected = IllegalStateException.class)
    public void testLoaderSetThenExtendedLoader() {
        CacheLoader<Number, ? extends CacheEntry<Number, Collection>> ecl = mockDummy(CacheLoader.class);
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);
        conf.setBackend(cl);
        conf.setExtendedBackend(ecl);
    }

    @Test(expected = IllegalStateException.class)
    public void testExtendedLoaderSetThenLoader() {
        CacheLoader<Number, ? extends CacheEntry<Number, Collection>> ecl = mockDummy(CacheLoader.class);
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);
        conf.setExtendedBackend(ecl);
        conf.setBackend(cl);
    }

    @Test
    public void testNoop() throws Exception {
        conf = rw(conf);
        assertNull(conf.getBackend());
        assertNull(conf.getExtendedBackend());
    }

    @Test
    public void testBackend() throws Exception {
        conf.setBackend(new MyBackend1());
        conf = rw(conf);
        assertTrue(conf.getBackend() instanceof MyBackend1);
        assertNull(conf.getExtendedBackend());
    }

    @Test
    public void testBackendFail() throws Exception {
        conf.setBackend(new MyBackend2(""));
        conf = rw(conf);
        assertNull(conf.getBackend());
        assertNull(conf.getExtendedBackend());
    }

    @Test
    public void testExtendedBackend() throws Exception {
        conf.setExtendedBackend(new MyBackend1());
        conf = rw(conf);
        assertNull(conf.getBackend());
        assertTrue(conf.getExtendedBackend() instanceof MyBackend1);
    }

    @Test
    public void testExtendedBackendFail() throws Exception {
        conf.setExtendedBackend(new MyBackend2(""));
        conf = rw(conf);
        assertNull(conf.getBackend());
        assertNull(conf.getExtendedBackend());
    }

    public static class MyBackend1 extends AbstractCacheLoader<Integer, String> {
        /**
         * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
         */
        public String load(Integer key) throws Exception {
            return null;
        }
    }

    public static class MyBackend2 extends MyBackend1 {
        public MyBackend2(Object foo) {
        }
    }
}
