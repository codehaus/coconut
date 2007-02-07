/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;

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
     * Tests the putIfAbsent(K key, V value) method.
     */
    @Test
    public void testPutIfAbsent() {
        assertNull(c0.putIfAbsent(M1.getKey(), M1.getValue()));
        assertEquals(M1.getValue(), c0.get(M1.getKey()));
        assertEquals(M1.getValue(), c0.putIfAbsent(M1.getKey(),
                M2.getValue()));
        assertFalse(c0.containsValue(M2.getValue()));
    }

    /**
     * Tests that putIfAbsent(null, Object) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testPutIfAbsentStringNPE() {
        c0.putIfAbsent(null, "A");
    }

    /**
     * Tests that putIfAbsent(Object, null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testPutIfAbsentIntegerNPE() {
        c0.putIfAbsent(1, null);
    }

    @Test
    public void testRemoveTwo() {
        assertTrue(c2.remove(M2.getKey(), M2.getValue()));
        assertEquals(1, c2.size());
        assertFalse(c2.remove(M1.getKey(), M2.getValue()));
        assertEquals(1, c2.size());
    }

    /**
     * remove(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testRemoveNullValue() {
        c1.remove(null, M1.getValue());
    }

    /**
     * remove(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testRemoveKeyNull() {
        c0.remove(M1.getKey(), null);
    }

    @Test
    public void testReplace2() {
        assertNull(c1.replace(M2.getKey(), M2.getValue()));
        assertEquals(M1.getValue(), c2
                .replace(M1.getKey(), M3.getValue()));
        assertEquals(M3.getValue(), c2.get(M1.getKey()));
        
        assertNull(c2.replace(M4.getKey(), M4.getValue()));
        assertFalse(c2.containsValue(M4.getValue()));
        assertFalse(c2.containsValue(M1.getValue()));
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace2NullValue() {
        c1.replace(null, M2.getValue());
    }

    /**
     * replace(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace2KeyNull() {
        c1.replace(M1.getKey(), null);
    }

    @Test
    public void testReplace3() {
        assertTrue(c2.replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertEquals(M3.getValue(), c2.get(M1.getKey()));
        assertFalse(c2.replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertFalse(c2.containsValue(M1.getValue()));
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3NullValueValue() {
        c1.replace(null, M1.getValue(), M2.getValue());
    }

    /**
     * replace(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3KeyNullValue() {
        c1.replace(M1.getKey(), null, M2.getValue());
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3KeyValueNull() {
        c1.replace(M1.getKey(), M1.getValue(), null);
    }

}
