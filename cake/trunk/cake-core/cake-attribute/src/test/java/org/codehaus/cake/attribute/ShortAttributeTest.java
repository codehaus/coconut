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
 * Various tests for {@link ShortAttribute}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ShortAttributeTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class ShortAttributeTest extends AtrStubs {
    static final ShortAttribute ATR0 = new ShortAttribute("a0", (short) 0) {};
    static final ShortAttribute ATR1 = new ShortAttribute("a1", (short) 1) {};
    static final ShortAttribute ATR100 = new ShortAttribute("a100", (short) 100) {};

    static final ShortAttribute NON_NEGATIVE = new ShortAttribute("a50", (short) 50) {
        @Override
        public boolean isValid(short value) {
            return value >= (short) 5;
        }
    };

    @Test
    public void _constructors() {
        assertEquals((short) 0, new ShortAttribute() {}.getDefault().shortValue());
        assertEquals((short) 0, new ShortAttribute("a") {}.getDefaultValue());
        assertFalse(new ShortAttribute() {}.getName().equals(new ShortAttribute() {}.getName()));
        assertFalse(new ShortAttribute((short) 3) {}.getName().equals(
                new ShortAttribute((short) 3) {}.getName()));
        assertTrue(new ShortAttribute("a") {}.getName()
                .equals(new ShortAttribute("a") {}.getName()));
        assertEquals((short) 3, new ShortAttribute((short) 3) {}.getDefaultValue());
        assertEquals((short) 0, ATR0.getDefaultValue());
        assertEquals((short) 100, ATR100.getDefaultValue());
        assertEquals((short) 100, ATR100.getDefault().shortValue());
        assertEquals("a100", ATR100.getName());

        assertSame(Short.TYPE, ATR100.getType());
    }

    @Test
    public void checkValid() {
        ATR100.checkValid(Short.MIN_VALUE);
        ATR100.checkValid(Short.MAX_VALUE);
        ATR100.checkValid(new Short(Short.MIN_VALUE));
        ATR100.checkValid(new Short(Short.MIN_VALUE));

        NON_NEGATIVE.checkValid((short) 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        NON_NEGATIVE.checkValid((short) 4);
    }

    @Test
    public void comparator() {
        WithAttributes wa1 = withAtr(ATR1.singleton((short) 1));
        WithAttributes wa2 = withAtr(ATR1.singleton((short) 2));
        WithAttributes wa22 = withAtr(ATR1.singleton((short) 2));
        WithAttributes wa3 = withAtr(ATR1.singleton((short) 3));
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
        assertEquals((short) -1, ATR100.fromString(Integer.valueOf(-1).toString()));
        assertEquals(Short.MIN_VALUE, ATR100.fromString(new Short(Short.MIN_VALUE).toString()));
        assertEquals(Short.MAX_VALUE, ATR100.fromString(new Short(Short.MAX_VALUE).toString()));
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(ATR100, (short) -1);
        AttributeMap am111 = Attributes.singleton(ATR100, (short) 111);
        AttributeMap ammax = Attributes.singleton(ATR100, Short.MAX_VALUE);

        assertEquals((short) 100, ATR100.get(withAtr(am)));
        assertEquals((short) -1, ATR100.get(withAtr(am1)));
        assertEquals((short) 111, ATR100.get(withAtr(am111)));
        assertEquals(Short.MAX_VALUE, ATR100.get(withAtr(ammax)));

        assertEquals((short) 10, ATR100.get(withAtr(am), (short) 10));
        assertEquals((short) -1, ATR100.get(withAtr(am1), (short) 10));
        assertEquals((short) 111, ATR100.get(withAtr(am111), (short) 10));
        assertEquals(Short.MAX_VALUE, ATR100.get(withAtr(ammax), (short) 10));

        assertEquals((short) -1, NON_NEGATIVE.get(withAtr(am), (short) -1));

        assertEquals((short) 100, ATR100.get(withAtr(am)));
        assertEquals((short) -1, ATR100.get(withAtr(am1)));
        assertEquals((short) 10, ATR100.get(withAtr(am), (short) 10));
        assertEquals((short) -1, ATR100.get(withAtr(am1), (short) 10));

    }

    @Test
    public void isValid() {
        assertTrue(ATR100.isValid(Short.MIN_VALUE));
        assertTrue(ATR100.isValid(Short.MAX_VALUE));
        assertTrue(ATR100.isValid(Short.valueOf(Short.MIN_VALUE)));
        assertTrue(ATR100.isValid(Short.valueOf(Short.MAX_VALUE)));

        assertTrue(NON_NEGATIVE.isValid((short) 5));
        assertFalse(NON_NEGATIVE.isValid((short) 4));
    }

    @Test
    public void set() {
        AttributeMap am = new DefaultAttributeMap();
        assertEquals((short) 10, ATR100.set(am, (short) 10).get(ATR100));
        assertEquals((short) -111, ATR100.set(withAtr(am), (short) -111).get(ATR100));
        assertEquals((short) 111, ATR100.set(am, Short.valueOf((short) 111)).get(ATR100));
        assertEquals(Short.MAX_VALUE, ATR100.set(am, Short.MAX_VALUE).get(ATR100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIAE() {
        NON_NEGATIVE.set(new DefaultAttributeMap(), (short) 2);
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        ATR100.set((AttributeMap) null, (short) 1);
    }

    @Test
    public void toSingleton() {
        assertEquals((short) -10, ATR100.singleton((short) -10).get(ATR100));
        assertEquals((short) 10, ATR100.singleton((short) 10).get(ATR100));
        assertEquals(Short.MAX_VALUE, ATR100.singleton(Short.MAX_VALUE).get(ATR100));

        assertEquals((short) 10, NON_NEGATIVE.singleton((short) 10).get(NON_NEGATIVE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        NON_NEGATIVE.singleton((short) 3);
    }
}
