/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEntryBundle extends CacheTestBundle {

    @Test
    public void testCacheEntry() {
        assertNull(c0.getEntry(M1.getKey()));

        CacheEntry<Integer, String> ce = c1.getEntry(M1.getKey());
        assertEquals(M1.getKey(), ce.getKey());
        assertEquals(M1.getValue(), ce.getValue());
    }

    /**
     * Tests that timestamp is set for creation date. Furthermore test that
     * creation date is not updated when replacing existing values.
     */
    @Test
    public void testCreationDate() {
        c = newCache(newConf().setClock(clock));
        clock.setAbsolutTime(10);
        put(M1);
        assertEquals(10l, getEntry(M1).getCreationTime());
        clock.incrementAbsolutTime();
        put(M1);
        assertEquals(10l, getEntry(M1).getCreationTime());
        clock.incrementAbsolutTime();
        c.put(M1.getKey(), M2.getValue());
        assertEquals(10l, getEntry(M1).getCreationTime());

        // Test for putAll
        c = newCache(newConf().setClock(clock));
        clock.setAbsolutTime(10);
        putAll(M1, M2);
        assertEquals(10l, getEntry(M1).getCreationTime());
        assertEquals(10l, getEntry(M2).getCreationTime());
        clock.incrementAbsolutTime();
        putAll(M1, M2);
        assertEquals(10l, getEntry(M1).getCreationTime());
        assertEquals(10l, getEntry(M2).getCreationTime());
    }

    /**
     * Tests that timestamp is set for creation date when loading values
     */
    @Test
    public void testCreationDateFromLoader() {
        c = newCache(newConf().setClock(clock).backend().setBackend(DEFAULT_LOADER).c());
        clock.setAbsolutTime(10);
        get(M1);
        assertEquals(10l, getEntry(M1).getCreationTime());
    }

    @Test
    public void testExpirationDate() {
        c = newCache(newConf().setClock(clock));
        clock.setRelativeTime(10);
        put(M1, 5);
        assertEquals(15l, getEntry(M1).getExpirationTime());
        put(M1, 10);
        assertEquals(20l, getEntry(M1).getExpirationTime());
    }

    @Test
    public void testLastUpdateTime() {
        c = newCache(newConf().setClock(clock));
        clock.setAbsolutTime(10);
        put(M1);
        assertEquals(10l, getEntry(M1).getLastUpdateTime());
        clock.setAbsolutTime(20);
        put(M1);
        assertEquals(20l, getEntry(M1).getLastUpdateTime());
        clock.setAbsolutTime(30);
        putAll(M1, M2);
        assertEquals(30l, getEntry(M1).getLastUpdateTime());
        assertEquals(30l, getEntry(M1).getLastUpdateTime());
    }

    @Test
    public void testLastUpdateFromLoader() {
        c = newCache(newConf().setClock(clock).backend().setBackend(DEFAULT_LOADER).c());
        clock.setAbsolutTime(10);
        get(M1);
        assertEquals(10l, getEntry(M1).getLastUpdateTime());
    }

    @Test
    public void testAccessedTime() {
        c = newCache(newConf().setClock(clock));
        clock.setAbsolutTime(10);
        put(M1);
        assertEquals(0l, getEntry(M1).getLastAccessTime());

        get(M1);
        assertEquals(10l, getEntry(M1).getLastAccessTime());
        clock.incrementAbsolutTime();

        peek(M1); // peek does not update accessTime
        assertEquals(10l, getEntry(M1).getLastAccessTime());
        clock.incrementAbsolutTime();

        getAll(M1, M2);
        assertEquals(12l, getEntry(M1).getLastAccessTime());
    }

    @Test
    public void testAccessFromLoader() {
        c = newCache(newConf().setClock(clock).backend().setBackend(DEFAULT_LOADER).c());
        clock.setAbsolutTime(10);
        get(M1);
        assertEquals(10l, getEntry(M1).getLastAccessTime());

        //tests that a never accessed entry returns 0
        c.loadAsync(M2.getKey());
        assertEquals(0l, getEntry(M2).getLastAccessTime());
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
        c = c1;
        CacheEntry<Integer, String> ce = getEntry(M1);
        assertEquals(0l, ce.getHits());

        get(M1);
        assertEquals(1l, ce.getHits());

        getAll(M1, M2);
        assertEquals(2l, ce.getHits());

        peek(M1);
        assertEquals(2l, ce.getHits());
    }

    /**
     * When a put for a given key occures with the same value as the existing
     * entry the version of the entry is updated even though there are no
     * difference. This is done so we can avoid running invoking equals on the 2
     * elements. We could implement a simple identity based check. However, I
     * think that people would expect either no check or a check based on
     * equals, definitly not a check based on identity.
     */
    @Test
    public void testIncrementVersion() {
        c = c1;
        CacheEntry<Integer, String> ce = getEntry(M1);
        assertEquals(1l, ce.getVersion());

        get(M1);
        ce = getEntry(M1);
        assertEquals(1l, ce.getVersion());

        put(M1); // its a new version even though
        ce = getEntry(M1);
        assertEquals(2l, ce.getVersion());

        c.put(M1.getKey(), M2.getValue());
        ce = getEntry(M1);
        assertEquals(3l, ce.getVersion());

        putAll(M1, M2);
        ce = getEntry(M1);
        assertEquals(4l, ce.getVersion());
    }
}
