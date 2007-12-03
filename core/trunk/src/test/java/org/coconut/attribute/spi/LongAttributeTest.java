package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.junit.Test;

public class LongAttributeTest {
    static final AbstractLongAttribute LA = new AbstractLongAttribute("foo", 100) {};

    static final AbstractLongAttribute NON_NEGATIVE = new AbstractLongAttribute("foo", 1) {
        @Override
        public boolean isValid(long value) {
            return value >= 0;
        }
    };

    @Test
    public void testDefault() {
        assertEquals(100L, LA.getDefaultValue());
    }

    @Test
    public void checkValid() {
        LA.checkValid(Long.MIN_VALUE);
        LA.checkValid(Long.MAX_VALUE);
        LA.checkValid(new Long(Long.MIN_VALUE));
        LA.checkValid(new Long(Long.MIN_VALUE));

        NON_NEGATIVE.checkValid(Long.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        NON_NEGATIVE.checkValid(Long.MIN_VALUE);
    }

    @Test
    public void fromString() {
        assertEquals(-1L, LA.fromString(Long.valueOf(-1).toString()));
        assertEquals(Long.MIN_VALUE, LA.fromString(new Long(Long.MIN_VALUE).toString()));
        assertEquals(Long.MAX_VALUE, LA.fromString(new Long(Long.MAX_VALUE).toString()));
    }

    @Test
    public void get() {
        AttributeMap am = AttributeMaps.EMPTY_MAP;
        AttributeMap am1 = AttributeMaps.singleton(LA, -1L);
        AttributeMap am10000 = AttributeMaps.singleton(LA, 10000L);
        AttributeMap ammax = AttributeMaps.singleton(LA, Long.MAX_VALUE);

        assertEquals(100L, LA.getPrimitive(am));
        assertEquals(-1l, LA.getPrimitive(am1));
        assertEquals(10000l, LA.getPrimitive(am10000));
        assertEquals(Long.MAX_VALUE, LA.getPrimitive(ammax));

        assertEquals(10L, LA.getPrimitive(am, 10L));
        assertEquals(-1l, LA.getPrimitive(am1, 10));
        assertEquals(10000l, LA.getPrimitive(am10000, 10));
        assertEquals(Long.MAX_VALUE, LA.getPrimitive(ammax, 10));

        assertEquals(-1l, NON_NEGATIVE.getPrimitive(am, -1));
    }

    @Test
    public void isValid() {
        assertTrue(LA.isValid(Long.MIN_VALUE));
        assertTrue(LA.isValid(Long.MAX_VALUE));
        assertTrue(LA.isValid(Long.valueOf(Long.MIN_VALUE)));
        assertTrue(LA.isValid(Long.valueOf(Long.MAX_VALUE)));

        assertTrue(NON_NEGATIVE.isValid(Long.MAX_VALUE));
        assertFalse(NON_NEGATIVE.isValid(Long.MIN_VALUE));
    }

    @Test
    public void set() {
        AttributeMap am = new AttributeMaps.DefaultAttributeMap();
        assertEquals(10l, LA.setAttribute(am, 10l).get(LA));
        assertEquals(-10000l, LA.setAttribute(am, -10000l).get(LA));
        assertEquals(10000l, LA.setValue(am, Long.valueOf(10000)).get(LA));
        assertEquals(Long.MAX_VALUE, LA.setAttribute(am, Long.MAX_VALUE).get(LA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        NON_NEGATIVE.setAttribute(new AttributeMaps.DefaultAttributeMap(), -1L);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        LA.setAttribute(null, 1L);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10l, LA.toSingleton(-10).get(LA));
        assertEquals(10l, LA.toSingleton(10).get(LA));
        assertEquals(Long.MAX_VALUE, LA.toSingleton(Long.MAX_VALUE).get(LA));

        assertEquals(10l, NON_NEGATIVE.toSingleton(10).get(NON_NEGATIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        NON_NEGATIVE.toSingleton(-10);
    }

}
