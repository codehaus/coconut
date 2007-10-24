/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests EvictionService.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EvictionService extends AbstractCacheTCKTest {
    static CacheEvictionConfiguration<?, ?> DEFAULT = new CacheEvictionConfiguration();

    @Before
    public void setup() {
        c = newCache();
    }

    @Test
    public void testServiceAvailable() {
        assertNotNull(c.getService(CacheEvictionService.class));
    }

    /**
     * Tests maximum capacity.
     */
    @Test
    public void maximumVolume() {
        assertEquals(Long.MAX_VALUE, eviction().getMaximumVolume());
        eviction().setMaximumVolume(1000);
        assertEquals(1000, eviction().getMaximumVolume());

        // start value
        c = newCache(newConf().eviction().setMaximumVolume(5000));
        assertEquals(5000, eviction().getMaximumVolume());
    }

    /**
     * Tests maximum capacity IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void maximumCapacityIAE() {
        eviction().setMaximumVolume(-1);
    }

    /**
     * Tests maximum capacity.
     */
    @Test
    public void maximumSize() {
        assertEquals(Integer.MAX_VALUE, eviction().getMaximumSize());
        eviction().setMaximumSize(1000);
        assertEquals(1000, eviction().getMaximumSize());

        // start value
        c = newCache(newConf().eviction().setMaximumSize(5000));
        assertEquals(5000, eviction().getMaximumSize());
    }

    /**
     * Tests maximum capacity IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void maximumSizeIAE() {
        eviction().setMaximumSize(-1);
    }

    /**
     * Tests trimToSize.
     */
    @Test
    public void trimToSize() {
        put(5);
        assertSize(5);
        eviction().trimToSize(3);
        assertSize(3);
        put(10, 15);
        assertSize(9);
        eviction().trimToSize(1);
        assertSize(1);
    }

    /**
     * Tests trimToSize IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void trimToSizeIAE() {
        eviction().trimToSize(-1);
    }
    
    /**
     * Tests trimToVolume IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void trimToVolumeIAE() {
        eviction().trimToVolume(-1);
    }
}
