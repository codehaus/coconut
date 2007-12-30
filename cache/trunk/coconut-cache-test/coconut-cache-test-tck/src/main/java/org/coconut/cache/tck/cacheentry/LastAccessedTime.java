/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Test;

public class LastAccessedTime extends AbstractCacheTCKTest {

    @Test
    public void get() {
        clock.setTimestamp(10);
        put(2);
        assertEquals(0l, peekEntry(M1).getLastAccessTime());
        assertEquals(0l, peekEntry(M2).getLastAccessTime());

        get(M1);
        assertEquals(10l, peekEntry(M1).getLastAccessTime());
        assertEquals(0l, peekEntry(M2).getLastAccessTime());

        get(M1);
        clock.incrementTimestamp();
        getAll(M1, M2);
        assertEquals(11l, peekEntry(M1).getLastAccessTime());
        assertEquals(11l, peekEntry(M2).getLastAccessTime());
    }
    
    @Test
    public void getEntry() {
        clock.setTimestamp(10);
        put(2);

        getEntry(M1);
        assertEquals(10l, peekEntry(M1).getLastAccessTime());
        assertEquals(0l, peekEntry(M2).getLastAccessTime());

        clock.incrementTimestamp();
        getEntry(M1);
        getEntry(M2);
        getEntry(M1);
        assertEquals(11l, peekEntry(M1).getLastAccessTime());
        assertEquals(11l, peekEntry(M2).getLastAccessTime());
    }
    
    @Test
    public void loadedNoAttributes() {
        clock.setTimestamp(10);
        c = newCache(newConf().loading().setLoader(new IntegerToStringLoader()));

        loadAndAwait(M1);
        assertEquals(0l, peekEntry(M1).getLastAccessTime());
        get(M1);
        assertEquals(10l, peekEntry(M1).getLastAccessTime());
    }

}
