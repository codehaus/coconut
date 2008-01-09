/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import org.coconut.cache.service.memorystore.MemoryStoreConfiguration;
import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests EvictionService.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EvictionService extends AbstractCacheTCKTest {
    static MemoryStoreConfiguration<?, ?> DEFAULT = new MemoryStoreConfiguration();

    @Before
    public void setup() {
        init();
    }

    @Test
    public void testServiceAvailable() {
        assertNotNull(c.getService(MemoryStoreService.class));
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

}
