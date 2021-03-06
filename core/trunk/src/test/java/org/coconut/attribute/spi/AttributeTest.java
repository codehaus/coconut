/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.attribute.DefaultAttributeMap;
import org.coconut.operations.Predicates;
import org.coconut.operations.StringPredicates;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class AttributeTest {
    static final AbstractAttribute<String> ATR = new DefaultAttribute("name", String.class,
            "default");

    static final AbstractAttribute<String> ATR_VALIDATE = new ValidateAttribute("fooignore");

    // ValidateAttribute
    AttributeMap am1 = new DefaultAttributeMap();

    AttributeMap am2 = Attributes.singleton(ATR, "value");

    @Test(expected = NullPointerException.class)
    public void abstractAttributeNPE() {
        new DefaultAttribute(null, String.class, "default");
    }

    @Test(expected = NullPointerException.class)
    public void abstractAttributeNPE1() {
        new DefaultAttribute("asd", null, "default");
    }

    @Test
    public void checkValid() {
        ATR_VALIDATE.checkValid("foowerwer");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        ATR_VALIDATE.checkValid("werwer");
    }

    @Test
    public void get() {
        assertEquals("default", ATR.getValue(am1));
        assertEquals("value", ATR.getValue(am2));
        assertEquals("foo", ATR.getValue(am1, "foo"));
        assertEquals("value", ATR.getValue(am2, "foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalDefaultValue() {
        new ValidateAttribute("erer");
    }

    @Test
    public void isSet() {
        assertFalse(ATR.isSet(am1));
        assertTrue(ATR.isSet(am2));
    }

    @Test
    public void nullDefaultValue() {
        assertNull(new DefaultAttribute("name", String.class, null).getDefaultValue());
    }

    @Test
    public void set() {
        AttributeMap am = new DefaultAttributeMap();
        assertEquals(0, am.size());
        assertSame(am, ATR.setValue(am, "a"));
        assertEquals(1, am.size());
        assertEquals("a", am.get(ATR));
        assertSame(am, ATR.setValue(am, null));
        assertEquals(1, am.size());
        assertNull(am.get(ATR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIllegal() {
        Attribute a = new ValidateAttribute("fooignore");
        a.setValue(new DefaultAttributeMap(), "asd");
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        ATR.setValue(null, "value");
    }

    @Test
    public void setValidate() {
        Attribute a = new ValidateAttribute("fooignore");
        a.setValue(new DefaultAttributeMap(), "fooasd");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void fromStringUOE() {
        Attribute a = new ValidateAttribute("fooignore");
        a.fromString("foo");
    }

    @Test
    public void test() {
        assertEquals("name", ATR.getName());
        assertEquals(String.class, ATR.getAttributeType());
        assertEquals("default", ATR.getDefaultValue());
    }

    @Test
    public void toSingleton() {
        AttributeMap map = ATR.singleton("singleton");
        assertEquals(1, map.size());
        assertTrue(map.containsKey(ATR));
        assertEquals("singleton", map.get(ATR));

        map = ATR.singleton(null);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(ATR));
        assertNull(map.get(ATR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        ATR_VALIDATE.singleton("sdfsdf");
    }

    @Test
    public void toStrng() {
        assertEquals("name", ATR.toString());
    }

    @Test
    public void unSet() {
        AttributeMap am1 = new DefaultAttributeMap();
        assertEquals(0, am1.size());
        ATR.remove(am1);
        assertEquals(0, am1.size());
    }

    @Test
    public void valid() {
        assertTrue(ATR.isValid(""));
        assertTrue(ATR.isValid(null));
        ATR.checkValid("");
        ATR.checkValid(null);
    }

    protected AttributeMap newMap() {
        return new DefaultAttributeMap();
    }

    @Test
    public void map() {
        TestUtil.assertIsSerializable(ATR.map());
        AttributeMap am = Attributes.singleton(ATR, "abc");
        assertEquals("abc", ATR.map().map(am));
        assertEquals("default", ATR.map().map(Attributes.EMPTY_ATTRIBUTE_MAP));
    }

    @Test(expected = NullPointerException.class)
    public void mapper() {
        ATR.map().map(null);
    }

    @Test(expected = NullPointerException.class)
    public void filterNPE() {
        ATR.filter(null);
    }

    @Test
    public void filter() {
        Predicate<AttributeMap> filter = ATR.filter(StringPredicates.startsWith("A"));
        assertTrue(filter.evaluate(Attributes.singleton(ATR, "Adf")));
        assertFalse(filter.evaluate(Attributes.singleton(ATR, "Bdf")));
        assertFalse(filter.evaluate(Attributes.singleton(ATR, "adf")));
    }

    static class DefaultAttribute extends AbstractAttribute<String> {
        public DefaultAttribute(String name, Class<String> clazz, String defaultValue) {
            super(name, clazz, defaultValue);
        }

// public String fromString(String str) {
// return str;
// }
    }

    static class ValidateAttribute extends DefaultAttribute {
        public ValidateAttribute(String defaultValue) {
            super("validate", String.class, defaultValue);
        }

        @Override
        public boolean isValid(String value) {
            return value.startsWith("foo");
        }
    }
}
