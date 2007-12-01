package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.junit.Test;

public class AttributeTest {
    static final AbstractAttribute<String> DA = new DefaultAttribute("name", String.class,
            "default")
    {};

    AttributeMap am1 = new AttributeMaps.DefaultAttributeMap();

    AttributeMap am2 = AttributeMaps.singleton(DA, "value");

    @Test(expected = NullPointerException.class)
    public void AbstractAttributeNPE() {
        new DefaultAttribute(null, String.class, "default");
    }

    @Test(expected = NullPointerException.class)
    public void AbstractAttributeNPE1() {
        new DefaultAttribute("asd", null, "default");
    }

    @Test
    public void set() {
        AttributeMap am = new AttributeMaps.DefaultAttributeMap();
        assertEquals(0, am.size());
        assertSame(am, DA.setValue(am, "a"));
        assertEquals(1, am.size());
        assertEquals("a", am.get(DA));
        assertSame(am, DA.setValue(am, null));
        assertEquals(1, am.size());
        assertNull(am.get(DA));
    }

    @Test
    public void setValidate() {
        Attribute a = new ValidateAttribute("fooignore");
        a.setValue(new AttributeMaps.DefaultAttributeMap(), "fooasd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIllegal() {
        Attribute a = new ValidateAttribute("fooignore");
        a.setValue(new AttributeMaps.DefaultAttributeMap(), "asd");
    }

    @Test
    public void get() {
        assertEquals("default", DA.getValue(am1));
        assertEquals("value", DA.getValue(am2));
        assertEquals("foo", DA.getValue(am1, "foo"));
        assertEquals("value", DA.getValue(am2, "foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalDefaultValue() {
        new ValidateAttribute("erer");
    }

    @Test
    public void isSet() {
        assertFalse(DA.isSet(am1));
        assertTrue(DA.isSet(am2));
    }

    @Test
    public void nullDefaultValue() {
        assertNull(new DefaultAttribute("name", String.class, null).getDefaultValue());
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        DA.setValue(null, "value");
    }

    @Test
    public void test() {
        assertEquals("name", DA.getName());
        assertEquals(String.class, DA.getAttributeType());
        assertEquals("default", DA.getDefaultValue());
    }

    @Test
    public void toStrng() {
        assertEquals("name", DA.toString());
    }

    @Test
    public void unSet() {
        AttributeMap am1 = new AttributeMaps.DefaultAttributeMap();
        AttributeMap am2 = AttributeMaps.singleton(DA, "value");
        assertEquals(0, am1.size());
        assertEquals(1, am2.size());
        DA.unSet(am1);
        DA.unSet(am2);
        assertEquals(0, am1.size());
        assertEquals(0, am2.size());
    }

    @Test
    public void valid() {
        assertTrue(DA.isValid(""));
        assertTrue(DA.isValid(null));
        DA.checkValid("");
        DA.checkValid(null);
    }

    protected AttributeMap newMap() {
        return new AttributeMaps.DefaultAttributeMap();
    }

    static class DefaultAttribute extends AbstractAttribute<String> {
        public DefaultAttribute(String name, Class<String> clazz, String defaultValue) {
            super(name, clazz, defaultValue);
        }

        public String fromString(String str) {
            return str;
        }
    }

    static class ValidateAttribute extends DefaultAttribute {
        public ValidateAttribute(String defaultValue) {
            super("validate", String.class, defaultValue);
        }

        @Override
        public void checkValid(String value) {
            if (!isValid(value)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public boolean isValid(String value) {
            return value.startsWith("foo");
        }
    }
}
