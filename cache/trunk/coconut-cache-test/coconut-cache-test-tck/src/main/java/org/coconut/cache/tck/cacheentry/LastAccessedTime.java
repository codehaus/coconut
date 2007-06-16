package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

public class LastAccessedTime  extends AbstractCacheTCKTestBundle{

    @Test
    public void dummyTest() {
        
    }
    
    public void testAccessedTime() {
        c = newCache(newConf().setClock(clock));
        clock.setTimestamp(10);
        put(M1);
        assertEquals(0l, getEntry(M1).getLastAccessTime());

        get(M1);
        assertEquals(10l, getEntry(M1).getLastAccessTime());
        clock.incrementTimestamp();

        peek(M1); // peek does not update accessTime
        assertEquals(10l, getEntry(M1).getLastAccessTime());
        clock.incrementTimestamp();

        getAll(M1, M2);
        assertEquals(12l, getEntry(M1).getLastAccessTime());
    }
}
