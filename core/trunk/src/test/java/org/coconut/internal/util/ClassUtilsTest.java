package org.coconut.internal.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

public class ClassUtilsTest {

    @Test
    public void fromPrimitive() {
        assertSame(Boolean.class, ClassUtils.fromPrimitive(Boolean.TYPE));
        assertSame(Byte.class, ClassUtils.fromPrimitive(Byte.TYPE));
        assertSame(Character.class, ClassUtils.fromPrimitive(Character.TYPE));
        assertSame(Double.class, ClassUtils.fromPrimitive(Double.TYPE));
        assertSame(Float.class, ClassUtils.fromPrimitive(Float.TYPE));
        assertSame(Integer.class, ClassUtils.fromPrimitive(Integer.TYPE));
        assertSame(Long.class, ClassUtils.fromPrimitive(Long.TYPE));
        assertSame(Short.class, ClassUtils.fromPrimitive(Short.TYPE));
        assertSame(Void.class, ClassUtils.fromPrimitive(Void.TYPE));
        
        assertSame(String.class, ClassUtils.fromPrimitive(String.class));
    }

    @Test
    public void isNumber() {
        assertTrue(ClassUtils.isNumberOrPrimitiveNumber(Float.TYPE));
        assertTrue(ClassUtils.isNumberOrPrimitiveNumber(Float.class));
        assertTrue(ClassUtils.isNumberOrPrimitiveNumber(BigDecimal.class));
        assertFalse(ClassUtils.isNumberOrPrimitiveNumber(Boolean.TYPE));
        assertFalse(ClassUtils.isNumberOrPrimitiveNumber(String.class));
    }
}
