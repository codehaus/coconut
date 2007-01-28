/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.test.MockTestCase.mockDummy;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration_BackendTest {
    CacheConfiguration<Number, Collection> conf;

    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
    }

    @Test
    public void testBackend() {
        assertEquals(conf, conf.backend().c());
    }
    
    @Test
    public void testLoader() {
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);

        assertNull(conf.backend().getBackend());
        //assertFalse(conf.backend().hasBackend());

        assertTrue(conf.backend().setBackend(cl) instanceof CacheConfiguration.Backend);

        assertEquals(cl, conf.backend().getBackend());
        //assertTrue(conf.backend().hasBackend());

        // narrow bounds
        CacheLoader<Number, List> clI = mockDummy(CacheLoader.class);

        assertTrue(conf.backend().setBackend(clI) instanceof CacheConfiguration.Backend);

        assertEquals(clI, conf.backend().getBackend());
    }

    @Test
    public void testExtendedLoader() {
        CacheLoader<Number, CacheEntry<Number, Collection>> ecl = mockDummy(CacheLoader.class);

        assertNull(conf.backend().getExtendedBackend());

        assertTrue(conf.backend().setExtendedBackend(ecl) instanceof CacheConfiguration.Backend);

        assertEquals(ecl, conf.backend().getExtendedBackend());
    }
    //assertTrue(conf.backend().hasBackend());

    @Test(expected = IllegalStateException.class)
    public void testLoaderSetThenExtendedLoader() {
        CacheLoader<Number, ? extends CacheEntry<Number, Collection>> ecl = mockDummy(CacheLoader.class);
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);
        conf.backend().setBackend(cl);
        conf.backend().setExtendedBackend(ecl);
    }

    @Test(expected = IllegalStateException.class)
    public void testExtendedLoaderSetThenLoader() {
        CacheLoader<Number, ? extends CacheEntry<Number, Collection>> ecl = mockDummy(CacheLoader.class);
        CacheLoader<Number, Collection> cl = mockDummy(CacheLoader.class);
        conf.backend().setExtendedBackend(ecl);
        conf.backend().setBackend(cl);
    }
}
