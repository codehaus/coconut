/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.service.management;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.codehaus.cake.test.util.TestUtil.dummy;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.codehaus.cake.management.ManagedVisitor;
import org.junit.Before;
import org.junit.Test;

public class ManagementConfigurationTest {

    ManagementConfiguration m;

    @Before
    public void setUp() {
        m = new ManagementConfiguration();
    }

    @Test
    public void domain() {
        assertNull(m.getDomain());
        assertEquals(m, m.setDomain("mydomain"));
        assertEquals("mydomain", m.getDomain());
    }

    @Test(expected = IllegalArgumentException.class)
    public void domainIAE() {
        m.setDomain("foo\n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void domainIAE1() {
        m.setDomain(":");
    }

    @Test
    public void enabled() {
        assertFalse(m.isEnabled());
        assertEquals(m, m.setEnabled(true));
        assertTrue(m.isEnabled());
    }

    @Test
    public void mBeanServer() {
        assertNull(m.getMBeanServer());
        MBeanServer s = MBeanServerFactory.createMBeanServer();
        assertEquals(m, m.setMBeanServer(s));
        assertEquals(s, m.getMBeanServer());
    }

    @Test
    public void registrant() {
        ManagedVisitor mgv = dummy(ManagedVisitor.class);
        assertNull(m.getRegistrant());
        assertSame(m, m.setRegistrant(mgv));
        assertSame(mgv, m.getRegistrant());
    }
}
