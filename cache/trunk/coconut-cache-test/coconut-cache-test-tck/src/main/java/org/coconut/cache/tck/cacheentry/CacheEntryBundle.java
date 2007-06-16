/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheEntryBundle.java 344 2007-06-10 11:09:38Z kasper $
 */
public class CacheEntryBundle extends AbstractCacheTCKTestBundle {

    @Test
    public void testCacheEntry() {
        c=newCache(0);
        assertNull(c.getEntry(M1.getKey()));
        c=newCache(1);
        CacheEntry<Integer, String> ce = c.getEntry(M1.getKey());
        assertEquals(M1.getKey(), ce.getKey());
        assertEquals(M1.getValue(), ce.getValue());
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


    @Test
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

    /**
     * Tests that a cache does not attempt to call any configured cache backends
     * if a value does not already exists in the cache. The reason is that if it
     * did that it should also effect the cache statistics (which would most
     * likely surprise people) gathered by the cache. However I don't think most
     * people will use getEntry for that purpose. It would also be nessessary to
     * send out and Accessed event. Perhaps we can make it configurable
     */
    public void testNoBackendConsulting() {

    }

    @Test
    public void testHits() {
        c = newCache(1);
        CacheEntry<Integer, String> ce = getEntry(M1);
        assertEquals(0l, ce.getHits());

        get(M1);
        assertEquals(1l, ce.getHits());

        getAll(M1, M2);
        assertEquals(2l, ce.getHits());

        peek(M1);
        assertEquals(2l, ce.getHits());
    }

//    /**
//     * When a put for a given key occures with the same value as the existing
//     * entry the version of the entry is updated even though there are no
//     * difference. This is done so we can avoid running invoking equals on the 2
//     * elements. We could implement a simple identity based check. However, I
//     * think that people would expect either no check or a check based on
//     * equals, definitly not a check based on identity.
//     */
//    @Test
//    public void testIncrementVersion() {
//        c = c1;
//        CacheEntry<Integer, String> ce = getEntry(M1);
//        assertEquals(1l, ce.getVersion());
//
//        get(M1);
//        ce = getEntry(M1);
//        assertEquals(1l, ce.getVersion());
//
//        put(M1); // its a new version even though
//        ce = getEntry(M1);
//        assertEquals(2l, ce.getVersion());
//
//        c.put(M1.getKey(), M2.getValue());
//        ce = getEntry(M1);
//        assertEquals(3l, ce.getVersion());
//
//        putAll(M1, M2);
//        ce = getEntry(M1);
//        assertEquals(4l, ce.getVersion());
//    }
}
