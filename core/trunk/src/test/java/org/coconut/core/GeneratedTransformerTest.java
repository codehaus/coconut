/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import static org.coconut.core.Mappers.transform;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.coconut.core.Mappers.DynamicMapper;
import org.junit.Test;

public class GeneratedTransformerTest {

    @Test
    public void testSimpleCreate() {
        assertNotNull(t("method"));
        assertEquals("m", t("method").map(new GeneratedTransformerMock()));
        assertEquals("m2", t("method2").map(new GeneratedTransformerMock()));
        assertEquals("im", t("interfaceMethod").map(new GeneratedTransformerMock()));
    }

    @Test
    public void testInheritance() {
        assertEquals("moverride", t("method").map(
                new GeneratedTransformerMockChild()));
        assertEquals("m2", t("method2").map(new GeneratedTransformerMockChild()));

        assertEquals("moverride", t("method").map(
                new GeneratedTransformerMockChild()));
        assertEquals("m2", t("method2").map(new GeneratedTransformerMockChild()));
    }

    @Test
    public void testInnerClass() {
        assertNotNull(transform(Simple.class, "foo"));
        assertEquals("foo1", transform(Simple.class, "foo").map(
                new SimpleStaticImpl()));
        assertEquals("foo2", transform(Simple.class, "foo").map(new SimpleImpl()));
    }

    @Test
    public void testPrimitive() {
        assertNotNull(t("ireturn"));
        assertEquals(1, t("ireturn").map(new GeneratedTransformerMockChild()));
        assertEquals(2l, t("lreturn").map(new GeneratedTransformerMockChild()));
        assertEquals((short) 3, t("sreturn").map(
                new GeneratedTransformerMockChild()));
        assertEquals(4d, t("dreturn").map(new GeneratedTransformerMockChild()));
        assertEquals(5f, t("freturn").map(new GeneratedTransformerMockChild()));
        assertEquals((byte) 6, t("byreturn").map(
                new GeneratedTransformerMockChild()));
        assertEquals((char) 7, t("creturn")
                .map(new GeneratedTransformerMockChild()));
        assertEquals(true, t("breturn").map(new GeneratedTransformerMockChild()));

    }

    // @Test
    // public void failTestVoid() {
    // // TODO contract of void methods.
    // // pro: easy way to invoke method, just send any object into transformer
    // // con: specify wrong method
    //
    // // alternative have a specific that takes the return value as a
    // // parameter
    // // and the require to call that with Void.class
    // t( "voidReturn");
    // }

//    @Test(expected = NullPointerException.class)
//    public void testfailNullPassed() {
//        t("method").transform(null);
//    }

    @Test(expected = IllegalArgumentException.class)
    public void failNonExistinMethod() {
        t("NoSuchMethod");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failProtectedMethod() {
        t("protectedMethod");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failPackagePrivateMethod() {
        t("packagePrivateMethod");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failPrivateMethod() {
        t("privateMethod");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNoParameters() {
        assertEquals(0,
                ((Mappers.ASMBasedMapper) t("ireturn")).getParameters().length);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWithStringParameters() {
        DynamicMapper gt = t("string1Arg", "5");
        assertNotNull(gt);
        assertEquals(5l, gt.map(new GeneratedTransformerMockChild()));

        assertEquals(12l, t("string2Arg", "5", "7").map(
                new GeneratedTransformerMockChild()));

        assertEquals(18l, t("string3Arg", "5", "7", "6").map(
                new GeneratedTransformerMockChild()));

        assertEquals(20l, t("string4Arg", "5", "7", "6", "2").map(
                new GeneratedTransformerMockChild()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWithVariousParameters() {
        DynamicMapper gt = t("transform", "5", (Object) Integer.valueOf(1), Long
                .valueOf(4l));
        assertNotNull(gt);
        assertEquals(10l, gt.map(new GeneratedTransformerMockChild()));
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testPrimitiveAndObjectCollision() {
//        t("iarg", 4);
//    }

    private static <T> DynamicMapper<GeneratedTransformerMock, T> t(String method,
            Object... args) {
        return transform(GeneratedTransformerMock.class, method, args);
    }

    public static void main(String[] args) {
        Mapper tt = t("string1Arg", "4");
        System.out.println(tt.map(new GeneratedTransformerMock()).getClass());
        System.out.println(tt);
    }

    public interface Simple {
        String foo();
    }

    public static class SimpleStaticImpl implements Simple {
        public String foo() {
            return "foo1";
        }
    }

    class SimpleImpl implements Simple {
        public String foo() {
            return "foo2";
        }
    }

}
