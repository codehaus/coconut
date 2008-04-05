/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

public class IntAttributeTest extends AtrStubs {
    static final IntAttribute LONG_A = new IntAttribute("foo", 100) {};

    static final IntAttribute NON_NEGATIVE = new IntAttribute("foo", 1) {
        @Override
        public boolean isValid(int value) {
            return value >= 0;
        }
    };

    @Test
    public void testDefault() {
        assertEquals(100, LONG_A.getDefault().intValue());
    }

    @Test
    public void checkValid() {
        LONG_A.checkValid(Integer.MIN_VALUE);
        LONG_A.checkValid(Integer.MAX_VALUE);
        LONG_A.checkValid(new Integer(Integer.MIN_VALUE));
        LONG_A.checkValid(new Integer(Integer.MIN_VALUE));

        NON_NEGATIVE.checkValid(Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        NON_NEGATIVE.checkValid(Integer.MIN_VALUE);
    }

    @Test
    public void fromString() {
        assertEquals(-1, LONG_A.fromString(Integer.valueOf(-1).toString()).intValue());
        assertEquals(Integer.MIN_VALUE, LONG_A.fromString(new Integer(Integer.MIN_VALUE).toString()).intValue());
        assertEquals(Integer.MAX_VALUE, LONG_A.fromString(new Integer(Integer.MAX_VALUE).toString()).intValue());
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(LONG_A, -1);
        AttributeMap am10000 = Attributes.singleton(LONG_A, 10000);
        AttributeMap ammax = Attributes.singleton(LONG_A, Integer.MAX_VALUE);

        assertEquals(100, LONG_A.getValue(am));
        assertEquals(-1, LONG_A.getValue(am1));
        assertEquals(10000, LONG_A.getValue(am10000));
        assertEquals(Integer.MAX_VALUE, LONG_A.getValue(ammax));

        assertEquals(10, LONG_A.getValue(am, 10));
        assertEquals(-1, LONG_A.getValue(am1, 10));
        assertEquals(10000, LONG_A.getValue(am10000, 10));
        assertEquals(Integer.MAX_VALUE, LONG_A.getValue(ammax, 10));

        assertEquals(-1, NON_NEGATIVE.getValue(am, -1));

        assertEquals(100, LONG_A.getValue(withAtr(am)));
        assertEquals(-1, LONG_A.getValue(withAtr(am1)));
        assertEquals(10, LONG_A.getValue(withAtr(am), 10));
        assertEquals(-1, LONG_A.getValue(withAtr(am1), 10));

    }

    @Test
    public void isValid() {
        assertTrue(LONG_A.isValid(Integer.MIN_VALUE));
        assertTrue(LONG_A.isValid(Integer.MAX_VALUE));
        assertTrue(LONG_A.isValid(Integer.valueOf(Integer.MIN_VALUE)));
        assertTrue(LONG_A.isValid(Integer.valueOf(Integer.MAX_VALUE)));

        assertTrue(NON_NEGATIVE.isValid(Integer.MAX_VALUE));
        assertFalse(NON_NEGATIVE.isValid(Integer.MIN_VALUE));
    }

    @Test
    public void set() {
        AttributeMap am = new DefaultAttributeMap();
        assertEquals(10, LONG_A.set(am, 10).get(LONG_A));
        assertEquals(-10000, LONG_A.set(withAtr(am), -10000).get(LONG_A));
        assertEquals(10000, LONG_A.set(am, Integer.valueOf(10000)).get(LONG_A));
        assertEquals(Integer.MAX_VALUE, LONG_A.set(am, Integer.MAX_VALUE).get(LONG_A));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        NON_NEGATIVE.set(new DefaultAttributeMap(), -1);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        LONG_A.set((AttributeMap) null, 1);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10, LONG_A.singleton(-10).get(LONG_A));
        assertEquals(10, LONG_A.singleton(10).get(LONG_A));
        assertEquals(Integer.MAX_VALUE, LONG_A.singleton(Integer.MAX_VALUE).get(LONG_A));

        assertEquals(10, NON_NEGATIVE.singleton(10).get(NON_NEGATIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        NON_NEGATIVE.singleton(-10);
    }

    @Test
    public void mapToLong() {
        TestUtil.assertIsSerializable(LONG_A.mapToInt());
        AttributeMap am = Attributes.singleton(LONG_A, 10000);
        assertEquals(10000, LONG_A.mapToInt().op(am));
        assertEquals(100, LONG_A.mapToInt().op(Attributes.EMPTY_ATTRIBUTE_MAP));
    }

    @Test(expected = NullPointerException.class)
    public void opNPE() {
        I_1.op(null);
    }

    @Test
    public void op() {
        assertEquals(5, I_1.op(withAtr(I_1.singleton(5))));
    }
    
    @Test
    public void comparator() {
        WithAttributes wa1 = withAtr(I_1.singleton(1));
        WithAttributes wa2 = withAtr(I_1.singleton(2));
        WithAttributes wa22 = withAtr(I_1.singleton(2));
        WithAttributes wa3 = withAtr(I_1.singleton(3));
        assertEquals(0, I_1.compare(wa2, wa2));
        assertEquals(0, I_1.compare(wa2, wa22));
        assertEquals(0, I_1.compare(wa22, wa2));
        assertTrue(I_1.compare(wa1, wa2) < 0);
        assertTrue(I_1.compare(wa2, wa1) > 0);
        assertTrue(I_1.compare(wa1, wa3) < 0);
        assertTrue(I_1.compare(wa3, wa2) > 0);
        assertTrue(I_1.compare(wa2, wa3) < 0);
    }
}
