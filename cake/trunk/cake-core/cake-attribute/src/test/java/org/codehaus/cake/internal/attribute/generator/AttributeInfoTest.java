package org.codehaus.cake.internal.attribute.generator;

import org.codehaus.cake.attribute.IntAttribute;
import org.junit.Test;

public class AttributeInfoTest {

    private final static IntAttribute I_1 = new IntAttribute("L_1", 1) {};
    @Test(expected = NullPointerException.class)
    public void testConstructor() {
        new DefaultAttributeConfiguration(null, false, false);
    }

    @Test
    public void testToString() {
        new DefaultAttributeConfiguration(I_1, false, false).toString();
    }
}
