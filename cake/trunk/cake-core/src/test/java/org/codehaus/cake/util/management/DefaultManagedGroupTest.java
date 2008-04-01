/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;

import org.codehaus.cake.util.management.stubs.MixedOperationsAttributes;
import org.codehaus.cake.util.management.stubs.SingleAttribute;
import org.codehaus.cake.util.management.stubs.SingleOperation;
import org.codehaus.cake.util.management.stubs.VariousAttributes;
import org.codehaus.cake.util.management.stubs.VariousOperations;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DefaultManagedGroupTest.java 510 2007-12-12 08:52:55Z kasper $
 */
public class DefaultManagedGroupTest {
    private final static ObjectName ON;

    static {
        try {
            ON = new ObjectName("example:name=hello");
        } catch (MalformedObjectNameException e) {
            throw new Error(e);
        }
    }

    DefaultManagedGroup dmg;

    private int initCount;

    private MBeanServer server;

    @Before
    public void setup() {
        server = MBeanServerFactory.createMBeanServer();
        initCount = server.getMBeanCount();
        dmg = new DefaultManagedGroup("foo", "boo");
    }

    @Test(expected = NullPointerException.class)
    public void defaultManagedGroupNPE1() {
        new DefaultManagedGroup(null, "boo");
    }

    @Test(expected = NullPointerException.class)
    public void defaultManagedGroupNPE2() {
        new DefaultManagedGroup("foo", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultManagedGroupISE1() {
        new DefaultManagedGroup("", "d");
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultManagedGroupISE2() {
        new DefaultManagedGroup("foo´+", "boo");
    }

    @Test
    public void init() {
        assertEquals("foo", dmg.getName());
        assertEquals("boo", dmg.getDescription());
        assertEquals(0, dmg.getChildren().size());
        assertNull(dmg.getObjectName());
        assertEquals(0, dmg.getObjects().size());
        assertNull(dmg.getParent());
        assertNull(dmg.getServer());
        assertFalse(dmg.isRegistered());
        dmg.toString(); // does not fail
        assertNotNull(dmg.getLock());
    }

    @Test
    public void addChild() {
        ManagedGroup mg = dmg.addChild("abb", "bcc");
        assertEquals("abb", mg.getName());
        assertEquals("bcc", mg.getDescription());
        assertEquals(0, mg.getChildren().size());
        assertNull(mg.getObjectName());
        assertEquals(0, mg.getObjects().size());
        assertSame(dmg, mg.getParent());
        assertNull(mg.getServer());
        assertEquals(1, dmg.getChildren().size());
        assertTrue(dmg.getChildren().contains(mg));
        assertSame(dmg.getLock(), ((DefaultManagedGroup) mg).getLock());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChildSameName() {
        dmg.addChild("abb", "bcc");
        dmg.addChild("abb", "bcc");
    }

    @Test
    public void removeChild() {
        ManagedGroup mg = dmg.addChild("abb", "bcc");
        assertEquals(1, dmg.getChildren().size());
        mg.remove();
        assertEquals(0, dmg.getChildren().size());
        mg.remove();// does not fail
    }

    @Test(expected = NullPointerException.class)
    public void registerServerNPE() throws JMException {
        dmg.add(new MixedOperationsAttributes());
        dmg.register(null, ON);
    }

    @Test(expected = NullPointerException.class)
    public void registerObjectNameNPE() throws JMException {
        dmg.add(new MixedOperationsAttributes());
        dmg.register(server, null);
    }

    @Test(expected = IllegalStateException.class)
    public void noRegisterTwice() throws JMException {
        dmg.add(new SingleOperation());
        dmg.register(server, ON);
        dmg.register(server, ON);
    }

    @Test(expected = NullPointerException.class)
    public void addNPE() {
        dmg.add(null);
    }

    @Test
    public void unregisterNoneRegistered() throws JMException {
        dmg.unregister();// ignored
    }

    @Test(expected = IllegalStateException.class)
    public void addRegistered() throws JMException {
        dmg.add(new SingleOperation());
        dmg.register(server, ON);
        dmg.add(new SingleAttribute());
    }

    @Test
    public void unregister() throws JMException {
        dmg.add(new SingleOperation());
        dmg.register(server, ON);
        assertEquals(initCount + 1, server.getMBeanCount().intValue());
        assertNotNull(server.getMBeanInfo(ON));
        dmg.unregister();
        assertNull(dmg.getParent());
        assertNull(dmg.getServer());
        assertFalse(dmg.isRegistered());
        assertEquals(initCount, server.getMBeanCount().intValue());
        try {
            server.getMBeanInfo(ON);
            fail("should throw");
        } catch (InstanceNotFoundException ok) {}
    }

    @Test(expected = IllegalArgumentException.class)
    public void noOperation() throws Throwable {
        SingleOperation o = new SingleOperation();
        dmg.add(o);
        dmg.register(server, ON);
        try {
            server.invoke(ON, "method1d", null, null);
        } catch (RuntimeMBeanException e) {
            throw e.getCause();
        }
    }

    @Test
    public void singleOperation() throws JMException {
        SingleOperation o = new SingleOperation();
        dmg.add(o);
        dmg.register(server, ON);
        assertTrue(dmg.isRegistered());
        assertSame(server, dmg.getServer());
        assertSame(ON, dmg.getObjectName());

        assertEquals(initCount + 1, server.getMBeanCount().intValue());
        MBeanInfo info = server.getMBeanInfo(ON);
        // System.out.println(info.getAttributes()[0].getName());
        assertEquals(0, info.getAttributes().length);
        assertEquals(0, info.getConstructors().length);
        assertEquals(1, info.getOperations().length);
        assertEquals(0, info.getNotifications().length);

        MBeanOperationInfo moi = info.getOperations()[0];
        assertEquals("method1", moi.getName());
        assertEquals("", moi.getDescription());

        assertEquals("void", moi.getReturnType());
        assertEquals(0, moi.getSignature().length);
        assertEquals(0, o.invokeCount);
        server.invoke(ON, "method1", null, null);
        assertEquals(1, o.invokeCount);

    }

    @Test
    public void variousOperations() throws JMException {
        VariousOperations o = new VariousOperations();
        dmg.add(o);
        dmg.register(server, ON);
        assertEquals(initCount + 1, server.getMBeanCount().intValue());
        MBeanInfo info = server.getMBeanInfo(ON);
        assertEquals(0, info.getAttributes().length);
        assertEquals(0, info.getConstructors().length);
        assertEquals(5, info.getOperations().length);
        assertEquals(0, info.getNotifications().length);

        MBeanOperationInfo moi = findOperation(info.getOperations(), "m2");
        assertEquals(0, o.invokeCount);
        server.invoke(ON, "m2", null, null);
        assertEquals(1, o.invokeCount);
        assertEquals("m3", server.invoke(ON, "method3", null, null));

        assertEquals("FOO", server.invoke(ON, "method4", new Object[] { "foo" },
                new String[] { "java.lang.String" }));

        moi = findOperation(info.getOperations(), "method5");
        assertEquals("desca", moi.getDescription());

        moi = findOperation(info.getOperations(), "foo");
        assertEquals("desc", moi.getDescription());
    }

    @Test
    public void singleAttribute() throws JMException {
        SingleAttribute o = new SingleAttribute();
        dmg.add(o);
        dmg.register(server, ON);
        assertEquals(initCount + 1, server.getMBeanCount().intValue());
        MBeanInfo info = server.getMBeanInfo(ON);
        assertEquals(1, info.getAttributes().length);
        assertEquals(0, info.getConstructors().length);
        assertEquals(0, info.getOperations().length);
        assertEquals(0, info.getNotifications().length);

        MBeanAttributeInfo moi = info.getAttributes()[0];
        assertEquals("String", moi.getName());
        assertEquals("", moi.getDescription());
        assertEquals("java.lang.String", moi.getType());
        assertTrue(moi.isReadable());
        assertTrue(moi.isWritable());
        assertFalse(moi.isIs());

        assertNull(server.getAttribute(ON, "String"));

        server.setAttribute(ON, new Attribute("String", "foo"));
        assertEquals("foo", server.getAttribute(ON, "String"));
    }

    @Test
    public void variousAttributes() throws JMException {
        VariousAttributes o = new VariousAttributes();
        dmg.add(o);
        dmg.register(server, ON);
        assertEquals(initCount + 1, server.getMBeanCount().intValue());
        MBeanInfo info = server.getMBeanInfo(ON);
        assertEquals(7, info.getAttributes().length);
        assertEquals(0, info.getConstructors().length);
        assertEquals(0, info.getOperations().length);
        assertEquals(0, info.getNotifications().length);

        AttributeList list = new AttributeList();
        list.add(new Attribute("ReadWrite", 123));
        list.add(new Attribute("WriteOnly", "boofoo"));
        list.add(new Attribute("DoNotExist", "boofoo"));
        AttributeList l = server.setAttributes(ON, list);
        assertEquals(2, l.size());
        assertEquals(123, o.getReadWrite().intValue());
        assertEquals("boofoo", o.getWriteOnly());

        list = server.getAttributes(ON, new String[] { "ReadWrite", "WriteOnly", "ReadOnly" });
        assertEquals(2, list.size());
// System.out.println(list.get(0).getClass());
        assertEquals("ReadWrite", ((Attribute) list.get(0)).getName());
        assertEquals(123, ((Attribute) list.get(0)).getValue());
        assertEquals("ReadOnly", ((Attribute) list.get(1)).getName());
        assertEquals(false, ((Attribute) list.get(1)).getValue());

    }

    static MBeanOperationInfo findOperation(MBeanOperationInfo[] operations, String name) {
        for (int i = 0; i < operations.length; i++) {
            if (operations[i].getName().equals(name)) {
                return operations[i];
            }
        }
        throw new IllegalArgumentException();
    }
}
