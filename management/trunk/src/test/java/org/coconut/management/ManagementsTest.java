package org.coconut.management;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.coconut.management.defaults.AttributedStub1;
import org.coconut.management.defaults.DefaultManagedGroup;
import org.junit.Before;
import org.junit.Test;

public class ManagementsTest {

    private MBeanServer server;

    private int initCount;

    DefaultManagedGroup dmg;

    @Before
    public void setup() {
        server = MBeanServerFactory.createMBeanServer();
        server = ManagementFactory.getPlatformMBeanServer();
        initCount = server.getMBeanCount();
        dmg = new DefaultManagedGroup("fooa", "booa");
    }

    @Test
    public void testAttributeStub1() throws Exception {
        AttributedStub1 o = new AttributedStub1();
        dmg.add(o);
        dmg.addChild("bahoo", "desc2").add(new AttributedStub1());
        
        ManagedGroupVisitor mgv = Managements.register(server,
                "org.coconut.management.test", "l1", "l2", "l3");
        mgv.visitManagedGroup(dmg);

        //Thread.sleep(10000000);
    }
}
