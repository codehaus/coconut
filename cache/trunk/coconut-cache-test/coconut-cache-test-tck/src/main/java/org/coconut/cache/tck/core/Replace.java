/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;

import java.util.concurrent.ConcurrentMap;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests {@link ConcurrentMap#replace(Object, Object)} and
 * {@link ConcurrentMap#replace(Object, Object, Object)}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Replace extends AbstractCacheTCKTest {

    @Test
    public void replace2() {
        c = newCache(1);
        assertNull(c.replace(M2.getKey(), M2.getValue()));
        c = newCache(2);
        assertEquals(M1.getValue(), c.replace(M1.getKey(), M3.getValue()));
        assertEquals(M3.getValue(), c.get(M1.getKey()));
        assertNull(c.replace(M4.getKey(), M4.getValue()));
        assertFalse(c.containsValue(M4.getValue()));
        assertFalse(c.containsValue(M1.getValue()));
    }

    /**
     * replace(Key, null) throws {@link NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void replace2NPE1() {
        c = newCache(1);
        c.replace(M1.getKey(), null);
    }

    /**
     * replace(null, Value) throws {@link NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void replace2NPE2() {
        c = newCache(1);
        c.replace(null, M2.getValue());
    }
    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void replace2LazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.replace(M2.getKey(), M2.getValue());
        checkLazystart();
    }
    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void replace2ShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.replace(M2.getKey(), M2.getValue());
    }
    @Test
    public void testReplace3() {
        c = newCache(2);
        assertTrue(c.replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertEquals(M3.getValue(), c.get(M1.getKey()));
        assertFalse(c.replace(M1.getKey(), M1.getValue(), M3.getValue()));
        assertFalse(c.containsValue(M1.getValue()));
    }
    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void replace3LazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.replace(M1.getKey(), M1.getValue(), M3.getValue());
        checkLazystart();
    }
    
    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void replace3ShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.replace(M1.getKey(), M1.getValue(), M3.getValue());
    }
    /**
     * replace(Key,null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3OldValueNPE() {
        c = newCache(1);
        c.replace(M1.getKey(), null, M2.getValue());
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3NewValueNPE() {
        c = newCache(1);
        c.replace(M1.getKey(), M1.getValue(), null);
    }

    /**
     * replace(null,Value) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void testReplace3KeyNPE() {
        c = newCache(1);
        c.replace(null, M1.getValue(), M2.getValue());
    }

}
