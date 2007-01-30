/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.util.Collection;

import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.ReplacementPolicy;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheConfiguration_EvictionTest {
    CacheConfiguration<Number, Collection> conf;

    CacheConfiguration.Eviction ee;

    @Before
    public void setUp() {
        conf = CacheConfiguration.create();
        ee = conf.eviction();
    }

    @Test
    public void testEviction() {
        assertEquals(conf, ee.c());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumCapacity() {
        assertEquals(Long.MAX_VALUE, ee.getMaximumCapacity());
        assertEquals(ee, ee.setMaximumCapacity(4));
        assertEquals(4, ee.getMaximumCapacity());
        ee.setMaximumCapacity(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumSize() {
        assertEquals(Integer.MAX_VALUE, ee.getMaximumSize());
        assertEquals(ee, ee.setMaximumSize(4));
        assertEquals(4, ee.getMaximumSize());
        ee.setMaximumSize(-1);
    }

    @Test
    public void testPolicy() {
        assertNull(ee.getPolicy());
        ReplacementPolicy p = Policies.newClock();
        assertEquals(ee, ee.setPolicy(p));
        assertEquals(p, ee.getPolicy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreferableCapacity() {
        assertEquals(Long.MAX_VALUE, ee.getPreferableCapacity());
        assertEquals(ee, ee.setPreferableCapacity(4));
        assertEquals(4, ee.getPreferableCapacity());
        ee.setPreferableCapacity(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPreferableSize() {
        assertEquals(Integer.MAX_VALUE, ee.getPreferableSize());
        assertEquals(ee, ee.setPreferableSize(4));
        assertEquals(4, ee.getPreferableSize());
        ee.setPreferableSize(-1);
    }
}
