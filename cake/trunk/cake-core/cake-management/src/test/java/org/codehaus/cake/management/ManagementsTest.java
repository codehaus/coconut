/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.codehaus.cake.management.stubs.SingleOperation;
import org.codehaus.cake.test.util.TestUtil;
import org.codehaus.cake.test.util.throwables.RuntimeException1;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ManagementsTest {
    Mockery context = new JUnit4Mockery();

    private DefaultManagedGroup dmg;

    private int initCount;

    private MBeanServer server;

    @Test
    public void delegatedManagedGroup() throws Exception {
        final ManagedGroup mg = context.mock(ManagedGroup.class);
        final ManagedGroup c1 = context.mock(ManagedGroup.class, "c1");
        final ManagedGroup c2 = context.mock(ManagedGroup.class, "c2");
        final ObjectName on = new ObjectName("ff.wfer:er=er");
        final MBeanServer server = TestUtil.dummy(MBeanServer.class);
        context.checking(new Expectations() {
            {
                one(mg).add(1);
                will(returnValue(mg));
                one(mg).addChild("name", "description");
                will(returnValue(c1));
                one(mg).getChildren();
                will(returnValue(Arrays.asList(c1, c2)));
                one(mg).getDescription();
                will(returnValue("desc"));
                one(mg).getName();
                will(returnValue("name"));
                one(mg).getObjectName();
                will(returnValue(on));
                one(mg).getObjects();
                will(returnValue(Arrays.asList(1, 2)));
                one(mg).getParent();
                will(returnValue(c1));
                one(mg).getServer();
                will(returnValue(server));
                one(mg).isRegistered();
                will(returnValue(true));
                one(mg).register(server, on);

                one(mg).remove();
                one(mg).unregister();
            }
        });
        ManagedGroup m = Managements.delegatedManagedGroup(mg);
        assertSame(m, m.add(1));
        assertTrue(m.addChild("name", "description") != m);
        assertEquals(2, m.getChildren().size());
        assertEquals("desc", m.getDescription());
        assertEquals("name", m.getName());
        assertSame(on, m.getObjectName());
        assertEquals(2, m.getObjects().size());
        assertSame(c1, m.getParent());
        assertSame(server, m.getServer());
        assertTrue(m.isRegistered());
        m.register(server, on);
        m.remove();
        m.toString();
        m.unregister();
    }

    @Test(expected = NullPointerException.class)
    public void delegatedManagedGroupNPE() {
        Managements.delegatedManagedGroup(null);
    }

    @Test
    public void hierarchicalRegistrant() throws Exception {
        SingleOperation parentOpr = new SingleOperation();
        dmg.add(parentOpr);
        dmg.addChild("a", "desc");
        // dmg.addChild("b", "desc").add(new Object()); <-Should this be registered?

        SingleOperation childOpr = new SingleOperation();
        dmg.addChild("c", "desc").add(childOpr).addChild("d", "desc").add(new SingleOperation());

        ManagedVisitor mgv = Managements.hierarchicalRegistrant(server,
                "org.coconut.management.test", "l1", "l2", "l3");
        assertSame(Void.TYPE, mgv.traverse(dmg));
        assertEquals(3 + initCount, server.getMBeanCount().intValue());
        ObjectName parent = new ObjectName("org.coconut.management.test:l1=fooa");
        ObjectName child = new ObjectName("org.coconut.management.test:l1=fooa,l2=c");
        ObjectName child2 = new ObjectName("org.coconut.management.test:l1=fooa,l2=c,l3=d");
        server.getObjectInstance(parent);
        server.getObjectInstance(child);
        server.getObjectInstance(child2);
        server.invoke(parent, "method1", null, null);
        assertEquals(1, parentOpr.invokeCount);
        // System.out.println(server.queryNames(null, null));
    }

    @Test(expected = NullPointerException.class)
    public void hierarchicalRegistrantNPE1() {
        Managements.hierarchicalRegistrant(null, "foo", "d");
    }

    @Test(expected = NullPointerException.class)
    public void hierarchicalRegistrantNPE2() {
        Managements.hierarchicalRegistrant(server, null, "d");
    }

    @Test(expected = NullPointerException.class)
    public void hierarchicalRegistrantNPE3() {
        Managements.hierarchicalRegistrant(server, "foo", (String[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void hierarchicalRegistrantNPE4() {
        Managements.hierarchicalRegistrant(server, "foo", new String[] { "asd", null });
    }

    @Before
    public void setup() {
        server = MBeanServerFactory.createMBeanServer();
        initCount = server.getMBeanCount();
        dmg = new DefaultManagedGroup("fooa", "booa");
    }

    @Test
    public void test() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Integer[] i2 = Managements.copyOf(i);
        assertTrue(Arrays.equals(i, i2));
        assertEquals(3, i2.length);
    }

    @Test
    public void test2() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Object[] i2 = Managements.copyOf(i, 3, Object[].class);
        assertTrue(Arrays.equals(i, i2));
    }

    @Test
    public void test3() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Number[] i2 = Managements.copyOf(i, 3, Number[].class);
        assertTrue(Arrays.equals(i, i2));
    }

    @Test
    public void test4() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Integer[] i2 = Managements.copyOf(i, 3, Integer[].class);
        assertTrue(Arrays.equals(i, i2));
    }

    @Test
    public void unregister() throws Exception {
        ManagedVisitor mv = Managements.unregister();
        final ManagedGroup mg = context.mock(ManagedGroup.class);
        final ManagedGroup c1 = context.mock(ManagedGroup.class, "c1");
        final ManagedGroup c2 = context.mock(ManagedGroup.class, "c2");
        context.checking(new Expectations() {
            {
                allowing(mg).getChildren();
                will(returnValue(Arrays.asList(c1)));
                allowing(c1).getChildren();
                will(returnValue(Arrays.asList(c2)));
                allowing(c2).getChildren();
                will(returnValue(Collections.EMPTY_LIST));

                one(c2).unregister();
                one(c1).unregister();
                one(mg).unregister();
            }
        });
        assertEquals(new HashMap(), mv.traverse(mg));
    }

    @Test
    public void unregisterException() throws Exception {
        ManagedVisitor<Map<ManagedGroup, Exception>> mv = Managements.unregister();
        final ManagedGroup mg = context.mock(ManagedGroup.class);
        final ManagedGroup c1 = context.mock(ManagedGroup.class, "c1");
        final ManagedGroup c2 = context.mock(ManagedGroup.class, "c2");
        context.checking(new Expectations() {
            {
                allowing(mg).getChildren();
                will(returnValue(Arrays.asList(c1)));
                allowing(c1).getChildren();
                will(returnValue(Arrays.asList(c2)));
                allowing(c2).getChildren();
                will(returnValue(Collections.EMPTY_LIST));

                one(c2).unregister();
                one(c1).unregister();
                will(throwException(new RuntimeException1()));
                one(mg).unregister();
            }
        });
        Map<ManagedGroup, Exception> result = mv.traverse(mg);
        assertEquals(1, result.size());
        assertTrue(result.get(c1) instanceof RuntimeException1);
    }
}
