package org.coconut.cache.tck.service.eviction;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests EvictionService.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EvictionService extends AbstractCacheTCKTestBundle {
    static CacheEvictionConfiguration<?, ?> DEFAULT = new CacheEvictionConfiguration();

    @Before
    public void setup() {
        c = newCache();
    }

    /**
     * Tests maximum capacity.
     */
    @Test
    public void maximumCapacity() {
        assertEquals(DEFAULT.getMaximumCapacity(), eviction().getMaximumCapacity());
        eviction().setMaximumCapacity(1000);
        assertEquals(1000, eviction().getMaximumCapacity());

        // start value
        c = newCache(newConf().eviction().setMaximumCapacity(5000));
        assertEquals(5000, eviction().getMaximumCapacity());
    }

    /**
     * Tests maximum capacity IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void maximumCapacityIAE() {
        eviction().setMaximumCapacity(-1);
    }

    /**
     * Tests maximum capacity.
     */
    @Test
    public void maximumSize() {
        assertEquals(DEFAULT.getMaximumSize(), eviction().getMaximumSize());
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
}
