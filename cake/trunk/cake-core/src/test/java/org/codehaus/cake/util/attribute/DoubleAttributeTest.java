/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

public class DoubleAttributeTest {
    static final DoubleAttribute LA = new DoubleAttribute("foo", 100.5d) {};

    @Test
    public void testDefault() {
        assertEquals(100.5D, LA.getDefault().doubleValue(),0);
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
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(LA, -1D);
        AttributeMap am10000 = Attributes.singleton(LA, 10000D);
        AttributeMap ammax = Attributes.singleton(LA, Double.MAX_VALUE);
        AttributeMap amINf = Attributes.singleton(LA, Double.POSITIVE_INFINITY);

        assertEquals(100.5, LA.getValue(am),0);
        assertEquals(-1D, LA.getValue(am1),0);
        assertEquals(10000D, LA.getValue(am10000),0);
        assertEquals(Double.MAX_VALUE, LA.getValue(ammax),0);
        assertEquals(Double.POSITIVE_INFINITY, LA.getValue(amINf),0);

        assertEquals(10D, LA.getValue(am, 10D),0);
        assertEquals(-1D, LA.getValue(am1, 10),0);
        assertEquals(10000D, LA.getValue(am10000, 10),0);
        assertEquals(Double.MAX_VALUE, LA.getValue(ammax, 10),0);
        assertEquals(Double.POSITIVE_INFINITY, LA.getValue(amINf, 10),0);

    }

    @Test
    public void set() {
        AttributeMap am = new DefaultAttributeMap();
        assertEquals(10.5, LA.set(am, 10.5D).get(LA));
        assertEquals(10000d, LA.set(am, new Double(10000)).get(LA));
        assertEquals(Double.MAX_VALUE, LA.set(am, Double.MAX_VALUE).get(LA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        LA.set(new DefaultAttributeMap(), Double.NaN);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        LA.set(null, 1L);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10.0, LA.singleton(-10).get(LA));
        assertEquals(10.0, LA.singleton(10).get(LA));
        assertEquals(Double.MAX_VALUE, LA.singleton(Double.MAX_VALUE).get(LA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        LA.singleton(Double.NaN);
    }

    @Test
    public void fromString() {
        assertEquals(-1D, LA.fromString(new Double(-1).toString()),0);
        assertEquals(Double.MIN_VALUE, LA.fromString(new Double(Double.MIN_VALUE).toString()),0);
        assertEquals(Double.MAX_VALUE, LA.fromString(new Double(Double.MAX_VALUE).toString()),0);
        assertEquals(Double.POSITIVE_INFINITY, LA.fromString(new Double(Double.POSITIVE_INFINITY)
                .toString()),0);
        assertEquals(Double.NEGATIVE_INFINITY, LA.fromString(new Double(Double.NEGATIVE_INFINITY)
                .toString()),0);
        assertEquals(Double.NaN, LA.fromString(new Double(Double.NaN).toString()),0);
    }

    @Test
    public void mapToLong() {
        TestUtil.assertIsSerializable(LA.mapToDouble());
        AttributeMap am = Attributes.singleton(LA, 100.1);
        assertEquals(100.1, LA.mapToDouble().op(am),0);
        assertEquals(100.5, LA.mapToDouble().op(Attributes.EMPTY_ATTRIBUTE_MAP),0);
    }

    @Test(expected = NullPointerException.class)
    public void mapper() {
        LA.mapToDouble().op(null);
    }
}
