/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.security.Permission;

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

    public static class A1 {
        public void foo() {}

        public void foo2(String ignore) {}
    }

    public static class A2 extends A1 {
        public void foo() {}
    }

    public static class B2 {
        public void foo() {}
    }

    public static class A3 extends A2 {
        public void foo2(String ignore) {}
    }

    @Test
    public void overridesMethod() {
        assertTrue(ClassUtils.overridesMethod(A1.class, A2.class, "foo"));
        assertFalse(ClassUtils.overridesMethod(A1.class, A2.class, "foo2", String.class));
        assertTrue(ClassUtils.overridesMethod(A1.class, B2.class, "foo"));
        assertTrue(ClassUtils.overridesMethod(A1.class, A3.class, "foo"));
        assertTrue(ClassUtils.overridesMethod(A1.class, A3.class, "foo2", String.class));
    }

    @Test
    public void overridesMethodSecurityManager() {
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {}

            @Override
            public void checkMemberAccess(Class<?> clazz, int which) {
                throw new SecurityException();
            }
        });
        try {
            assertTrue(ClassUtils.overridesMethod(A1.class, A2.class, "foo2", String.class));
        } finally {
            System.setSecurityManager(null);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void overridesMethodIAE() {
        ClassUtils.overridesMethod(A1.class, A2.class, "unknown");
    }
}
