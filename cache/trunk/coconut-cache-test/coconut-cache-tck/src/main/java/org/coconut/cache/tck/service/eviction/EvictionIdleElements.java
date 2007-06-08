package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionUtils.M1;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

public class EvictionIdleElements extends AbstractCacheTCKTestBundle {

    @Before
    public void setup() {
        c = newCache();
    }

    @Test
    public void testIdle() {
        setCache(newConf().eviction().setDefaultIdleTime(10, TimeUnit.MILLISECONDS));
        put(1);
        assertGet(M1);
        incTime(5);
        eviction().evictIdleElements();
    }
}
