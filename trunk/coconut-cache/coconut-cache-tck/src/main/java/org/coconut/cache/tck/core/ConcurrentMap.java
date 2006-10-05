/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * Test the features of {@link java.util.concurrent.ConcurrentMap}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ConcurrentMap extends CacheTestBundle {

    /**
     * Converts a cache to a concurrentmap, right now the Cache interface
     * extends ConcurrentMap, however, this might change.
     * 
     * @return the cache as a concurrent map.
     */
    private static <K, V> java.util.concurrent.ConcurrentMap<K, V> asCm(
            Cache<K, V> cache) {
        // return (ConcurrentMap) cache;
        return cache;
    }

    /**
     * Tests the putIfAbsent(K key, V value) method.
     */
    @Test
    public void testPutIfAbsent() {
        assertNull(asCm(c0).putIfAbsent(M1.getKey(), M1.getValue()));
        assertEquals(M1.getValue(), asCm(c0).get(M1.getKey()));
        assertEquals(M1.getValue(), asCm(c0).putIfAbsent(M1.getKey(),
                M2.getValue()));
        assertFalse(asCm(c0).containsValue(M2.getValue()));
    }

    /**
     * Tests that putIfAbsent(null, Object) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testPutIfAbsentStringNPE() {
        asCm(c0).putIfAbsent(null, "A");
    }

    /**
     * Tests that putIfAbsent(Object, null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testPutIfAbsentIntegerNPE() {
        asCm(c0).putIfAbsent(1, null);
    }

    @Test
    public void testRemoveTwo() {
        assertTrue(asCm(c2).remove(M2.getKey(), M2.getValue()));
        assertEquals(1, asCm(c2).size());
        assertFalse(asCm(c2).remove(M1.getKey(), M2.getValue()));
        assertEquals(1, asCm(c2).size());
    }

    /**
     * remove(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testRemoveNullValue() {
        asCm(c1).remove(null, M1.getValue());
    }

    /**
     * remove(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testRemoveKeyNull() {
        asCm(c0).remove(M1.getKey(), null);
    }

    @Test
    public void testReplace2() {
        assertNull(asCm(c1).replace(M2.getKey(), M2.getValue()));
        assertEquals(M1.getValue(), asCm(c2)
                .replace(M1.getKey(), M3.getValue()));
        assertEquals(M3.getValue(), asCm(c2).get(M1.getKey()));
        assertNull(asCm(c2).replace(M4.getKey(), M4.getValue()));
        assertFalse(asCm(c2).containsValue(M4.getValue()));
        assertFalse(asCm(c2).containsValue(M1.getValue()));
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace2NullValue() {
        asCm(c1).replace(null, M2.getValue());
    }

    /**
     * replace(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace2KeyNull() {
        asCm(c1).replace(M1.getKey(), null);
    }

    @Test
    public void testReplace3() {
        assertTrue(asCm(c2).replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertEquals(M3.getValue(), asCm(c2).get(M1.getKey()));
        assertFalse(asCm(c2).replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertFalse(asCm(c2).containsValue(M1.getValue()));
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3NullValueValue() {
        asCm(c1).replace(null, M1.getValue(), M2.getValue());
    }

    /**
     * replace(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3KeyNullValue() {
        asCm(c1).replace(M1.getKey(), null, M2.getValue());
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3KeyValueNull() {
        asCm(c1).replace(M1.getKey(), M1.getValue(), null);
    }

}
