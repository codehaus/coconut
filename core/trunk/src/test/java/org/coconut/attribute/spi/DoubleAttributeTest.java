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

public class DoubleAttributeTest {
    static final AbstractDoubleAttribute LA = new AbstractDoubleAttribute("foo", 100.5d) {};

    @Test
    public void testDefault() {
        assertEquals(100.5D, LA.getDefaultValue());
    }

    @Test
    public void checkValid() {
        LA.checkValid(Double.MIN_VALUE);
        LA.checkValid(Double.MAX_VALUE);
        LA.checkValid(new Double(Double.MIN_VALUE));
        LA.checkValid(new Double(Double.MIN_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        LA.checkValid(Double.NEGATIVE_INFINITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE1() {
        LA.checkValid(Double.POSITIVE_INFINITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE2() {
        LA.checkValid(Double.NaN);
    }

    @Test
    public void isValid() {
        assertTrue(LA.isValid(Double.MIN_VALUE));
        assertTrue(LA.isValid(Double.MAX_VALUE));
        assertTrue(LA.isValid(new Double(Double.MIN_VALUE)));
        assertTrue(LA.isValid(new Double(Double.MAX_VALUE)));
        assertFalse(LA.isValid(Double.NEGATIVE_INFINITY));
        assertFalse(LA.isValid(Double.POSITIVE_INFINITY));
        assertFalse(LA.isValid(Double.NaN));
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_MAP;
        AttributeMap am1 = Attributes.singleton(LA, -1D);
        AttributeMap am10000 = Attributes.singleton(LA, 10000D);
        AttributeMap ammax = Attributes.singleton(LA, Double.MAX_VALUE);
        AttributeMap amINf = Attributes.singleton(LA, Double.POSITIVE_INFINITY);

        assertEquals(100.5, LA.getPrimitive(am));
        assertEquals(-1D, LA.getPrimitive(am1));
        assertEquals(10000D, LA.getPrimitive(am10000));
        assertEquals(Double.MAX_VALUE, LA.getPrimitive(ammax));
        assertEquals(Double.POSITIVE_INFINITY, LA.getPrimitive(amINf));

        assertEquals(10D, LA.getPrimitive(am, 10D));
        assertEquals(-1D, LA.getPrimitive(am1, 10));
        assertEquals(10000D, LA.getPrimitive(am10000, 10));
        assertEquals(Double.MAX_VALUE, LA.getPrimitive(ammax, 10));
        assertEquals(Double.POSITIVE_INFINITY, LA.getPrimitive(amINf, 10));

    }

    @Test
    public void set() {
        AttributeMap am = new DefaultAttributeMap();
        assertEquals(10.5, LA.setAttribute(am, 10.5D).get(LA));
        assertEquals(10000d, LA.setValue(am, new Double(10000)).get(LA));
        assertEquals(Double.MAX_VALUE, LA.setAttribute(am, Double.MAX_VALUE).get(LA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        LA.setAttribute(new DefaultAttributeMap(), Double.NaN);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        LA.setAttribute(null, 1L);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10.0, LA.toSingletonLong(-10).get(LA));
        assertEquals(10.0, LA.toSingletonLong(10).get(LA));
        assertEquals(Double.MAX_VALUE, LA.toSingletonLong(Double.MAX_VALUE).get(LA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        LA.toSingletonLong(Double.NaN);
    }

    @Test
    public void fromString() {
        assertEquals(-1D, LA.fromString(new Double(-1).toString()));
        assertEquals(Double.MIN_VALUE, LA.fromString(new Double(Double.MIN_VALUE).toString()));
        assertEquals(Double.MAX_VALUE, LA.fromString(new Double(Double.MAX_VALUE).toString()));
        assertEquals(Double.POSITIVE_INFINITY, LA.fromString(new Double(Double.POSITIVE_INFINITY)
                .toString()));
        assertEquals(Double.NEGATIVE_INFINITY, LA.fromString(new Double(Double.NEGATIVE_INFINITY)
                .toString()));
        assertEquals(Double.NaN, LA.fromString(new Double(Double.NaN).toString()));
    }

    @Test
    public void mapToLong() {
        TestUtil.assertIsSerializable(LA.mapToDouble());
        AttributeMap am = Attributes.singleton(LA, 100.1);
        assertEquals(100.1, LA.mapToDouble().map(am));
        assertEquals(100.5, LA.mapToDouble().map(Attributes.EMPTY_MAP));
    }

    @Test(expected = NullPointerException.class)
    public void mapper() {
        LA.mapToDouble().map(null);
    }
}
