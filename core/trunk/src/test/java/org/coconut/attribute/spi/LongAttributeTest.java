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
import org.coconut.operations.LongPredicates;
import org.coconut.operations.Ops.LongPredicate;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class LongAttributeTest {
    static final AbstractLongAttribute LONG_A = new AbstractLongAttribute("foo", 100) {};

    static final AbstractLongAttribute NON_NEGATIVE = new AbstractLongAttribute("foo", 1) {
        @Override
        public boolean isValid(long value) {
            return value >= 0;
        }
    };

    @Test
    public void testDefault() {
        assertEquals(100L, LONG_A.getDefaultValue());
    }

    @Test
    public void checkValid() {
        LONG_A.checkValid(Long.MIN_VALUE);
        LONG_A.checkValid(Long.MAX_VALUE);
        LONG_A.checkValid(new Long(Long.MIN_VALUE));
        LONG_A.checkValid(new Long(Long.MIN_VALUE));

        NON_NEGATIVE.checkValid(Long.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        NON_NEGATIVE.checkValid(Long.MIN_VALUE);
    }

    @Test
    public void fromString() {
        assertEquals(-1L, LONG_A.fromString(Long.valueOf(-1).toString()));
        assertEquals(Long.MIN_VALUE, LONG_A.fromString(new Long(Long.MIN_VALUE).toString()));
        assertEquals(Long.MAX_VALUE, LONG_A.fromString(new Long(Long.MAX_VALUE).toString()));
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(LONG_A, -1L);
        AttributeMap am10000 = Attributes.singleton(LONG_A, 10000L);
        AttributeMap ammax = Attributes.singleton(LONG_A, Long.MAX_VALUE);

        assertEquals(100L, LONG_A.getLong(am));
        assertEquals(-1l, LONG_A.getLong(am1));
        assertEquals(10000l, LONG_A.getLong(am10000));
        assertEquals(Long.MAX_VALUE, LONG_A.getLong(ammax));

        assertEquals(10L, LONG_A.getLong(am, 10L));
        assertEquals(-1l, LONG_A.getLong(am1, 10));
        assertEquals(10000l, LONG_A.getLong(am10000, 10));
        assertEquals(Long.MAX_VALUE, LONG_A.getLong(ammax, 10));

        assertEquals(-1l, NON_NEGATIVE.getLong(am, -1));
    }

    @Test
    public void isValid() {
        assertTrue(LONG_A.isValid(Long.MIN_VALUE));
        assertTrue(LONG_A.isValid(Long.MAX_VALUE));
        assertTrue(LONG_A.isValid(Long.valueOf(Long.MIN_VALUE)));
        assertTrue(LONG_A.isValid(Long.valueOf(Long.MAX_VALUE)));

        assertTrue(NON_NEGATIVE.isValid(Long.MAX_VALUE));
        assertFalse(NON_NEGATIVE.isValid(Long.MIN_VALUE));
    }

    @Test
    public void set() {
        AttributeMap am = new DefaultAttributeMap();
        assertEquals(10l, LONG_A.setLong(am, 10l).get(LONG_A));
        assertEquals(-10000l, LONG_A.setLong(am, -10000l).get(LONG_A));
        assertEquals(10000l, LONG_A.setValue(am, Long.valueOf(10000)).get(LONG_A));
        assertEquals(Long.MAX_VALUE, LONG_A.setLong(am, Long.MAX_VALUE).get(LONG_A));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        NON_NEGATIVE.setLong(new DefaultAttributeMap(), -1L);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        LONG_A.setLong(null, 1L);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10l, LONG_A.toSingletonLong(-10).get(LONG_A));
        assertEquals(10l, LONG_A.toSingletonLong(10).get(LONG_A));
        assertEquals(Long.MAX_VALUE, LONG_A.toSingletonLong(Long.MAX_VALUE).get(LONG_A));

        assertEquals(10l, NON_NEGATIVE.toSingletonLong(10).get(NON_NEGATIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        NON_NEGATIVE.toSingletonLong(-10);
    }

    @Test
    public void mapToLong() {
        TestUtil.assertIsSerializable(LONG_A.mapToLong());
        AttributeMap am = Attributes.singleton(LONG_A, 10000L);
        assertEquals(10000L, LONG_A.mapToLong().map(am));
        assertEquals(100L, LONG_A.mapToLong().map(Attributes.EMPTY_ATTRIBUTE_MAP));
    }

    @Test(expected = NullPointerException.class)
    public void mapper() {
        LONG_A.mapToLong().map(null);
    }

    @Test(expected = NullPointerException.class)
    public void filterOnLongNPE() {
        LONG_A.filterLong(null);
    }

    @Test
    public void filterOnLong() {
        Predicate<AttributeMap> filter = LONG_A.filterLong(LongPredicates.greaterThen(5));
        assertTrue(filter.evaluate(Attributes.singleton(LONG_A, 6L)));
        assertFalse(filter.evaluate(Attributes.singleton(LONG_A, 5L)));
        assertFalse(filter.evaluate(Attributes.singleton(LONG_A, 4L)));
    }
}
