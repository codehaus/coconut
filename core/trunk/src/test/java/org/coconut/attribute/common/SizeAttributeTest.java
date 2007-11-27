package org.coconut.attribute.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.Attribute;
import org.junit.Test;

public class SizeAttributeTest extends AbstractAttributeTest {

    SizeAttribute s = SizeAttribute.INSTANCE;

    @Test
    public void isValid() {
        isValid(0l, Long.MAX_VALUE);
        isNotValid(Long.MIN_VALUE, -1l);
    }

    @Override
    Attribute a() {
        return SizeAttribute.INSTANCE;
    }
}
