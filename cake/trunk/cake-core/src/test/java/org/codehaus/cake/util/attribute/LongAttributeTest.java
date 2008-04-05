/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LongAttributeTest extends AtrStubs {
    static final LongAttribute LONG_A = new LongAttribute("foo", 100) {};

    static final LongAttribute NON_NEGATIVE = new LongAttribute("foo", 1) {
        @Override
        public boolean isValid(long value) {
            return value >= 0;
        }
    };

    @Test
    public void testDefault() {
        assertEquals(100L, LONG_A.getDefault().longValue());
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
        assertEquals(-1L, LONG_A.fromString(Long.valueOf(-1).toString()).longValue());
        assertEquals(Long.MIN_VALUE, LONG_A.fromString(new Long(Long.MIN_VALUE).toString())
                .longValue());
        assertEquals(Long.MAX_VALUE, LONG_A.fromString(new Long(Long.MAX_VALUE).toString())
                .longValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStringIl() {
        LONG_A.fromString("wer");
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(LONG_A, -1L);
        AttributeMap am10000 = Attributes.singleton(LONG_A, 10000L);
        AttributeMap ammax = Attributes.singleton(LONG_A, Long.MAX_VALUE);

        assertEquals(100L, LONG_A.getValue(am));
        assertEquals(-1l, LONG_A.getValue(am1));
        assertEquals(10000l, LONG_A.getValue(am10000));
        assertEquals(Long.MAX_VALUE, LONG_A.getValue(ammax));
        assertEquals(10000l, LONG_A.getValue(withAtr(am10000)));
        assertEquals(Long.MAX_VALUE, LONG_A.getValue(withAtr(ammax)));
        
        assertEquals(10L, LONG_A.getValue(am, 10L));
        assertEquals(-1l, LONG_A.getValue(am1, 10));
        assertEquals(10000l, LONG_A.getValue(am10000, 10));
        assertEquals(Long.MAX_VALUE, LONG_A.getValue(ammax, 10));
        assertEquals(10000l, LONG_A.getValue(withAtr(am10000), 10));
        assertEquals(Long.MAX_VALUE, LONG_A.getValue(withAtr(ammax), 10));
        
        assertEquals(-1l, NON_NEGATIVE.getValue(am, -1));
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
        assertEquals(10l, LONG_A.set(am, 10l).get(LONG_A));
        assertEquals(-10000l, LONG_A.set(am, -10000l).get(LONG_A));
        assertEquals(10000l, LONG_A.set(am, Long.valueOf(10000)).get(LONG_A));
        assertEquals(Long.MAX_VALUE, LONG_A.set(am, Long.MAX_VALUE).get(LONG_A));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        NON_NEGATIVE.set(new DefaultAttributeMap(), -1L);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        LONG_A.set(null, 1L);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10l, LONG_A.singleton(-10).get(LONG_A));
        assertEquals(10l, LONG_A.singleton(10).get(LONG_A));
        assertEquals(Long.MAX_VALUE, LONG_A.singleton(Long.MAX_VALUE).get(LONG_A));

        assertEquals(10l, NON_NEGATIVE.singleton(10).get(NON_NEGATIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        NON_NEGATIVE.singleton(-10);
    }

    @Test(expected = NullPointerException.class)
    public void opNPE() {
        LONG_A.op(null);
    }

    @Test
    public void op() {
        assertEquals(5, L_1.op(withAtr(L_1.singleton(5))));
    }

    @Test
    public void comparator() {
        WithAttributes wa1 = withAtr(L_1.singleton(1));
        WithAttributes wa2 = withAtr(L_1.singleton(2));
        WithAttributes wa22 = withAtr(L_1.singleton(2));
        WithAttributes wa3 = withAtr(L_1.singleton(3));
        assertEquals(0, L_1.compare(wa2, wa2));
        assertEquals(0, L_1.compare(wa2, wa22));
        assertEquals(0, L_1.compare(wa22, wa2));
        assertTrue(L_1.compare(wa1, wa2) < 0);
        assertTrue(L_1.compare(wa2, wa1) > 0);
        assertTrue(L_1.compare(wa1, wa3) < 0);
        assertTrue(L_1.compare(wa3, wa2) > 0);
        assertTrue(L_1.compare(wa2, wa3) < 0);
    }
}
