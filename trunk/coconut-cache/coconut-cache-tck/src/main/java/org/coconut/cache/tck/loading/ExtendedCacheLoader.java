/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.tck.loading;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.util.CacheEntryLoader;
import org.coconut.cache.util.DefaultCacheEntry;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class ExtendedCacheLoader extends Loading {

    private CacheEntryLoader cel = new CacheEntryLoader();

    private Entry e1;

    private Entry e2;

    @Before
    public void setup() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.newConf();
        cc.backend().setExtendedLoader(cel);
        cc.setClock(clock);
        c = newCache(cc);
        e1 = new Entry(M1.getKey(), M1.getValue());
        e2 = new Entry(M2.getKey(), M2.getValue());
        cel.entries.put(e1.getKey(), e1);
        cel.entries.put(e2.getKey(), e2);
        cel.entries.put(M3.getKey(), new Entry(M3.getKey(), M3.getValue()));
        cel.entries.put(M4.getKey(), new Entry(M4.getKey(), M4.getValue()));
        cel.entries.put(M5.getKey(), new Entry(M5.getKey(), M5.getValue()));

        e1.cost = 10.1;
        e1.creationTime = 11;
        e1.expirationTime = 12;
        e1.hits = 13;
        e1.lastAccessTime = 14;
        e1.lastUpdateTime = 15;
        e1.size = 16;
        e1.version = 17;

        e2.cost = 20.1;
        e2.creationTime = 21;
        e2.expirationTime = 22;
        e2.hits = 23;
        e2.lastAccessTime = 24;
        e2.lastUpdateTime = 25;
        e2.size = 26;
        e2.version = 27;

    }

    @Test
    public void testGet() {
        clock.setAbsolutTime(200);
        assertGet(M1);
        assertEquals(10.1, getEntry(M1).getCost());
        assertEquals(11l, getEntry(M1).getCreationTime());
        assertEquals(12l, getEntry(M1).getExpirationTime());
        assertEquals(13l, getEntry(M1).getHits());
        // last access/update was at 200, because of get
        // override 14 and 15
        assertEquals(200l, getEntry(M1).getLastAccessTime());
        assertEquals(200l, getEntry(M1).getLastUpdateTime());
        assertEquals(16l, getEntry(M1).getSize());
        assertEquals(17l, getEntry(M1).getVersion());
    }

    @Test
    public void testGetAll() {
        clock.setAbsolutTime(200);
        assertGetAll(M1, M2);

        assertEquals(10.1, getEntry(M1).getCost());
        assertEquals(11l, getEntry(M1).getCreationTime());
        assertEquals(12l, getEntry(M1).getExpirationTime());
        assertEquals(13l, getEntry(M1).getHits());
        // last access/update was at 200, because of get
        // override 14 and 15
        assertEquals(200l, getEntry(M1).getLastAccessTime());
        assertEquals(200l, getEntry(M1).getLastUpdateTime());
        assertEquals(16l, getEntry(M1).getSize());
        assertEquals(17l, getEntry(M1).getVersion());

        assertEquals(20.1, getEntry(M2).getCost());
        assertEquals(21l, getEntry(M2).getCreationTime());
        assertEquals(22l, getEntry(M2).getExpirationTime());
        assertEquals(23l, getEntry(M2).getHits());
        // last access/update was at 200, because of get
        // override 14 and 15
        assertEquals(200l, getEntry(M2).getLastAccessTime());
        assertEquals(200l, getEntry(M2).getLastUpdateTime());
        assertEquals(26l, getEntry(M2).getSize());
        assertEquals(27l, getEntry(M2).getVersion());

    }

    @Test
    public void testLoad() throws InterruptedException, ExecutionException {
        clock.setAbsolutTime(200);
        c.loadAll(Arrays.asList(M1.getKey(), M2.getKey())).get();

        assertEquals(10.1, getEntry(M1).getCost());
        assertEquals(11l, getEntry(M1).getCreationTime());
        assertEquals(12l, getEntry(M1).getExpirationTime());
        assertEquals(13l, getEntry(M1).getHits());
        // notice that unlike get accesstime is not updated
        // because load does not count as an access
        assertEquals(14l, getEntry(M1).getLastAccessTime());
        // last update was at 200, because of load
        // override 15
        assertEquals(200l, getEntry(M1).getLastUpdateTime());
        assertEquals(16l, getEntry(M1).getSize());
        assertEquals(17l, getEntry(M1).getVersion());

        assertEquals(20.1, getEntry(M2).getCost());
        assertEquals(21l, getEntry(M2).getCreationTime());
        assertEquals(22l, getEntry(M2).getExpirationTime());
        assertEquals(23l, getEntry(M2).getHits());
        // notice that unlike get accesstime is not updated
        // because load does not count as an access
        assertEquals(24l, getEntry(M2).getLastAccessTime());
        // last update was at 200, because of load
        // override 25
        assertEquals(200l, getEntry(M2).getLastUpdateTime());
        assertEquals(26l, getEntry(M2).getSize());
        assertEquals(27l, getEntry(M2).getVersion());
    }

    @Test
    public void testLoadAll() throws InterruptedException, ExecutionException {
        clock.setAbsolutTime(200);
        c.load(M1.getKey()).get();
        assertEquals(10.1, getEntry(M1).getCost());
        assertEquals(11l, getEntry(M1).getCreationTime());
        assertEquals(12l, getEntry(M1).getExpirationTime());
        assertEquals(13l, getEntry(M1).getHits());
        // notice that unlike get accesstime is not updated
        // because load does not count as an access
        assertEquals(14l, getEntry(M1).getLastAccessTime());
        // last update was at 200, because of load
        // override 15
        assertEquals(200l, getEntry(M1).getLastUpdateTime());
        assertEquals(16l, getEntry(M1).getSize());
        assertEquals(17l, getEntry(M1).getVersion());
    }

    // also test getAll, loadAll

    @Test
    public void testSimpleLoading() {
        assertNullPeek(M1);
        assertFalse(containsKey(M1));
        assertFalse(containsValue(M1));
        assertSize(0);

        assertGet(M1);
        assertSize(1); // M1 loaded
        assertPeek(M1);
        assertTrue(containsKey(M1));
        assertTrue(containsValue(M1));
    }

    static class Entry extends DefaultCacheEntry<Integer, String> {

        double cost;

        long creationTime;

        long expirationTime;

        long hits;

        long lastAccessTime;

        long lastUpdateTime;

        long size;

        private long version;

        public Entry(Integer key, String value) {
            super(key, value);
        }

        @Override
        public double getCost() {
            return cost;
        }

        @Override
        public long getCreationTime() {
            return creationTime;
        }

        @Override
        public long getExpirationTime() {
            return expirationTime;
        }

        @Override
        public long getHits() {
            return hits;
        }

        @Override
        public Integer getKey() {
            return super.getKey();
        }

        @Override
        public long getLastAccessTime() {
            return lastAccessTime;
        }

        @Override
        public long getLastUpdateTime() {
            return lastUpdateTime;
        }

        public long getSize() {
            return size;
        }

        @Override
        public String getValue() {
            return super.getValue();
        }

        @Override
        public long getVersion() {
            return version;
        }

        @Override
        public String setValue(String value) {
            throw new UnsupportedOperationException();
        }

    }

}
