/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import static org.junit.Assert.assertEquals;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.coconut.management.defaults.DefaultManagedGroup;
import org.coconut.management.defaults.stubs.SingleOperation;
import org.junit.Before;
import org.junit.Test;

public class ManagementsTest {

    private MBeanServer server;

    private int initCount;

    private DefaultManagedGroup dmg;

    @Before
    public void setup() {
        server = MBeanServerFactory.createMBeanServer();
        initCount = server.getMBeanCount();
        dmg = new DefaultManagedGroup("fooa", "booa");
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
        mgv.visitManagedGroup(dmg);
        assertEquals(3 + initCount, server.getMBeanCount());
        ObjectName parent = new ObjectName("org.coconut.management.test:l1=fooa");
        ObjectName child = new ObjectName("org.coconut.management.test:l1=fooa,l2=c");
        ObjectName child2 = new ObjectName("org.coconut.management.test:l1=fooa,l2=c,l3=d");
        server.getObjectInstance(parent);
        server.getObjectInstance(child);
        server.getObjectInstance(child2);
        server.invoke(parent, "method1", null, null);
        assertEquals(1,parentOpr.invokeCount);
        // System.out.println(server.queryNames(null, null));
    }
}
