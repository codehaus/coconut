/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_TO_M5_SET;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M6;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Various String utils.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CollectionUtilsTest {

    @Test
    public void checkCollectionForNulls() {
        CollectionUtils.checkCollectionForNulls(Arrays.asList("foo", "ayy", "Foo"));
    }

    @Test(expected = NullPointerException.class)
    public void checkCollectionForNullsNPE() {
        CollectionUtils.checkCollectionForNulls(Arrays.asList("foo", null, "Foo"));
    }

    @Test
    public void checkMapForNulls() {
        Map m = new HashMap<String, String>();
        m.put("1", "3");
        m.put("foo", "boo");
        CollectionUtils.checkMapForNulls(m);
    }

    @Test(expected = NullPointerException.class)
    public void checkMapForNullsKeyNPE() {
        Map m = new HashMap<String, String>();
        m.put("1", "3");
        m.put(null, "boo");
        CollectionUtils.checkMapForNulls(m);
    }

    @Test(expected = NullPointerException.class)
    public void checkMapForNullsValueNPE() {
        Map m = new HashMap<String, String>();
        m.put("1", "3");
        m.put("ggg", null);
        CollectionUtils.checkMapForNulls(m);
    }

    @Test
    public void eq() {
        assertTrue(CollectionUtils.eq(null, null));
        assertFalse(CollectionUtils.eq(1, null));
        assertFalse(CollectionUtils.eq(null, 1));
        assertTrue(CollectionUtils.eq(1, 1));
    }

    @Test
    public void simpleImmutableEntry() {

        Map.Entry<Integer, Integer> me = new CollectionUtils.SimpleImmutableEntry(0, 1);
        assertEquals(0, me.getKey().intValue());
        assertEquals(me.getKey(), new CollectionUtils.SimpleImmutableEntry(me).getKey());
        assertEquals(1, me.getValue().intValue());
        assertEquals(me.getValue(), new CollectionUtils.SimpleImmutableEntry(me).getValue());
        assertEquals("0=1", me.toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void simpleImmutableEntrySetValueUOE() {
        new CollectionUtils.SimpleImmutableEntry(0, 1).setValue(2);
    }

    @Test
    public void simpleImmutableEntryHashcode() {
        assertEquals(0, new CollectionUtils.SimpleImmutableEntry(null, null).hashCode());
        assertEquals(100 ^ 200, new CollectionUtils.SimpleImmutableEntry(100, 200).hashCode());
    }

    @Test
    public void simpleImmutableEntryEquals() {
        Map.Entry me = new CollectionUtils.SimpleImmutableEntry(0, 1);
        assertFalse(me.equals(null));
        assertFalse(me.equals(new Object()));
        assertTrue(me.equals(me));
        assertFalse(me.equals(new CollectionUtils.SimpleImmutableEntry(0, 0)));
        assertFalse(me.equals(new CollectionUtils.SimpleImmutableEntry(0, null)));
        assertFalse(me.equals(new CollectionUtils.SimpleImmutableEntry(1, 1)));
        assertFalse(me.equals(new CollectionUtils.SimpleImmutableEntry(null, 1)));
        assertTrue(me.equals(new CollectionUtils.SimpleImmutableEntry(0, 1)));
    }

    @Test
    public void immutableSet() {
        Set<Integer> col = new HashSet(Arrays.asList(1, 2, 3, 4));
        Set<Integer> i = new CollectionUtils.ImmutableSet(col);
        assertEquals(col, i);
        assertEquals(i, col);
        assertEquals(i, i);
        assertEquals(col.hashCode(), i.hashCode());
    }

    @Test
    public void immutableEntrySet() {
        Set<Map.Entry<Integer, String>> i = new CollectionUtils.ImmutableEntrySet(M1_TO_M5_SET);
        assertFalse(i.contains(1));
        assertTrue(i.contains(M1));
        assertFalse(i.containsAll(Arrays.asList(M1, M6)));
        assertTrue(i.containsAll(Arrays.asList(M1, M2)));
        assertEquals(i, i);
        assertFalse(i.equals(new HashSet()));
        assertFalse(i.equals(1));
        assertTrue(i.equals(M1_TO_M5_SET));
        assertEquals(i, new HashSet(i));// invoke iterator

        assertEquals(new HashSet(Arrays.asList(i.toArray())), new HashSet(Arrays
                .asList(M1_TO_M5_SET.toArray())));

        assertEquals(new HashSet(Arrays.asList(i.toArray(new Map.Entry[0]))), new HashSet(Arrays
                .asList(M1_TO_M5_SET.toArray(new Map.Entry[0]))));
        assertEquals(new HashSet(Arrays.asList(i.toArray(new Map.Entry[1]))), new HashSet(Arrays
                .asList(M1_TO_M5_SET.toArray(new Map.Entry[1]))));
        assertEquals(new HashSet(Arrays.asList(i.toArray(new Map.Entry[5]))), new HashSet(Arrays
                .asList(M1_TO_M5_SET.toArray(new Map.Entry[5]))));
        assertEquals(new HashSet(Arrays.asList(i.toArray(new Map.Entry[6]))), new HashSet(Arrays
                .asList(M1_TO_M5_SET.toArray(new Map.Entry[6]))));

        try {
            Iterator<Map.Entry<Integer, String>> iter = i.iterator();
            iter.next();
            iter.remove();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
    }

    @Test
    public void immutableCollection() {
        Collection<Integer> col = new ArrayList(Arrays.asList(1, 2, 3, 4));
        Collection<Integer> i = new CollectionUtils.ImmutableCollection(col);

        assertTrue(i.contains(4));
        assertFalse(i.contains(5));
        assertTrue(i.containsAll(Arrays.asList(2, 3, 4)));
        assertFalse(i.containsAll(Arrays.asList(2, 3, 5)));
        assertFalse(i.isEmpty());
        assertTrue(new CollectionUtils.ImmutableCollection(new HashSet()).isEmpty());
        assertEquals(col.size(), i.size());

        assertEquals(col.toArray(), i.toArray());
        assertEquals(col.toArray(new Integer[3]), i.toArray(new Integer[3]));
        assertEquals(col.toString(), i.toString());
        assertEquals(col.toString(), i.toString());
        try {
            i.add(2);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            i.addAll(Arrays.asList(1, 2));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            i.clear();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            i.remove(2);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            i.removeAll(Arrays.asList(1, 2));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            i.retainAll(Arrays.asList(1, 2));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
        try {
            Iterator<Integer> iter = i.iterator();
            iter.next();
            iter.remove();
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}
    }

    @Test(expected = NullPointerException.class)
    public void immutableSetNPE() {
        new CollectionUtils.ImmutableSet(null);
    }

    @Test(expected = NullPointerException.class)
    public void immutableCollectionNPE() {
        new CollectionUtils.ImmutableCollection(null);
    }
}
