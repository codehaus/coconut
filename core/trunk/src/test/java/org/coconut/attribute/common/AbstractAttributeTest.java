package org.coconut.attribute.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.attribute.Attribute;

public abstract class AbstractAttributeTest {

    abstract Attribute a();
    
    public void isNotValid(Long... o) {
        for (Long oo : o) {
            assertFalse(a().isValid(oo));
        }
    }

    public void isValid(Long... o) {
        for (Long oo : o) {
            assertTrue(a().isValid(oo));
        }
    }
}
