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

}
