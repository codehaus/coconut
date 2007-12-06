/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class InternalEventTest {

    @Test
    public void cleared() {
        Cache<Integer, String> cache = TestUtil.dummy(Cache.class);
        CacheEvent.CacheCleared<Integer, String> c = InternalEvent.cleared(cache, 1, 2);
        assertSame(cache, c.getCache());
        assertEquals(1, c.getPreviousSize());
        assertEquals(2l, c.getPreviousVolume());
        assertEquals(CacheEvent.CacheCleared.NAME, c.getName());
        assertEquals(c, c);
        assertFalse(c.equals(InternalEvent.cleared(cache, 1, 3)));
        assertFalse(c.equals(InternalEvent.cleared(cache, 2, 2)));
        assertFalse(c.equals(InternalEvent.cleared(TestUtil.dummy(Cache.class), 1, 2)));
        c.toString();
        assertEquals(InternalEvent.cleared(cache, 1, 2).hashCode(), InternalEvent.cleared(cache, 1,
                2).hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void clearedNPE() {
        InternalEvent.cleared(null, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clearedIAE() {
        InternalEvent.cleared(TestUtil.dummy(Cache.class), -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clearedIAE2() {
        InternalEvent.cleared(TestUtil.dummy(Cache.class), 0, -1);
    }

    @Test
    public void started() {
        Cache<Integer, String> cache = CacheConfiguration.<Integer, String> create("foo")
                .newCacheInstance(UnsynchronizedCache.class);
        CacheEvent.CacheStarted<Integer, String> c = InternalEvent.started(cache);
        assertSame(cache, c.getCache());
        assertEquals(CacheEvent.CacheStarted.NAME, c.getName());

        assertEquals(c, c);
        assertFalse(c.equals(new Object()));
        assertFalse(c.equals(InternalEvent.started(TestUtil.dummy(Cache.class))));
        c.toString();
        assertEquals(InternalEvent.started(cache).hashCode(), InternalEvent.started(cache)
                .hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void startedNPE() {
        InternalEvent.started(null);
    }

}
