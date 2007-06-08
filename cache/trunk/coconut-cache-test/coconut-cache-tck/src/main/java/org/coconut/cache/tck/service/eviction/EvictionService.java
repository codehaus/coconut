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
     * Tests default idle time.
     */
    @Test
    public void defaultIdleTime() {
        assertEquals(DEFAULT.getDefaultIdleTime(TimeUnit.MILLISECONDS), eviction()
                .getDefaultIdleTime(TimeUnit.MILLISECONDS));

        assertEquals(DEFAULT.getDefaultIdleTime(TimeUnit.SECONDS), eviction()
                .getDefaultIdleTime(TimeUnit.SECONDS));

        eviction().setDefaultIdleTime(1000, TimeUnit.MILLISECONDS);
        assertEquals(1000, eviction().getDefaultIdleTime(TimeUnit.MILLISECONDS));
        assertEquals(1000 * 1000, eviction().getDefaultIdleTime(TimeUnit.MICROSECONDS));

        eviction().setDefaultIdleTime(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        assertEquals(Long.MAX_VALUE, eviction().getDefaultIdleTime(TimeUnit.NANOSECONDS));

        eviction().setDefaultIdleTime(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        assertEquals(Long.MAX_VALUE, eviction().getDefaultIdleTime(TimeUnit.SECONDS));

        // start value
        c = newCache(newConf().eviction().setDefaultIdleTime(1800, TimeUnit.SECONDS));
        assertEquals(1800 * 1000, eviction().getDefaultIdleTime(TimeUnit.MILLISECONDS));
    }

    /**
     * Tests maximum capacity IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void defaultIdleIAE() {
        eviction().setDefaultIdleTime(-1, TimeUnit.NANOSECONDS);
    }

    /**
     * Tests maximum capacity IllegalArgumentException.
     */
    @Test(expected = NullPointerException.class)
    public void defaultIdleNPE() {
        eviction().setDefaultIdleTime(1, null);
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
