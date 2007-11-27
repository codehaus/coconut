/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

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
        server = ManagementFactory.getPlatformMBeanServer();
        initCount = server.getMBeanCount();
        dmg = new DefaultManagedGroup("fooa", "booa");
    }

    @Test
    public void testAttributeStub1() throws Exception {
        SingleOperation o = new SingleOperation();
        dmg.add(o);
        dmg.addChild("bahoo", "desc2").add(new SingleOperation());
        
        ManagedVisitor mgv = Managements.register(server,
                "org.coconut.management.test", "l1", "l2", "l3");
        mgv.visitManagedGroup(dmg);

        //Thread.sleep(10000000);
    }
}
