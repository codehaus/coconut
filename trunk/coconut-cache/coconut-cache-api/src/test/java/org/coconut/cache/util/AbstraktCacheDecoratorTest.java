package org.coconut.cache.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.Cache.HitStat;
import org.coconut.event.bus.EventBus;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

public class AbstraktCacheDecoratorTest extends MockTestCase {

    public void testDecorator() {
        EventBus eventBus = mockDummy(EventBus.class);
        HitStat hitStat = mockDummy(HitStat.class);
        Map map = new HashMap();
        Future future = mockDummy(Future.class);
        Collection<Integer> col = new LinkedList<Integer>();
        Set set = mockDummy(Set.class);

        Mock m = mock(Cache.class);
        m.expects(once()).method("clear");
        m.expects(once()).method("containsKey").with(eq(1)).will(
                returnValue(true));
        m.expects(once()).method("containsValue").with(eq("A")).will(
                returnValue(false));
        m.expects(once()).method("entrySet").will(returnValue(set));
        m.expects(once()).method("equals").with(eq("B")).will(
                returnValue(false));
        m.expects(once()).method("evict");
        m.expects(once()).method("get").with(eq(2)).will(returnValue("C"));
        m.expects(once()).method("getAll").with(same(col)).will(
                returnValue(map));
        m.expects(once()).method("getEventBus").will(returnValue(eventBus));
        m.expects(once()).method("getHitStat").will(returnValue(hitStat));

        m.expects(once()).method("hashCode").will(returnValue(3));
        m.expects(once()).method("isEmpty").will(returnValue(true));
        m.expects(once()).method("entrySet").will(returnValue(set));
        m.expects(once()).method("keySet").will(returnValue(set));
        m.expects(once()).method("load").with(eq(4)).will(returnValue(future));
        m.expects(once()).method("loadAll").with(same(col)).will(
                returnValue(future));
        m.expects(once()).method("peek").with(eq(5)).will(returnValue("D"));

        m.expects(once()).method("put").with(eq(6), eq("E")).will(
                returnValue("F"));
        m.expects(once()).method("put").with(eq(7), eq("G"), eq(8l),
                same(TimeUnit.MILLISECONDS)).will(returnValue("H"));
        m.expects(once()).method("putAll").with(same(map));
        m.expects(once()).method("putAll").with(same(map), eq(9l),
                same(TimeUnit.SECONDS));

        Cache<Integer, String> c = new Decorator((Cache<Integer, String>) m
                .proxy());

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
        assertSame(future, c.load(4));
        assertSame(future, c.loadAll(col));
        assertEquals("D", c.peek(5));
        assertEquals("F", c.put(6, "E"));
        assertEquals("H", c.put(7, "G", 8, TimeUnit.MILLISECONDS));
        c.putAll(map);
        c.putAll(map, 9, TimeUnit.SECONDS);

        // c.putAll(null);
        // c.putAll(null, 8, TimeUnit.MICROSECONDS);
        // c.putIfAbsent(9, "D");
        // c.remove(10);
        // c.remove(10, "E");
        // c.replace(11, "F", "G");
        // c.replace(12, "H");
        // c.resetStatistics();
        // c.size();
        // c.toString();
        // c.values();

    }

    static class Decorator extends CacheDecorator<Integer, String> {
        private static final long serialVersionUID = 1L; // shut up compiler

        public Decorator(Cache<Integer, String> cache) {
            super(cache);
        }
    }
}
