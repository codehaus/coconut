/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.attribute.DefaultAttributeMap;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class IntAttributeTest {
    static final AbstractIntAttribute LONG_A = new AbstractIntAttribute("foo", 100) {};

    static final AbstractIntAttribute NON_NEGATIVE = new AbstractIntAttribute("foo", 1) {
        @Override
        public boolean isValid(int value) {
            return value >= 0;
        }
    };

    @Test
    public void testDefault() {
        assertEquals(100, LONG_A.getDefaultValue());
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
        assertEquals(-1, LONG_A.fromString(Integer.valueOf(-1).toString()));
        assertEquals(Integer.MIN_VALUE, LONG_A.fromString(new Integer(Integer.MIN_VALUE).toString()));
        assertEquals(Integer.MAX_VALUE, LONG_A.fromString(new Integer(Integer.MAX_VALUE).toString()));
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(LONG_A, -1);
        AttributeMap am10000 = Attributes.singleton(LONG_A, 10000);
        AttributeMap ammax = Attributes.singleton(LONG_A, Integer.MAX_VALUE);

        assertEquals(100, LONG_A.getPrimitive(am));
        assertEquals(-1, LONG_A.getPrimitive(am1));
        assertEquals(10000, LONG_A.getPrimitive(am10000));
        assertEquals(Integer.MAX_VALUE, LONG_A.getPrimitive(ammax));

        assertEquals(10, LONG_A.getPrimitive(am, 10));
        assertEquals(-1, LONG_A.getPrimitive(am1, 10));
        assertEquals(10000, LONG_A.getPrimitive(am10000, 10));
        assertEquals(Integer.MAX_VALUE, LONG_A.getPrimitive(ammax, 10));

        assertEquals(-1, NON_NEGATIVE.getPrimitive(am, -1));
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
        assertEquals(10, LONG_A.setAttribute(am, 10).get(LONG_A));
        assertEquals(-10000, LONG_A.setAttribute(am, -10000).get(LONG_A));
        assertEquals(10000, LONG_A.setValue(am, Integer.valueOf(10000)).get(LONG_A));
        assertEquals(Integer.MAX_VALUE, LONG_A.setAttribute(am, Integer.MAX_VALUE).get(LONG_A));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        NON_NEGATIVE.setAttribute(new DefaultAttributeMap(), -1);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        LONG_A.setAttribute(null, 1);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10, LONG_A.toSingletonInt(-10).get(LONG_A));
        assertEquals(10, LONG_A.toSingletonInt(10).get(LONG_A));
        assertEquals(Integer.MAX_VALUE, LONG_A.toSingletonInt(Integer.MAX_VALUE).get(LONG_A));

        assertEquals(10, NON_NEGATIVE.toSingletonInt(10).get(NON_NEGATIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        NON_NEGATIVE.toSingletonInt(-10);
    }

    @Test
    public void mapToLong() {
        TestUtil.assertIsSerializable(LONG_A.mapToInt());
        AttributeMap am = Attributes.singleton(LONG_A, 10000);
        assertEquals(10000, LONG_A.mapToInt().map(am));
        assertEquals(100, LONG_A.mapToInt().map(Attributes.EMPTY_ATTRIBUTE_MAP));
    }

    @Test(expected = NullPointerException.class)
    public void mapper() {
        LONG_A.mapToInt().map(null);
    }
}
