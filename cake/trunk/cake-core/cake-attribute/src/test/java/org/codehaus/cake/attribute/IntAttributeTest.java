/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Various tests for {@link IntAttribute}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: IntAttributeTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class IntAttributeTest extends AtrStubs {
    static final IntAttribute ATR0 = new IntAttribute("a0", 0) {};
    static final IntAttribute ATR1 = new IntAttribute("a1", 1) {};
    static final IntAttribute ATR100 = new IntAttribute("a100", 100) {};

    static final IntAttribute NON_NEGATIVE = new IntAttribute("a50", 50) {
        @Override
        public boolean isValid(int value) {
            return value >= 5;
        }
    };

    @Test
    public void _constructors() {
        assertEquals(0, new IntAttribute() {}.getDefault().intValue());
        assertEquals(0, new IntAttribute("a") {}.getDefaultValue());
        assertFalse(new IntAttribute() {}.getName().equals(new IntAttribute() {}.getName()));
        assertFalse(new IntAttribute(3) {}.getName().equals(new IntAttribute(3) {}.getName()));
        assertTrue(new IntAttribute("a") {}.getName().equals(new IntAttribute("a") {}.getName()));
        assertEquals(3, new IntAttribute(3) {}.getDefaultValue());
        assertEquals(0, ATR0.getDefaultValue());
        assertEquals(100, ATR100.getDefaultValue());
        assertEquals(100, ATR100.getDefault().intValue());
        assertEquals("a100", ATR100.getName());

        assertSame(Integer.TYPE, ATR100.getType());
    }

    @Test
    public void checkValid() {
        ATR100.checkValid(Integer.MIN_VALUE);
        ATR100.checkValid(Integer.MAX_VALUE);
        ATR100.checkValid(new Integer(Integer.MIN_VALUE));
        ATR100.checkValid(new Integer(Integer.MIN_VALUE));

        NON_NEGATIVE.checkValid(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        NON_NEGATIVE.checkValid(4);
    }

    @Test
    public void comparator() {
        WithAttributes wa1 = withAtr(ATR1.singleton(1));
        WithAttributes wa2 = withAtr(ATR1.singleton(2));
        WithAttributes wa22 = withAtr(ATR1.singleton(2));
        WithAttributes wa3 = withAtr(ATR1.singleton(3));
        assertEquals(0, ATR1.compare(wa2, wa2));
        assertEquals(0, ATR1.compare(wa2, wa22));
        assertEquals(0, ATR1.compare(wa22, wa2));
        assertTrue(ATR1.compare(wa1, wa2) < 0);
        assertTrue(ATR1.compare(wa2, wa1) > 0);
        assertTrue(ATR1.compare(wa1, wa3) < 0);
        assertTrue(ATR1.compare(wa3, wa2) > 0);
        assertTrue(ATR1.compare(wa2, wa3) < 0);
    }

    @Test
    public void fromString() {
        assertEquals(-1, ATR100.fromString(Integer.valueOf(-1).toString()));
        assertEquals(Integer.MIN_VALUE, ATR100
                .fromString(new Integer(Integer.MIN_VALUE).toString()));
        assertEquals(Integer.MAX_VALUE, ATR100
                .fromString(new Integer(Integer.MAX_VALUE).toString()));
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(ATR100, -1);
        AttributeMap am111 = Attributes.singleton(ATR100, 111);
        AttributeMap ammax = Attributes.singleton(ATR100, Integer.MAX_VALUE);

        assertEquals(100, ATR100.get(withAtr(am)));
        assertEquals(-1, ATR100.get(withAtr(am1)));
        assertEquals(111, ATR100.get(withAtr(am111)));
        assertEquals(Integer.MAX_VALUE, ATR100.get(withAtr(ammax)));

        assertEquals(10, ATR100.get(withAtr(am), 10));
        assertEquals(-1, ATR100.get(withAtr(am1), 10));
        assertEquals(111, ATR100.get(withAtr(am111), 10));
        assertEquals(Integer.MAX_VALUE, ATR100.get(withAtr(ammax), 10));

        assertEquals(-1, NON_NEGATIVE.get(withAtr(am), -1));

        assertEquals(100, ATR100.get(withAtr(am)));
        assertEquals(-1, ATR100.get(withAtr(am1)));
        assertEquals(10, ATR100.get(withAtr(am), 10));
        assertEquals(-1, ATR100.get(withAtr(am1), 10));

    }

    @Test
    public void isValid() {
        assertTrue(ATR100.isValid(Integer.MIN_VALUE));
        assertTrue(ATR100.isValid(Integer.MAX_VALUE));
        assertTrue(ATR100.isValid(Integer.valueOf(Integer.MIN_VALUE)));
        assertTrue(ATR100.isValid(Integer.valueOf(Integer.MAX_VALUE)));

        assertTrue(NON_NEGATIVE.isValid(5));
        assertFalse(NON_NEGATIVE.isValid(4));
    }

    @Test
    public void set() {
        AttributeMap am = new DefaultAttributeMap();
        assertEquals(10, ATR100.set(am, 10).get(ATR100));
        assertEquals(-111, ATR100.set(withAtr(am), -111).get(ATR100));
        assertEquals(111, ATR100.set(am, Integer.valueOf(111)).get(ATR100));
        assertEquals(Integer.MAX_VALUE, ATR100.set(am, Integer.MAX_VALUE).get(ATR100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        NON_NEGATIVE.set(new DefaultAttributeMap(), 2);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        ATR100.set((AttributeMap) null, 1);
    }

    @Test
    public void toSingleton() {
        assertEquals(-10, ATR100.singleton(-10).get(ATR100));
        assertEquals(10, ATR100.singleton(10).get(ATR100));
        assertEquals(Integer.MAX_VALUE, ATR100.singleton(Integer.MAX_VALUE).get(ATR100));

        assertEquals(10, NON_NEGATIVE.singleton(10).get(NON_NEGATIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        NON_NEGATIVE.singleton(3);
    }
}
