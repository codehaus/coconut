/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DefaultAttributeMapTest extends AtrStubs {

    ObjectAttribute a1 = new ObjectAttribute("a1", Integer.class, 6) {};

    ObjectAttribute a2 = new ObjectAttribute("a2", Integer.class, 7) {};

    ObjectAttribute a3 = new ObjectAttribute("a3", Integer.class, 8) {};

    ObjectAttribute a4 = new ObjectAttribute("a3", Boolean.class, true) {};

    AttributeMap m;

    AttributeMap m2;

    @Test
    public void clear() {
        assertEquals(2, m2.size());
        m2.clear();
        assertEquals(0, m2.size());
        m2.clear();
        assertEquals(0, m2.size());
    }

    @Test
    public void contains() {
        assertTrue(m2.contains(a1));
        assertTrue(m2.contains(a2));
        assertFalse(m2.contains(a3));
    }

    @Test
    public void copyConstructor() {
        AttributeMap am = create();
        am.put(a1, 12.23);
        am.put(a2, 12);
        assertEquals(am, new DefaultAttributeMap(am));
    }

    protected AttributeMap create() {
        return new DefaultAttributeMap();
    }

    @Test
    public void equals() {
        assertEquals(m2, m2);
        assertFalse(m2.equals(new Object()));
        assertFalse(m2.equals(new DefaultAttributeMap()));
        assertEquals(m2, new DefaultAttributeMap(m2));
    }

    void mappedPutted() {

    }

    @Test
    public void putGet() {
        m.put(a1, "foo");
        m.put(a2, true);

        mappedPutted();
        assertEquals("foo", m.get(a1));
        assertEquals(Boolean.TRUE, m.get(a2));
        assertEquals("foo", m.get(a1, "boo"));
        assertEquals(Boolean.TRUE, m.get(a2, Boolean.FALSE));
        assertEquals(8, m.get(a3));
        assertEquals(true, m.get(a3, true));
        assertEquals("true", m.get(a3, "true"));

        assertEquals("15", m.get(O_2));
        assertEquals(null, m.get(O_3));
        assertEquals("15", m.put(O_2, null));
        assertEquals(null, m.put(O_3, null));
        assertEquals(null, m.put(O_2, "15"));
        assertEquals(null, m.put(O_3, "15"));
        assertEquals("15", m.put(O_2, "25"));
        assertEquals("15", m.put(O_3, "25"));

    }

    @Test
    public void putGetBoolean() {
        BooleanAttribute a1 = new BooleanAttribute("a1", true) {};
        BooleanAttribute a2 = new BooleanAttribute("a2", true) {};

        assertTrue(m.put(a1, false));
        assertFalse(m.put(a1, false));
        m.put(a2, Boolean.TRUE);
        mappedPutted();
        assertEquals(Boolean.FALSE, m.get(a1));
        assertEquals(Boolean.TRUE, m.get(a2));
        assertEquals(false, m.get(a1));
        assertEquals(true, m.get(a2));
        assertEquals(true, m.get(new BooleanAttribute("", false) {}, true));
        assertEquals(false, m.get(a1, true));
        assertEquals(true, m.get(a2, false));

        assertEquals(false, m.get(new BooleanAttribute("", false) {}));
        assertEquals(true, m.get(new BooleanAttribute("", true) {}));
        assertEquals(false, m.get(new BooleanAttribute("", false) {}));
        assertEquals(true, m.get(new BooleanAttribute("", true) {}));
    }

    @Test
    public void putGetByte() {
        assertEquals((byte) 1, m.put(B_1, (byte) 1));
        assertEquals((byte) 1, m.put(B_1, (byte) 2));
        assertTrue(m.contains(B_1));
        assertEquals((byte) 2, m.put(B_1, (byte) 3));
        assertEquals((byte) 2, m.put(B_2, (byte) 4));
        mappedPutted();
        assertEquals((byte) 3, m.get(B_1));
        assertEquals((byte) 4, m.get(B_2));
        assertEquals((byte) 3, m.get(B_1));
        assertEquals((byte) 3, m.get(B_1, (byte) 3));
        assertEquals((byte) 4, m.get(B_2));
        assertEquals((byte) 4, m.get(B_2, (byte) 4));
        assertEquals((byte) 3, m.get(B_3));
        assertEquals((byte) 5, m.get(B_3, (byte) 5));
    }

    @Test
    public void putGetChar() {
        assertEquals((char) 1, m.put(C_1, (char) 1));
        assertEquals((char) 1, m.put(C_1, (char) 2));
        assertTrue(m.contains(C_1));
        assertEquals((char) 2, m.put(C_1, (char) 3));
        assertEquals((char) 2, m.put(C_2, (char) 4));
        mappedPutted();
        assertEquals((char) 3, m.get(C_1));
        assertEquals((char) 4, m.get(C_2));
        assertEquals((char) 3, m.get(C_1));
        assertEquals((char) 3, m.get(C_1, (char) 3));
        assertEquals((char) 4, m.get(C_2));
        assertEquals((char) 4, m.get(C_2, (char) 4));
        assertEquals((char) 3, m.get(C_3));
        assertEquals((char) 5, m.get(C_3, (char) 5));
    }

    @Test
    public void putGetDouble() {
        assertEquals(1.5, m.put(D_1, 1.5), 0);
        assertEquals(1.5, m.put(D_1, 2.5), 0);
        assertTrue(m.contains(D_1));
        assertEquals(2.5, m.put(D_1, 3.5), 0);
        assertEquals(2.5d, (Double) m.put(D_2, 4.5), 0);
        mappedPutted();
        assertEquals(3.5d, (Double) m.get(D_1), 0);
        assertEquals(4.5d, (Double) m.get(D_2), 0);
        assertEquals(3.5, m.get(D_1), 0);
        assertEquals(3.5, m.get(D_1, 3.5), 0);
        assertEquals(4.5, m.get(D_2), 0);
        assertEquals(4.5, m.get(D_2, 4.5), 0);
        assertEquals(3.5, m.get(D_3), 0);
        assertEquals(5.5, m.get(D_3, 5.5), 0);
    }

    @Test
    public void putGetFloat() {
        assertEquals(1.5f, m.put(F_1, 1.5f), 0);
        assertEquals(1.5f, m.put(F_1, 2.5f), 0);
        assertTrue(m.contains(F_1));
        assertEquals(2.5f, m.put(F_1, 3.5f), 0);
        assertEquals(2.5f, (Float) m.put(F_2, 4.5f), 0);
        mappedPutted();
        assertEquals(3.5f, (Float) m.get(F_1), 0);
        assertEquals(4.5f, (Float) m.get(F_2), 0);
        assertEquals(3.5f, m.get(F_1), 0);
        assertEquals(3.5f, m.get(F_1, 3.5f), 0);
        assertEquals(4.5f, m.get(F_2), 0);
        assertEquals(4.5f, m.get(F_2, 4.5f), 0);
        assertEquals(3.5f, m.get(F_3), 0);
        assertEquals(5.5f, m.get(F_3, 5.5f), 0);
    }

    @Test
    public void putGetInt() {
        assertEquals(1, m.put(I_1, 1));
        assertEquals(1, m.put(I_1, 2));
        assertTrue(m.contains(I_1));
        assertEquals(2, m.put(I_1, 3));
        assertEquals(2, m.put(I_2, 4));
        mappedPutted();
        assertEquals(3, m.get(I_1));
        assertEquals(4, m.get(I_2));
        assertEquals(3, m.get(I_1));
        assertEquals(3, m.get(I_1, 3));
        assertEquals(4, m.get(I_2));
        assertEquals(4, m.get(I_2, 4));
        assertEquals(3, m.get(I_3));
        assertEquals(5, m.get(I_3, 5));
    }

    @Test
    public void putGetLong() {
        assertEquals(1, m.put(L_1, 1));
        assertEquals(1, m.put(L_1, 2));
        assertTrue(m.contains(L_1));
        assertEquals(2, m.put(L_1, 3));
        assertEquals(2L, m.put(L_2, 4L));
        mappedPutted();
        assertEquals(3L, m.get(L_1));
        assertEquals(4L, m.get(L_2));
        assertEquals(3, m.get(L_1));
        assertEquals(3, m.get(L_1, 3));
        assertEquals(4, m.get(L_2));
        assertEquals(4, m.get(L_2, 4));
        assertEquals(3, m.get(L_3));
        assertEquals(5, m.get(L_3, 5));
    }

    @Test
    public void putGetShort() {
        assertEquals((short) 1, m.put(S_1, (short) 1));
        assertEquals((short) 1, m.put(S_1, (short) 2));
        assertTrue(m.contains(S_1));
        assertEquals((short) 2, m.put(S_1, (short) 3));
        assertEquals((short) 2, m.put(S_2, (short) 4));
        mappedPutted();
        assertEquals((short) 3, m.get(S_1));
        assertEquals((short) 4, m.get(S_2));
        assertEquals((short) 3, m.get(S_1));
        assertEquals((short) 3, m.get(S_1, (short) 3));
        assertEquals((short) 4, m.get(S_2));
        assertEquals((short) 4, m.get(S_2, (short) 4));
        assertEquals((short) 3, m.get(S_3));
        assertEquals((short) 5, m.get(S_3, (short) 5));
    }

    @Test
    public void remove() {
        AttributeMap map = new DefaultAttributeMap();
        assertEquals(B_TRUE.getDefaultValue(), map.remove(B_TRUE));
        assertEquals(B_1.getDefaultValue(), map.remove(B_1));
        assertEquals(C_1.getDefaultValue(), map.remove(C_1));
        assertEquals(D_1.getDefaultValue(), map.remove(D_1), 0);
        assertEquals(F_1.getDefaultValue(), map.remove(F_1), 0);
        assertEquals(I_1.getDefaultValue(), map.remove(I_1));
        assertEquals(L_1.getDefaultValue(), map.remove(L_1));
        assertEquals(O_1.getDefaultValue(), map.remove(O_1));
        assertEquals(S_1.getDefaultValue(), map.remove(S_1));

        map.put(B_TRUE, false);
        map.put(B_1, (byte) 5);
        map.put(C_1, (char) 5);
        map.put(D_1, 5);
        map.put(F_1, 5);
        map.put(I_1, 5);
        map.put(L_1, 5);
        map.put(O_1, "5");
        map.put(O_2, null);
        map.put(S_1, (short) 5);
        assertFalse(map.remove(B_TRUE));
        assertEquals((byte) 5, map.remove(B_1));
        assertEquals((char) 5, map.remove(C_1));
        assertEquals(5D, map.remove(D_1), 0);
        assertEquals(5F, map.remove(F_1), 0);
        assertEquals(5, map.remove(I_1));
        assertEquals(5L, map.remove(L_1));
        assertEquals("5", map.remove(O_1));
        assertEquals(null, map.remove(O_2));

        assertEquals((short) 5, map.remove(S_1));

    }

    @Before
    public void setup() {
        m = create();
        m2 = create();
        m2.put(a1, 12.23);
        m2.put(a2, 12);
    }
}
