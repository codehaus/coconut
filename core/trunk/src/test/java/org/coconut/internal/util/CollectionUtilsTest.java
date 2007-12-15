/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
}
