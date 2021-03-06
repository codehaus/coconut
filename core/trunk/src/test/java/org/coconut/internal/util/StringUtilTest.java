/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test of {@link UnitOfTime}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class StringUtilTest {

    @Test(expected = NullPointerException.class)
    public void capitalizeNPE() {
        StringUtil.capitalize(null);
    }

    @Test
    public void capitalize() {
        assertEquals("", StringUtil.capitalize(""));
        assertEquals("Foo", StringUtil.capitalize("foo"));
        assertEquals("Foo", StringUtil.capitalize("Foo"));
    }
}
