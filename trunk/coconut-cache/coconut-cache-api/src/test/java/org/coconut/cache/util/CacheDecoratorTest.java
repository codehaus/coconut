/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheQuery;
import org.coconut.cache.Cache.HitStat;
import org.coconut.event.EventBus;
import org.coconut.filter.Filter;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

public class CacheDecoratorTest extends MockTestCase {

    public void testNullConstructor() {
        try {
            new CacheDecorator<Long, Long>(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testDecorator() {
        EventBus eventBus = mockDummy(EventBus.class);
        HitStat hitStat = mockDummy(HitStat.class);
        Map map = new HashMap();
        Future future = mockDummy(Future.class);
        Collection<Integer> col = new LinkedList<Integer>();
        Collection<String> cols = new LinkedList<String>();
        Set set = mockDummy(Set.class);
        ReadWriteLock lock = mockDummy(ReadWriteLock.class);
        CacheEntry ce = mockDummy(CacheEntry.class);
        //CacheQuery cq = mockDummy(CacheQuery.class);
        Filter f = mockDummy(Filter.class);

        Mock m = mock(Cache.class);

        m.expects(once()).method("clear");
        m.expects(once()).method("containsKey").with(eq(1)).will(returnValue(true));
        m.expects(once()).method("containsValue").with(eq("A")).will(returnValue(false));
        m.expects(once()).method("entrySet").will(returnValue(set));
        m.expects(once()).method("equals").with(eq("B")).will(returnValue(false));
        m.expects(once()).method("evict");
        m.expects(once()).method("get").with(eq(2)).will(returnValue("C"));
        m.expects(once()).method("getAll").with(same(col)).will(returnValue(map));
        m.expects(once()).method("getEventBus").will(returnValue(eventBus));
        m.expects(once()).method("getHitStat").will(returnValue(hitStat));

        m.expects(once()).method("hashCode").will(returnValue(3));
        m.expects(once()).method("isEmpty").will(returnValue(true));
        m.expects(once()).method("entrySet").will(returnValue(set));
        m.expects(once()).method("keySet").will(returnValue(set));
        m.expects(once()).method("loadAsync").with(eq(4)).will(returnValue(future));
        m.expects(once()).method("loadAllAsync").with(same(col)).will(returnValue(future));
        m.expects(once()).method("peek").with(eq(5)).will(returnValue("D"));

        m.expects(once()).method("put").with(eq(6), eq("E")).will(returnValue("F"));
        m.expects(once()).method("put").with(eq(7), eq("G"), eq(8l), same(TimeUnit.MILLISECONDS))
                .will(returnValue("H"));

        m.expects(once()).method("putAll").with(same(map));
        m.expects(once()).method("putAll").with(same(map), eq(9l), same(TimeUnit.SECONDS));
        m.expects(once()).method("putIfAbsent").with(eq(8), eq("I")).will(returnValue("J"));
        m.expects(once()).method("remove").with(eq(9), eq("J")).will(returnValue(true));
        m.expects(once()).method("remove").with(eq(10)).will(returnValue(true));
        m.expects(once()).method("replace").with(eq(11), eq("K"), eq("L")).will(returnValue(true));
        m.expects(once()).method("replace").with(eq(12), eq("M")).will(returnValue("N"));
        m.expects(once()).method("resetStatistics");
        m.expects(once()).method("size").will(returnValue(13));
        m.expects(once()).method("values").will(returnValue(col));
        m.expects(once()).method("getLock").with(eq(new Integer[] { 14, 15 })).will(
                returnValue(lock));
    
        
        m.expects(once()).method("toString").will(returnValue("ttt"));
        //m.expects(once()).method("query").with(eq(f)).will(returnValue(cq));
        m.expects(once()).method("getEntry").with(eq(16)).will(returnValue(ce));

        Cache<Integer, String> c = new CacheDecorator((Cache<Integer, String>) m.proxy());

        c.clear();
        assertEquals(true, c.containsKey(1));
        assertEquals(false, c.containsValue("A"));
        assertSame(set, c.entrySet());
        assertEquals(false, c.equals("B"));
        c.evict();
        assertEquals("C", c.get(2));
        assertSame(map, c.getAll(col));
        assertSame(eventBus, c.getEventBus());
        assertSame(hitStat, c.getHitStat());

        assertEquals(3, c.hashCode());
        assertTrue(c.isEmpty());
        assertSame(set, c.entrySet());
        assertSame(set, c.keySet());
        assertSame(future, c.loadAsync(4));
        assertSame(future, c.loadAllAsync(col));
        assertEquals("D", c.peek(5));
        assertEquals("F", c.put(6, "E"));
        assertEquals("H", c.put(7, "G", 8, TimeUnit.MILLISECONDS));
        c.putAll(map);
        c.putAll(map, 9, TimeUnit.SECONDS);
        assertEquals("J", c.putIfAbsent(8, "I"));
        assertEquals(true, c.remove(9, "J"));
        assertEquals(true, c.remove(10));
        assertEquals(true, c.replace(11, "K", "L"));
        assertEquals("N", c.replace(12, "M"));
        c.resetStatistics();
        assertEquals(13, c.size());
        assertEquals(cols, c.values());
        //assertEquals(lock, c.getLock(14, 15));
        assertEquals("ttt", c.toString());
        //assertEquals(cq, c.query(f));
        assertEquals(ce, c.getEntry(16));
    }

    // @SuppressWarnings("serial")
    // static class Decorator extends CacheDecorator<Integer, String> {
    // public Decorator(Cache<Integer, String> cache) {
    // super(cache);
    // }
    // }
}
