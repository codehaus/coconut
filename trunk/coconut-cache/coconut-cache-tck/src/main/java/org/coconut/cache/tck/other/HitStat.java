/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.other;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * This class contains test the statistics gathered by a cache. A number of the
 * tests checks that the statistics gathered by a cache is not affected by other
 * methods then get() and getAll(). As a consequence working with the entryset,
 * valueset or keyset of a cache does not influence the statistics gathered by
 * the cache.
 * <p>
 * If the cache does not support keep any kind of cache statistics
 * {@link NoHitStat} can be used instead.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class HitStat extends CacheTestBundle {

    /**
     * Tests that the initial hit stat is 0 misses, 0 hits and -1 for the ratio.
     */
    @Test
    public void initialHitStat() {
        assertHitstat(Float.NaN, 0, 0, c0.getHitStat());
        assertHitstat(Float.NaN, 0, 0, c1.getHitStat());
    }

    /**
     * Tests of a simple hit on the cache and a simple miss on the cache.
     */
    @Test
    public void getHitStat() {
        c = c1;

        get(M2);
        assertHitstat(0, 0, 1, c.getHitStat());

        get(M1);
        assertHitstat(0.5f, 1, 1, c.getHitStat());
    }

    /**
     * Tests that peek does not influence the cache statistics of the cache.
     */
    @Test
    public void peekHitstat() {
        c = c1;
        assertNotNull(peek(M1)); // hit
        assertNull(peek(M2)); // miss
        assertHitstat(Float.NaN, 0, 0, c.getHitStat());
    }

    /**
     * Tests that reset hitstat works.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void resetHitstat() {
        c = c1;
        assertGet(M1); // hit
        assertNullGet(M2); // miss
        assertHitstat(0.5f, 1, 1, c.getHitStat());
        c.resetStatistics();
        assertHitstat(Float.NaN, 0, 0, c.getHitStat());
    }

    /**
     * Tests that getAll affects the hit statistics of a cache.
     */
    @Test
    public void getAllHitStat() {
        c0.getAll(asList(0, 1, 2, 3));
        assertHitstat(0, 0, 4, c0.getHitStat());

        c2.getAll(asList(0, 1, 2, 3));
        assertHitstat(0.50f, 2, 2, c2.getHitStat());

        c4.getAll(asList(1, 2, 3, 4));
        assertHitstat(1, 4, 0, c4.getHitStat());
    }

    /**
     * Tests that remove works properly for hit statistics.
     */
    @Test
    public void removeItem() {
        c = c1;
        get(M1);
        remove(M1);
        assertHitstat(1f, 1, 0, c.getHitStat());
    }

    /**
     * Tests that various methods does not affect the number of hits/misses.
     */
    @Test
    public void variousMethods() {
        c=c4;
        c.containsKey(M3.getKey());
        c.containsKey(M5.getKey());

        c.containsValue(M3.getValue());
        c.containsValue(M5.getValue());

        c.equals(c);

        c.hashCode();
        c.isEmpty();

        c.size();
        c.toString();
        assertHitstat(Float.NaN, 0, 0, c4.getHitStat());
    }

    /**
     * Test that the use of a caches valueset does not affect the cache
     * statistics gathered.
     */
    @Test
    public void testValues() {
        Collection<String> values = c4.values();
        values.contains(M1.getValue());
        values.contains(M5.getValue());
        values.equals(values);
        values.hashCode();
        values.isEmpty();
        for (Iterator<String> iter = values.iterator(); iter.hasNext();) {
            iter.next();
        }
        values.remove(M4.getValue());
        values.removeAll(Arrays.asList(M4.getValue(), M3.getValue()));
        values.retainAll(Arrays.asList(M2.getValue(), M3.getValue()));
        values.size();
        values.toArray();
        values.toArray(new String[4]);
        values.toString();
        assertHitstat(Float.NaN, 0, 0, c4.getHitStat());
    }

    /**
     * Test that the use of a caches keyset does not affect the cache statistics
     * gathered.
     */
    @Test
    public void testKeySet() {
        Set<Integer> keys = c4.keySet();
        keys.contains(M1.getKey());
        keys.contains(M5.getKey());
        keys.equals(keys);
        keys.isEmpty();
        keys.hashCode();
        for (Iterator<Integer> iter = keys.iterator(); iter.hasNext();) {
            iter.next();
        }
        keys.remove(M4.getKey());
        keys.removeAll(Arrays.asList(M4.getKey(), M3.getKey()));
        keys.retainAll(Arrays.asList(M2.getKey(), M3.getKey()));
        keys.size();
        keys.toArray();
        keys.toArray(new Integer[4]);
        keys.toString();
        assertHitstat(Float.NaN, 0, 0, c4.getHitStat());
    }

    /**
     * Test that the use of a caches entryset does not affect the cache
     * statistics gathered.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testEntrySet() {
        Set<Map.Entry<Integer, String>> entrySet = c4.entrySet();
        entrySet.contains(M1);
        entrySet.contains(M5);
        entrySet.equals(entrySet);
        entrySet.isEmpty();
        for (Iterator<Map.Entry<Integer, String>> iter = entrySet.iterator(); iter
                .hasNext();) {
            iter.next();
        }
        entrySet.remove(M4);
        entrySet.removeAll(Arrays.asList(M4, M3));
        entrySet.retainAll(Arrays.asList(M2, M3));
        entrySet.size();
        entrySet.toArray();
        entrySet.toArray(new Map.Entry[4]);
        entrySet.toString();
        assertHitstat(Float.NaN, 0, 0, c4.getHitStat());
    }

    /**
     * Tests that loading of elements does not does not affect the cache
     * statistics gathered.
     * 
     * @throws Exception
     *             test could not complete properly
     */
    @Test
    public void load() throws Exception {
        Future<?> f = loadableEmptyCache.loadAsync(M5.getKey());
        Future<?> fAll = loadableEmptyCache.loadAllAsync(Arrays.asList(M2.getKey(),
                M4.getKey()));
        try {
            f.get();
            fAll.get();
        } catch (UnsupportedOperationException ignore) {
            // cache does not support loading, ignore
        }
        assertHitstat(Float.NaN, 0, 0, loadableEmptyCache.getHitStat());
    }

    @Test
    public void replace() {
        // TODO test replace methods        
    }

}
