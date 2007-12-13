package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.junit.Test;

public class AttributeTest {
    static final AbstractAttribute<String> ATR = new DefaultAttribute("name", String.class,
            "default");

    static final AbstractAttribute<String> ATR_VALIDATE = new ValidateAttribute("fooignore");

    // ValidateAttribute
    AttributeMap am1 = new Attributes.DefaultAttributeMap();

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
        AttributeMap am = new Attributes.DefaultAttributeMap();
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
        a.setValue(new Attributes.DefaultAttributeMap(), "asd");
    }

    @Test(expected = NullPointerException.class)
    public void setNPE() {
        ATR.setValue(null, "value");
    }

    @Test
    public void setValidate() {
        Attribute a = new ValidateAttribute("fooignore");
        a.setValue(new Attributes.DefaultAttributeMap(), "fooasd");
    }

    @Test
    public void test() {
        assertEquals("name", ATR.getName());
        assertEquals(String.class, ATR.getAttributeType());
        assertEquals("default", ATR.getDefaultValue());
    }

    @Test
    public void toSingleton() {
        AttributeMap map = ATR.toSingleton("singleton");
        assertEquals(1, map.size());
        assertTrue(map.containsKey(ATR));
        assertEquals("singleton", map.get(ATR));

        map = ATR.toSingleton(null);
        assertEquals(1, map.size());
        assertTrue(map.containsKey(ATR));
        assertNull(map.get(ATR));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        ATR_VALIDATE.toSingleton("sdfsdf");
    }

    @Test
    public void toStrng() {
        assertEquals("name", ATR.toString());
    }

    @Test
    public void unSet() {
        AttributeMap am1 = new Attributes.DefaultAttributeMap();
        AttributeMap am2 = Attributes.singleton(ATR, "value");
        assertEquals(0, am1.size());
        assertEquals(1, am2.size());
        ATR.unSet(am1);
        ATR.unSet(am2);
        assertEquals(0, am1.size());
        assertEquals(0, am2.size());
    }

    @Test
    public void valid() {
        assertTrue(ATR.isValid(""));
        assertTrue(ATR.isValid(null));
        ATR.checkValid("");
        ATR.checkValid(null);
    }

    protected AttributeMap newMap() {
        return new Attributes.DefaultAttributeMap();
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
        public boolean isValid(String value) {
            return value.startsWith("foo");
        }
    }
}
