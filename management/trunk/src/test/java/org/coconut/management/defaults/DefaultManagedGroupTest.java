/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.util.Arrays;

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

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultManagedGroupTest {
	private final static ObjectName on;

	private MBeanServer server;

	private int initCount;

	DefaultManagedGroup dmg;

	@Before
	public void setup() {
		server = MBeanServerFactory.createMBeanServer();
		initCount = server.getMBeanCount();
		dmg = new DefaultManagedGroup("foo", "boo");
	}

	static {
		try {
			on = new ObjectName("example:name=hello");
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testAttributeStub1() throws JMException {

		AttributedStub1 o = new AttributedStub1();
		dmg.add(o);
		dmg.register(server, on);
		assertEquals(initCount + 1, server.getMBeanCount());
		MBeanInfo info = server.getMBeanInfo(on);
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
		server.invoke(on, "method1", null, null);
		assertEquals(1, o.invokeCount);

		server.unregisterMBean(on);
	}

	@Test
	public void testAttributeStub2() throws JMException {
		AttributedStub2 o = new AttributedStub2();
		dmg.add(o);
		dmg.register(server, on);
		assertEquals(initCount + 1, server.getMBeanCount());
		MBeanInfo info = server.getMBeanInfo(on);
		assertEquals(0, info.getAttributes().length);
		assertEquals(0, info.getConstructors().length);
		assertEquals(5, info.getOperations().length);
		assertEquals(0, info.getNotifications().length);

		MBeanOperationInfo moi = findOperation(info.getOperations(), "m2");
		assertEquals(0, o.invokeCount);
		server.invoke(on, "m2", null, null);
		assertEquals(1, o.invokeCount);
		assertEquals("m3", server.invoke(on, "method3", null, null));

		assertEquals("FOO", server.invoke(on, "method4", new Object[] { "foo" }, null));

		moi = findOperation(info.getOperations(), "method5");
		assertEquals("desca", moi.getDescription());

		moi = findOperation(info.getOperations(), "foo");
		assertEquals("desc", moi.getDescription());
	}

	@Test
	public void testAttributeStub3() throws JMException {
		AttributedStub3 o = new AttributedStub3();
		dmg.add(o);
		dmg.register(server, on);
		assertEquals(initCount + 1, server.getMBeanCount());
		MBeanInfo info = server.getMBeanInfo(on);
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

		assertNull(server.getAttribute(on, "String"));

		server.setAttribute(on, new Attribute("String", "foo"));
		assertEquals("foo", server.getAttribute(on, "String"));
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
