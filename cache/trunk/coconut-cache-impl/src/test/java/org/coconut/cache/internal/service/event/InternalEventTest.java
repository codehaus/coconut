package org.coconut.cache.internal.service.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.test.MockTestCase;
import org.junit.Test;

public class InternalEventTest {

    @Test
    public void cleared() {
        Cache<Integer, String> cache = MockTestCase.mockDummy(Cache.class);
        CacheEvent.CacheCleared<Integer, String> c = InternalEvent.cleared(cache, 1, 2);
        assertSame(cache, c.getCache());
        assertEquals(1, c.getPreviousSize());
        assertEquals(2l, c.getPreviousVolume());
        assertEquals(CacheEvent.CacheCleared.NAME, c.getName());
        assertEquals(c, c);
        assertFalse(c.equals(InternalEvent.cleared(cache, 1, 3)));
        assertFalse(c.equals(InternalEvent.cleared(cache, 2, 2)));
        assertFalse(c.equals(InternalEvent.cleared(MockTestCase.mockDummy(Cache.class),
                1, 2)));
        c.toString();
        assertEquals(InternalEvent.cleared(cache, 1, 2).hashCode(), InternalEvent
                .cleared(cache, 1, 2).hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void clearedNPE() {
        InternalEvent.cleared(null, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clearedIAE() {
        InternalEvent.cleared(MockTestCase.mockDummy(Cache.class), -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clearedIAE2() {
        InternalEvent.cleared(MockTestCase.mockDummy(Cache.class), 0, -1);
    }
}