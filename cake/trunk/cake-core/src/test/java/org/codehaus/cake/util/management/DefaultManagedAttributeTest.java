/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.management.MBeanAttributeInfo;
import javax.management.ReflectionException;

import org.codehaus.cake.util.management.annotation.ManagedAttribute;
import org.codehaus.cake.util.management.stubs.PrivateMethods;
import org.codehaus.cake.util.management.stubs.VariousAttributes;
import org.junit.Before;
import org.junit.Test;

public class DefaultManagedAttributeTest {

    Map<String, AbstractManagedAttribute> attr;

    VariousAttributes stub;

    @Before
    public void setup() throws Exception {
        BeanInfo bi = Introspector.getBeanInfo(VariousAttributes.class);
        stub = new VariousAttributes();
        attr = DefaultManagedAttribute.fromPropertyDescriptors(bi.getPropertyDescriptors(), stub);
        assertEquals(7, attr.size());
    }

    @Test(expected = NullPointerException.class)
    public void defaultManagedAttribute1NPE() {
        Method m = DefaultManagedAttribute.class.getMethods()[0];
        new DefaultManagedAttribute(null, m, m, "name", "desc");
    }

    @Test(expected = NullPointerException.class)
    public void defaultManagedAttribute2NPE() {
        new DefaultManagedAttribute(new Object(), null, null, "name", "desc");
    }

    @Test(expected = NullPointerException.class)
    public void defaultManagedAttribute3NPE() {
        Method m = DefaultManagedAttribute.class.getMethods()[0];
        new DefaultManagedAttribute(new Object(), m, m, null, "desc");
    }

    @Test(expected = NullPointerException.class)
    public void defaultManagedAttribute4NPE() {
        Method m = DefaultManagedAttribute.class.getMethods()[0];
        new DefaultManagedAttribute(new Object(), m, m, "name", null);
    }

    @Test
    public void readOnly() throws Exception {
        AbstractManagedAttribute att = attr.get("ReadOnly");
        MBeanAttributeInfo info = att.getInfo();
        assertEquals("ReadOnly", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("boolean", info.getType());
        assertTrue(info.isReadable());
        assertFalse(info.isWritable());
        assertTrue(info.isIs());

        assertFalse(stub.isReadOnly());
        stub.setReadOnly(true);
        assertTrue(stub.isReadOnly());
    }

    @Test(expected = IllegalStateException.class)
    public void readOnlySet() throws Exception {
        AbstractManagedAttribute att = attr.get("ReadOnly");
        att.setValue(false);
    }

    @Test
    public void writeOnly() throws Exception {
        AbstractManagedAttribute att = attr.get("WriteOnly");
        MBeanAttributeInfo info = att.getInfo();
        assertEquals("WriteOnly", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("java.lang.String", info.getType());
        assertFalse(info.isReadable());
        assertTrue(info.isWritable());
        assertFalse(info.isIs());

        assertNull(stub.getWriteOnly());
        att.setValue("foo");
        assertEquals("foo", stub.getWriteOnly());
    }

    @Test(expected = IllegalStateException.class)
    public void writeOnlySet() throws Exception {
        AbstractManagedAttribute att = attr.get("WriteOnly");
        att.getValue();
    }

    @Test
    public void readWritable() throws Exception {
        AbstractManagedAttribute att = attr.get("ReadWrite");
        MBeanAttributeInfo info = att.getInfo();
        assertEquals("ReadWrite", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("java.lang.Integer", info.getType());
        assertTrue(info.isReadable());
        assertTrue(info.isWritable());
        assertFalse(info.isIs());

        assertNull(att.getValue());
        att.setValue(123);
        assertEquals(123, att.getValue());
    }

    @Test(expected = LinkageError.class)
    public void getError() throws Exception {
        AbstractManagedAttribute att = attr.get("throwError");
        assertEquals("desc", att.getInfo().getDescription());
        att.getValue();
    }

    @Test(expected = LinkageError.class)
    public void setError() throws Exception {
        AbstractManagedAttribute att = attr.get("throwError");
        assertEquals("desc", att.getInfo().getDescription());
        att.setValue("foo");
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void getRuntimeException() throws Exception {
        AbstractManagedAttribute att = attr.get("throwRuntimeException");
        assertEquals("desc", att.getInfo().getDescription());
        att.getValue();
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void setRuntimeException() throws Exception {
        AbstractManagedAttribute att = attr.get("throwRuntimeException");
        assertEquals("desc", att.getInfo().getDescription());
        att.setValue("foo");
    }

    @Test(expected = IOException.class)
    public void getException() throws Throwable {
        AbstractManagedAttribute att = attr.get("Exception2");
        try {
            att.getValue();
            throw new AssertionError("should fail");
        } catch (ReflectionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IOException.class)
    public void setException() throws Throwable {
        AbstractManagedAttribute att = attr.get("Exception1");
        try {
            att.setValue("ignore");
            throw new AssertionError("should fail");
        } catch (ReflectionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void twoAttributeAnnotations() throws Exception {
        BeanInfo bi = Introspector.getBeanInfo(TwoAttributes.class);
        attr = DefaultManagedAttribute.fromPropertyDescriptors(bi.getPropertyDescriptors(),
                new TwoAttributes());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writableReader() throws Exception {
        BeanInfo bi = Introspector.getBeanInfo(WritableReader.class);
        attr = DefaultManagedAttribute.fromPropertyDescriptors(bi.getPropertyDescriptors(),
                new WritableReader());
    }

    @Test(expected = ReflectionException.class)
    public void illegalAccessGet() throws Exception {
        Method mGet = PrivateMethods.class.getDeclaredMethod("getIllegal");
        Method mSet = PrivateMethods.class.getDeclaredMethod("setIllegal", String.class);
        DefaultManagedAttribute opr = new DefaultManagedAttribute(new PrivateMethods(), mGet, mSet,
                "", "");
        opr.getValue();
    }
    @Test(expected = ReflectionException.class)
    public void illegalAccessSet() throws Exception {
        Method mGet = PrivateMethods.class.getDeclaredMethod("getIllegal");
        Method mSet = PrivateMethods.class.getDeclaredMethod("setIllegal", String.class);
        DefaultManagedAttribute opr = new DefaultManagedAttribute(new PrivateMethods(), mGet, mSet,
                "", "");
        opr.setValue("dd");
    }
    public static class WritableReader {
        @ManagedAttribute(isWriteOnly = true)
        public String getFoo() {
            return null;
        }
    }

    public static class TwoAttributes {

        @ManagedAttribute
        public String getFoo() {
            return null;
        }

        @ManagedAttribute
        public void setFoo(String ignore) {}
    }
}
