package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

public class LastModifiedTime extends AbstractCacheTCKTestBundle {
    @Test
    public void dummyTest() {

    }
    

    @Test
    public void testLastUpdateTime() {
        c = newCache(newConf().setClock(clock));
        clock.setTimestamp(10);
        put(M1);
        assertEquals(10l, getEntry(M1).getLastUpdateTime());
        clock.setTimestamp(20);
        put(M1);
        assertEquals(20l, getEntry(M1).getLastUpdateTime());
        clock.setTimestamp(30);
        putAll(M1, M2);
        assertEquals(30l, getEntry(M1).getLastUpdateTime());
        assertEquals(30l, getEntry(M1).getLastUpdateTime());
    }
}
