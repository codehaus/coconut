/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultManagedGroupTest {

    @Test
    public void testConstructor() {
        DefaultManagedGroup dmg = new DefaultManagedGroup("foo", "desc", true);
        assertEquals("foo", dmg.getName());
        assertEquals("desc", dmg.getDescription());
        assertNull(dmg.getParent());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNPE1() {
        new DefaultManagedGroup(null, "desc", true);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNPE2() {
        new DefaultManagedGroup("foo", null, true);
    }
}
