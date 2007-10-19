/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import static org.coconut.test.CollectionUtils.M1_TO_M5_SET;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EntrySetHashCodeEquals extends AbstractCacheTCKTest {

    /**
     * isEmpty is true of empty map and false for non-empty.
     */
    @Test
    public void nothing() {
       
 
    }
    
    
    /**
     * Maps with same contents are equal
     */
    @Test
    public void testEquals() {
        c = newCache();

        assertTrue(new HashSet().equals(c.entrySet()));
        assertTrue(c.entrySet().equals(new HashSet()));

        assertFalse(c.entrySet().equals(null));
        assertFalse(c.entrySet().equals(newCache(1).entrySet()));
        c = newCache(5);
        assertTrue(M1_TO_M5_SET.equals(c.entrySet()));
        assertTrue(c.entrySet().equals(M1_TO_M5_SET));

        assertFalse(c.entrySet().equals(null));
        assertFalse(c.entrySet().equals(newCache(4).entrySet()));
        assertFalse(c.entrySet().equals(newCache(6).entrySet()));
    }

    @Test
    public void testHashCode() {
        assertEquals(M1_TO_M5_SET.hashCode(), newCache(5).entrySet().hashCode());
        assertEquals(new HashSet().hashCode(), newCache().entrySet().hashCode());
    }
}
