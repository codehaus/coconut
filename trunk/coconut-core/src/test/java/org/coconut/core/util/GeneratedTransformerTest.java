/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import junit.framework.JUnit4TestAdapter;

import org.coconut.core.Transformer;
import org.coconut.core.util.Transformers.DynamicTransformer;
import static org.coconut.core.util.Transformers.*;
import org.junit.Test;

public class GeneratedTransformerTest {

    @Test
    public void testSimpleCreate() {
        assertNotNull(t("method"));
        assertEquals("m", t("method").transform(new GeneratedTransformerMock()));
        assertEquals("m2", t("method2").transform(new GeneratedTransformerMock()));
        assertEquals("im", t("interfaceMethod").transform(new GeneratedTransformerMock()));
    }

    @Test
    public void testInheritance() {
        assertEquals("moverride", t("method").transform(
                new GeneratedTransformerMockChild()));
        assertEquals("m2", t("method2").transform(new GeneratedTransformerMockChild()));

        assertEquals("moverride", t("method").transform(
                new GeneratedTransformerMockChild()));
        assertEquals("m2", t("method2").transform(new GeneratedTransformerMockChild()));
    }

    @Test
    public void testInnerClass() {
        assertNotNull(transform(Simple.class, "foo"));
        assertEquals("foo1", transform(Simple.class, "foo").transform(
                new SimpleStaticImpl()));
        assertEquals("foo2", transform(Simple.class, "foo").transform(new SimpleImpl()));
    }

    @Test
    public void testPrimitive() {
        assertNotNull(t("ireturn"));
        assertEquals(1, t("ireturn").transform(new GeneratedTransformerMockChild()));
        assertEquals(2l, t("lreturn").transform(new GeneratedTransformerMockChild()));
        assertEquals((short) 3, t("sreturn").transform(
                new GeneratedTransformerMockChild()));
        assertEquals(4d, t("dreturn").transform(new GeneratedTransformerMockChild()));
        assertEquals(5f, t("freturn").transform(new GeneratedTransformerMockChild()));
        assertEquals((byte) 6, t("byreturn").transform(
                new GeneratedTransformerMockChild()));
        assertEquals((char) 7, t("creturn")
                .transform(new GeneratedTransformerMockChild()));
        assertEquals(true, t("breturn").transform(new GeneratedTransformerMockChild()));

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
                ((Transformers.ASMBasedTransformer) t("ireturn")).getParameters().length);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWithStringParameters() {
        DynamicTransformer gt = t("string1Arg", "5");
        assertNotNull(gt);
        assertEquals(5l, gt.transform(new GeneratedTransformerMockChild()));

        assertEquals(12l, t("string2Arg", "5", "7").transform(
                new GeneratedTransformerMockChild()));

        assertEquals(18l, t("string3Arg", "5", "7", "6").transform(
                new GeneratedTransformerMockChild()));

        assertEquals(20l, t("string4Arg", "5", "7", "6", "2").transform(
                new GeneratedTransformerMockChild()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWithVariousParameters() {
        DynamicTransformer gt = t("transform", "5", (Object) Integer.valueOf(1), Long
                .valueOf(4l));
        assertNotNull(gt);
        assertEquals(10l, gt.transform(new GeneratedTransformerMockChild()));
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testPrimitiveAndObjectCollision() {
//        t("iarg", 4);
//    }

    private static <T> DynamicTransformer<GeneratedTransformerMock, T> t(String method,
            Object... args) {
        return transform(GeneratedTransformerMock.class, method, args);
    }

    public static junit.framework.Test suite() {
        System.out.println("suite");
        return new JUnit4TestAdapter(GeneratedTransformerTest.class);
    }

    public static void main(String[] args) {
        Transformer tt = t("string1Arg", "4");
        System.out.println(tt.transform(new GeneratedTransformerMock()).getClass());
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
