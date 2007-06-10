/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;

import java.util.Map;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

/**
 * Test the features of {@link java.util.concurrent.ConcurrentMap}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ConcurrentMap extends AbstractCacheTCKTestBundle {

    /**
     * This method is used because we are not to sure about whether or not cache should
     * extend {@link java.util.concurrent.ConcurrentMap} or just {@link java.util.Map}.
     */
    java.util.concurrent.ConcurrentMap<Integer, String> c() {
        return (java.util.concurrent.ConcurrentMap) c;
    }

    /**
     * Tests the putIfAbsent(K key, V value) method.
     */
    @Test
    public void testPutIfAbsent() {
        c = newCache();
        assertNull(c().putIfAbsent(M1.getKey(), M1.getValue()));
        assertEquals(M1.getValue(), c().get(M1.getKey()));
        assertEquals(M1.getValue(), c().putIfAbsent(M1.getKey(), M2.getValue()));
        assertFalse(c.containsValue(M2.getValue()));
    }

    /**
     * Tests that putIfAbsent(null, Object) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testPutIfAbsentStringNPE() {
        c = newCache(0);
        c().putIfAbsent(null, "A");
    }

    /**
     * Tests that putIfAbsent(Object, null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testPutIfAbsentIntegerNPE() {
        c = newCache();
        c().putIfAbsent(1, null);
    }

    @Test
    public void testRemoveTwo() {
        c = newCache(2);
        assertTrue(c().remove(M2.getKey(), M2.getValue()));
        assertEquals(1, c.size());
        assertFalse(c().remove(M1.getKey(), M2.getValue()));
        assertEquals(1, c.size());
    }

    /**
     * remove(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testRemoveNullValue() {
        c = newCache(1);
        c().remove(null, M1.getValue());
    }

    /**
     * remove(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testRemoveKeyNull() {
        c = newCache();
        c().remove(M1.getKey(), null);
    }

    @Test
    public void testReplace2() {
        c = newCache(1);
        assertNull(c().replace(M2.getKey(), M2.getValue()));
        c = newCache(2);
        assertEquals(M1.getValue(), c().replace(M1.getKey(), M3.getValue()));
        assertEquals(M3.getValue(), c.get(M1.getKey()));
        assertNull(c().replace(M4.getKey(), M4.getValue()));
        assertFalse(c.containsValue(M4.getValue()));
        assertFalse(c.containsValue(M1.getValue()));
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace2NullValue() {
        c = newCache(1);
        c().replace(null, M2.getValue());
    }

    /**
     * replace(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace2KeyNull() {
        c = newCache(1);
        c().replace(M1.getKey(), null);
    }

    @Test
    public void testReplace3() {
        c = newCache(2);
        assertTrue(c().replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertEquals(M3.getValue(), c.get(M1.getKey()));
        assertFalse(c().replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertFalse(c.containsValue(M1.getValue()));
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3NullValueValue() {
        c = newCache(1);
        c().replace(null, M1.getValue(), M2.getValue());
    }

    /**
     * replace(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3KeyNullValue() {
        c = newCache(1);
        c().replace(M1.getKey(), null, M2.getValue());
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3KeyValueNull() {
        c = newCache(1);
        c().replace(M1.getKey(), M1.getValue(), null);
    }

}