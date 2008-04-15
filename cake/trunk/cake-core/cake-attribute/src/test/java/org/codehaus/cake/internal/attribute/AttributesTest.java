package org.codehaus.cake.internal.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
public class AttributesTest {

    @Test
    public void simpleImmutableEntry() {
    
        Map.Entry<Integer, Integer> me = new AttributeHelper.SimpleImmutableEntry(0, 1);
        assertEquals(0, me.getKey().intValue());
        assertEquals(me.getKey(), new AttributeHelper.SimpleImmutableEntry(me).getKey());
        assertEquals(1, me.getValue().intValue());
        assertEquals(me.getValue(), new AttributeHelper.SimpleImmutableEntry(me).getValue());
        assertEquals("0=1", me.toString());
    }

    @Test
    public void simpleImmutableEntryEquals() {
        Map.Entry me = new AttributeHelper.SimpleImmutableEntry(0, 1);
        assertFalse(me.equals(null));
        assertFalse(me.equals(new Object()));
        assertTrue(me.equals(me));
        assertFalse(me.equals(new AttributeHelper.SimpleImmutableEntry(0, 0)));
        assertFalse(me.equals(new AttributeHelper.SimpleImmutableEntry(0, null)));
        assertFalse(me.equals(new AttributeHelper.SimpleImmutableEntry(1, 1)));
        assertFalse(me.equals(new AttributeHelper.SimpleImmutableEntry(null, 1)));
        assertTrue(me.equals(new AttributeHelper.SimpleImmutableEntry(0, 1)));
    }

    @Test
    public void simpleImmutableEntryHashcode() {
        assertEquals(0, new AttributeHelper.SimpleImmutableEntry(null, null).hashCode());
        assertEquals(100 ^ 200, new AttributeHelper.SimpleImmutableEntry(100, 200).hashCode());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void simpleImmutableEntrySetValueUOE() {
        new AttributeHelper.SimpleImmutableEntry(0, 1).setValue(2);
    }

    
}
