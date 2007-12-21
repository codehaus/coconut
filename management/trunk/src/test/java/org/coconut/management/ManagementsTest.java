/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

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

import org.coconut.management.defaults.DefaultManagedGroup;
import org.coconut.management.defaults.stubs.SingleOperation;
import org.coconut.test.throwables.RuntimeException1;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ManagementsTest {
    private DefaultManagedGroup dmg;

    private int initCount;

    private MBeanServer server;

    Mockery context = new JUnit4Mockery();

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
        assertEquals(3 + initCount, server.getMBeanCount());
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
    public void unregister() throws Exception {
        ManagedVisitor mv = Managements.unregister();
        final ManagedGroup mg = context.mock(ManagedGroup.class);
        final ManagedGroup c1 = context.mock(ManagedGroup.class);
        final ManagedGroup c2 = context.mock(ManagedGroup.class);
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
        final ManagedGroup c1 = context.mock(ManagedGroup.class);
        final ManagedGroup c2 = context.mock(ManagedGroup.class);
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
