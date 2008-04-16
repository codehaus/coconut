package org.codehaus.cake.internal.attribute.generator;

import org.codehaus.cake.attribute.AtrStubs;
import org.junit.Test;

public class AttributeInfoTest extends AtrStubs {

    @Test(expected = NullPointerException.class)
    public void testConstructor() {
        new DefaultAttributeConfiguration(null, false, false);
    }

    @Test
    public void testToString() {
        new DefaultAttributeConfiguration(I_1,false,false).toString();
    }
}
