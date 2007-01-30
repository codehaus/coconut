/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static org.coconut.test.TestUtil.assertEqual;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Caches_MapAdapterTest extends MockTestCase {

    @Test
    public void testMapAsCache() {
        Map<Integer, Integer> dummyMap = new HashMap<Integer, Integer>();

        Mock mock = mock(Map.class);
        Cache<Integer, Integer> c = Caches.mapToCache((Map) mock.proxy());

        mock.expects(once()).method("clear");
        c.clear();

        mock.expects(once()).method("containsKey").with(eq(0)).will(returnValue(true));
        assertTrue(c.containsKey(0));

        mock.expects(once()).method("containsValue").with(eq(0)).will(returnValue(false));
        assertFalse(c.containsValue(0));

        mock.expects(once()).method("entrySet").will(returnValue(dummyMap.entrySet()));
        assertSame(dummyMap.entrySet(), c.entrySet());

        c.evict(); // ignore

        mock.expects(once()).method("get").with(eq(0)).will(returnValue(1));
        assertEqual(1, c.get(0));

        try {
            c.getEntry(1);
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }
        try {
            c.getEventBus();
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }

        mock.expects(once()).method("hashCode").will(returnValue(123));
        assertEquals(123, c.hashCode());

        mock.expects(once()).method("isEmpty").will(returnValue(false));
        assertFalse(c.isEmpty());

        mock.expects(once()).method("keySet").will(returnValue(dummyMap.keySet()));
        assertSame(dummyMap.keySet(), c.keySet());

        try {
            c.load(1);
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }

        try {
            c.loadAll(Arrays.asList(1, 4));
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }

        mock.expects(once()).method("get").with(eq(0)).will(returnValue(1));
        assertEqual(1, c.peek(0));

        try {
            c.peekEntry(1);
            shouldThrow();
        } catch (UnsupportedOperationException e) { /* okay */
        }

        mock.expects(once()).method("put").with(eq(0), eq(1)).will(
                returnValue(Integer.valueOf(2)));
        assertEqual(2, c.put(0, 1));

        mock.expects(once()).method("put").with(eq(1), eq(2)).will(
                returnValue(Integer.valueOf(3)));
        assertEqual(3, c.put(1, 2, 4, TimeUnit.MICROSECONDS));

        mock.expects(once()).method("putAll").with(eq(dummyMap));
        c.putAll(dummyMap);

        mock.expects(once()).method("putAll").with(eq(dummyMap));
        c.putAll(dummyMap, 1, TimeUnit.MICROSECONDS);

        mock.expects(once()).method("remove").with(eq(0)).will(returnValue(1));
        assertEqual(1, c.remove(0));

        c.resetStatistics(); // ignore

        mock.expects(once()).method("size").will(returnValue(2));
        assertEquals(2, c.size());

        mock.expects(once()).method("toString").will(returnValue("foo"));
        assertEquals("foo", c.toString());

        mock.expects(once()).method("values").will(returnValue(dummyMap.values()));
        assertSame(dummyMap.values(), c.values());

        /* */
        // c.putIfAbsent()
        //        
        // c.remove()
        //        
        // c.replace()
        //        
        // c.replace()
    }

    @Test
    public void testConcurrentMapMethods() {
        Map<Integer, Integer> dummyMap = new HashMap<Integer, Integer>();

        Mock mock = mock(Map.class);
        Cache<Integer, Integer> c = Caches.mapToCache((Map) mock.proxy());

        /* Put If Absent*/
        mock.expects(once()).method("containsKey").with(eq(1)).will(returnValue(true));
        mock.expects(once()).method("get").with(eq(1)).will(returnValue(3));
        assertEquals(3, c.putIfAbsent(1, 2).intValue());
        
        mock.expects(once()).method("containsKey").with(eq(2)).will(returnValue(false));
        mock.expects(once()).method("put").with(eq(2), eq(3)).will(
                returnValue(Integer.valueOf(5)));
        assertEquals(5, c.putIfAbsent(2, 3).intValue());
        
    }

    @Test
    public void testEquals() {
        // we test this separately because we would get a StackOverFlow
        // exception
        // if we used a mock
    }
}
