/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import static org.coconut.core.AttributeMaps.EMPTY_MAP;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.serializeAndUnserialize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Tests {@link AttributeMaps#EMPTY_MAP}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AttributeMaps_EmptyMapTest {

    @Test
    public void various() {
        assertFalse(EMPTY_MAP.containsKey("foo"));
        assertFalse(EMPTY_MAP.containsValue("foo"));
    }

    /**
     * Tests that EMPTY_MAP is serializable and maintains the singleton property.
     * 
     * @throws Exception something went wrong
     */
    @Test
    public void serialization() throws Exception {
        assertIsSerializable(EMPTY_MAP);
        assertSame(EMPTY_MAP, serializeAndUnserialize(EMPTY_MAP));
    }
}
