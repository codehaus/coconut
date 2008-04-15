/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AttributeMaps_SingletonMapTest extends AbstractAttributeMapTest {

    private final static Attribute KEY = new ObjectAttribute("key", Integer.class, 5) {};

    private final static Object VALUE = "10";

    private final static Attribute OTHER_KEY = new ObjectAttribute("key", Integer.class, 6) {};

    private final static Object OTHER_VALUE = 10;
    private final AttributeMap singleton_null = Attributes.singleton(KEY, null);

    @Before
    public void setup() {
        map = Attributes.singleton(KEY, VALUE);

    }

    @Test(expected = NullPointerException.class)
    public void singletonNPE() {
        Attributes.singleton(null, VALUE);
    }

    @Test
    public void various() {
        super.assertImmutable();
    }

    @Test
    public void contains() {
        assertTrue(map.contains(KEY));
        // assertTrue(singleton.containsValue(VALUE));

        assertFalse(map.contains(OTHER_KEY));
        // assertFalse(singleton.containsValue(OTHER_VALUE));

        // assertEquals(1, singleton.entrySet().size());
        assertEquals(1, map.attributeSet().size());
        // assertEquals(1, singleton.values().size());
        assertEquals(1, map.size());
        assertFalse(map.isEmpty());
    }

    @Test
    public void hashcode() {
        assertEquals(map.hashCode(), map.hashCode());
        assertEquals(singleton_null.hashCode(), singleton_null.hashCode());
    }

    @Test
    public void get() {

    }

    @Test
    public void bool() {
        AttributeMap b1 = Attributes.singleton(B_TRUE, true);
        assertFalse(b1.get(B_FALSE));
        assertTrue(b1.get(B_TRUE));
        assertFalse(b1.get(B_FALSE, false));
        assertTrue(b1.get(B_TRUE, false));
        assertTrue(b1.get(B_FALSE, true));
        assertTrue(b1.get(B_TRUE, true));

        AttributeMap b2 = Attributes.singleton(B_TRUE, false);
        assertFalse(b2.get(B_FALSE));
        assertFalse(b2.get(B_TRUE));
        assertFalse(b2.get(B_FALSE, false));
        assertFalse(b2.get(B_TRUE, false));
        assertTrue(b2.get(B_FALSE, true));
        assertFalse(b2.get(B_TRUE, true));

    }

    @Test
    public void bytes() {
        AttributeMap b1 = Attributes.singleton(B_1, (byte) 5);
        assertEquals((byte) 5, b1.get(B_1));
        assertEquals((byte) 2, b1.get(B_2));
        assertEquals((byte) 5, b1.get(B_1, (byte) 3));
        assertEquals((byte) 3, b1.get(B_2, (byte) 3));
        assertEquals((byte) 5, b1.get(B_1, (byte) 5));
        assertEquals((byte) 5, b1.get(B_2, (byte) 5));
        assertEquals((byte) 5, b1.get(B_1, (byte) 1));
        assertEquals((byte) 2, b1.get(B_2, (byte) 2));

        AttributeMap b2 = Attributes.singleton(B_1, (byte) 1);
        assertEquals((byte) 1, b2.get(B_1));
        assertEquals((byte) 2, b2.get(B_2));
        assertEquals((byte) 1, b2.get(B_1, (byte) 3));
        assertEquals((byte) 3, b2.get(B_2, (byte) 3));
        assertEquals((byte) 1, b2.get(B_1, (byte) 5));
        assertEquals((byte) 5, b2.get(B_2, (byte) 5));
        assertEquals((byte) 1, b2.get(B_1, (byte) 1));
        assertEquals((byte) 2, b2.get(B_2, (byte) 2));
    }

    @Test
    public void charts() {
        AttributeMap b1 = Attributes.singleton(C_1, (char) 5);
        assertEquals((char) 5, b1.get(C_1));
        assertEquals((char) 2, b1.get(C_2));
        assertEquals((char) 5, b1.get(C_1, (char) 3));
        assertEquals((char) 3, b1.get(C_2, (char) 3));
        assertEquals((char) 5, b1.get(C_1, (char) 5));
        assertEquals((char) 5, b1.get(C_2, (char) 5));
        assertEquals((char) 5, b1.get(C_1, (char) 1));
        assertEquals((char) 2, b1.get(C_2, (char) 2));

        AttributeMap b2 = Attributes.singleton(C_1, (char) 1);
        assertEquals((char) 1, b2.get(C_1));
        assertEquals((char) 2, b2.get(C_2));
        assertEquals((char) 1, b2.get(C_1, (char) 3));
        assertEquals((char) 3, b2.get(C_2, (char) 3));
        assertEquals((char) 1, b2.get(C_1, (char) 5));
        assertEquals((char) 5, b2.get(C_2, (char) 5));
        assertEquals((char) 1, b2.get(C_1, (char) 1));
        assertEquals((char) 2, b2.get(C_2, (char) 2));
    }

    @Test
    public void doubles() {
        AttributeMap b1 = Attributes.singleton(D_1, (double) 5.5);
        assertEquals((double) 5.5, b1.get(D_1), 0);
        assertEquals((double) 2.5, b1.get(D_2), 0);
        assertEquals((double) 5.5, b1.get(D_1, (double) 3), 0);
        assertEquals((double) 3, b1.get(D_2, (double) 3), 0);
        assertEquals((double) 5.5, b1.get(D_1, (double) 5.5), 0);
        assertEquals((double) 5.5, b1.get(D_2, (double) 5.5), 0);
        assertEquals((double) 5.5, b1.get(D_1, (double) 1.5), 0);
        assertEquals((double) 2.5, b1.get(D_2, (double) 2.5), 0);

        AttributeMap b2 = Attributes.singleton(D_1, (double) 1.5);
        assertEquals((double) 1.5, b2.get(D_1), 0);
        assertEquals((double) 2.5, b2.get(D_2), 0);
        assertEquals((double) 1.5, b2.get(D_1, (double) 3), 0);
        assertEquals((double) 3, b2.get(D_2, (double) 3), 0);
        assertEquals((double) 1.5, b2.get(D_1, (double) 5.5), 0);
        assertEquals((double) 5.5, b2.get(D_2, (double) 5.5), 0);
        assertEquals((double) 1.5, b2.get(D_1, (double) 1.5), 0);
        assertEquals((double) 2.5, b2.get(D_2, (double) 2.5), 0);
    }

    @Test
    public void floats() {
        AttributeMap b1 = Attributes.singleton(F_1, (float) 5.5);
        assertEquals((float) 5.5, b1.get(F_1), 0);
        assertEquals((float) 2.5, b1.get(F_2), 0);
        assertEquals((float) 5.5, b1.get(F_1, (float) 3), 0);
        assertEquals((float) 3, b1.get(F_2, (float) 3), 0);
        assertEquals((float) 5.5, b1.get(F_1, (float) 5.5), 0);
        assertEquals((float) 5.5, b1.get(F_2, (float) 5.5), 0);
        assertEquals((float) 5.5, b1.get(F_1, (float) 1.5), 0);
        assertEquals((float) 2.5, b1.get(F_2, (float) 2.5), 0);

        AttributeMap b2 = Attributes.singleton(F_1, (float) 1.5);
        assertEquals((float) 1.5, b2.get(F_1), 0);
        assertEquals((float) 2.5, b2.get(F_2), 0);
        assertEquals((float) 1.5, b2.get(F_1, (float) 3), 0);
        assertEquals((float) 3, b2.get(F_2, (float) 3), 0);
        assertEquals((float) 1.5, b2.get(F_1, (float) 5.5), 0);
        assertEquals((float) 5.5, b2.get(F_2, (float) 5.5), 0);
        assertEquals((float) 1.5, b2.get(F_1, (float) 1.5), 0);
        assertEquals((float) 2.5, b2.get(F_2, (float) 2.5), 0);
    }

    @Test
    public void ints() {
        AttributeMap b1 = Attributes.singleton(I_1, (int) 5);
        assertEquals((int) 5, b1.get(I_1));
        assertEquals((int) 2, b1.get(I_2));
        assertEquals((int) 5, b1.get(I_1, (int) 3));
        assertEquals((int) 3, b1.get(I_2, (int) 3));
        assertEquals((int) 5, b1.get(I_1, (int) 5));
        assertEquals((int) 5, b1.get(I_2, (int) 5));
        assertEquals((int) 5, b1.get(I_1, (int) 1));
        assertEquals((int) 2, b1.get(I_2, (int) 2));

        AttributeMap b2 = Attributes.singleton(I_1, (int) 1);
        assertEquals((int) 1, b2.get(I_1));
        assertEquals((int) 2, b2.get(I_2));
        assertEquals((int) 1, b2.get(I_1, (int) 3));
        assertEquals((int) 3, b2.get(I_2, (int) 3));
        assertEquals((int) 1, b2.get(I_1, (int) 5));
        assertEquals((int) 5, b2.get(I_2, (int) 5));
        assertEquals((int) 1, b2.get(I_1, (int) 1));
        assertEquals((int) 2, b2.get(I_2, (int) 2));
    }

    @Test
    public void longs() {
        AttributeMap b1 = Attributes.singleton(L_1, (long) 5);
        assertEquals((long) 5, b1.get(L_1));
        assertEquals((long) 2, b1.get(L_2));
        assertEquals((long) 5, b1.get(L_1, (long) 3));
        assertEquals((long) 3, b1.get(L_2, (long) 3));
        assertEquals((long) 5, b1.get(L_1, (long) 5));
        assertEquals((long) 5, b1.get(L_2, (long) 5));
        assertEquals((long) 5, b1.get(L_1, (long) 1));
        assertEquals((long) 2, b1.get(L_2, (long) 2));

        AttributeMap b2 = Attributes.singleton(L_1, (long) 1);
        assertEquals((long) 1, b2.get(L_1));
        assertEquals((long) 2, b2.get(L_2));
        assertEquals((long) 1, b2.get(L_1, (long) 3));
        assertEquals((long) 3, b2.get(L_2, (long) 3));
        assertEquals((long) 1, b2.get(L_1, (long) 5));
        assertEquals((long) 5, b2.get(L_2, (long) 5));
        assertEquals((long) 1, b2.get(L_1, (long) 1));
        assertEquals((long) 2, b2.get(L_2, (long) 2));
    }

    @Test
    public void shorts() {
        AttributeMap b1 = Attributes.singleton(S_1, (short) 5);
        assertEquals((short) 5, b1.get(S_1));
        assertEquals((short) 2, b1.get(S_2));
        assertEquals((short) 5, b1.get(S_1, (short) 3));
        assertEquals((short) 3, b1.get(S_2, (short) 3));
        assertEquals((short) 5, b1.get(S_1, (short) 5));
        assertEquals((short) 5, b1.get(S_2, (short) 5));
        assertEquals((short) 5, b1.get(S_1, (short) 1));
        assertEquals((short) 2, b1.get(S_2, (short) 2));

        AttributeMap b2 = Attributes.singleton(S_1, (short) 1);
        assertEquals((short) 1, b2.get(S_1));
        assertEquals((short) 2, b2.get(S_2));
        assertEquals((short) 1, b2.get(S_1, (short) 3));
        assertEquals((short) 3, b2.get(S_2, (short) 3));
        assertEquals((short) 1, b2.get(S_1, (short) 5));
        assertEquals((short) 5, b2.get(S_2, (short) 5));
        assertEquals((short) 1, b2.get(S_1, (short) 1));
        assertEquals((short) 2, b2.get(S_2, (short) 2));
    }

    // TODO long, short
    @Test
    @Ignore
    public void getNull() {
        assertEquals(VALUE, map.get(KEY));
        assertEquals(VALUE, map.get(KEY, OTHER_VALUE));
        assertNull(map.get(OTHER_KEY));
        assertEquals(OTHER_VALUE, map.get(OTHER_KEY, OTHER_VALUE));

        assertNull(singleton_null.get(KEY));
        assertNull(singleton_null.get(OTHER_KEY));
        assertNull(singleton_null.get(KEY, VALUE));
        assertEquals(VALUE, singleton_null.get(OTHER_KEY, VALUE));
    }

    @Test
    public void entrySet() {
        assertEquals(1, map.entrySet().size());
        assertEquals(Collections.singletonMap(KEY, VALUE).entrySet(),map.entrySet());
        assertEquals(Collections.singletonMap(KEY, VALUE).values(),map.values());
    }

    @Test
    public void equals() {
        assertFalse(map.equals(null));
        assertFalse(map.equals(new Object()));
        assertFalse(map.equals(Attributes.singleton(OTHER_KEY, VALUE)));
        assertFalse(map.equals(Attributes.singleton(KEY, OTHER_VALUE)));
        assertFalse(map.equals(Attributes.singleton(OTHER_KEY, null)));
        assertFalse(map.equals(Attributes.singleton(KEY, null)));
        assertFalse(map.equals(Attributes.singleton(OTHER_KEY, OTHER_VALUE)));
        assertFalse(singleton_null.equals(Attributes.singleton(KEY, VALUE)));
        assertFalse(singleton_null.equals(Attributes.singleton(OTHER_KEY, VALUE)));

        // classcast exception
        Map m = new HashMap() {
            @Override
            public Object get(Object key) {
                String k = (String) key;
                return super.get(key);
            }
        };
        m.put(KEY, VALUE);
        assertFalse(map.equals(m));

        // differen sizes
        AttributeMap am = new DefaultAttributeMap();
        am.put(KEY, VALUE);
        am.put(OTHER_KEY, VALUE);
        assertFalse(map.equals(am));

        assertTrue(map.equals(map));
        assertTrue(map.equals(Attributes.singleton(KEY, VALUE)));
        assertTrue(singleton_null.equals(Attributes.singleton(KEY, null)));
    }

    @Test
    public void toString_() {
        assertEquals("{key=10}", map.toString());
    }

    /**
     * Tests that singleton map is serializable
     * 
     * @throws Exception
     *             something went wrong
     */
    @Test
    public void serialization() throws Exception {
        assertIsSerializable(Attributes.singleton(KEY, 123));
    }
}
