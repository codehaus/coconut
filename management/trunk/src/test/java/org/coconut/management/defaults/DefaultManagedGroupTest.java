/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultManagedGroupTest {
    private final static ObjectName ON;

    static {
        try {
            ON = new ObjectName("example:name=hello");
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
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

    @Test
    public void testAttributeStub1() throws JMException {

        AttributedStub1 o = new AttributedStub1();
        dmg.add(o);
        dmg.register(server, ON);
        assertEquals(initCount + 1, server.getMBeanCount());
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
        // System.out.println(Arrays.toString(server.getDomains()));
        assertEquals(0, o.invokeCount);
        server.invoke(ON, "method1", null, null);
        assertEquals(1, o.invokeCount);

        server.unregisterMBean(ON);
    }

    @Test
    public void testAttributeStub2() throws JMException {
        AttributedStub2 o = new AttributedStub2();
        dmg.add(o);
        dmg.register(server, ON);
        assertEquals(initCount + 1, server.getMBeanCount());
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

        assertEquals("FOO", server.invoke(ON, "method4", new Object[] { "foo" }, null));

        moi = findOperation(info.getOperations(), "method5");
        assertEquals("desca", moi.getDescription());

        moi = findOperation(info.getOperations(), "foo");
        assertEquals("desc", moi.getDescription());
    }

    @Test
    public void testAttributeStub3() throws JMException {
        AttributedStub3 o = new AttributedStub3();
        dmg.add(o);
        dmg.register(server, ON);
        assertEquals(initCount + 1, server.getMBeanCount());
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

    static MBeanOperationInfo findOperation(MBeanOperationInfo[] operations, String name) {
        for (int i = 0; i < operations.length; i++) {
            if (operations[i].getName().equals(name)) {
                return operations[i];
            }
        }
        throw new IllegalArgumentException();
    }
}
