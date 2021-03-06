/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.internal.service.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.test.TestUtil;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class EventDispatcherTest {

    Cache<?, ?> c;

    BlockingQueue<CacheEvent<Integer, String>> events;

 
    protected void setUp() throws Exception {
        c = TestUtil.dummy(Cache.class);
        events = new LinkedBlockingQueue<CacheEvent<Integer, String>>();
    }

    private <S> S consumeItem(Class<? extends CacheEvent> type, long sequenceId) {
        try {
            CacheEvent<Integer, String> event = events.poll(1, TimeUnit.SECONDS);
            if (event == null) {
                throw new NullPointerException("event is null");
            }
            assertTrue(type.isAssignableFrom(event.getClass()));
            assertSame(c, event.getCache());
            assertEquals(type.getDeclaredField("NAME").get(null), event.getName());
            // assertEquals(sequenceId, event.getSequenceID());
            event.toString(); // just test that it doesn't fail
            return (S) event;
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread was interrupted", e);
        } catch (SecurityException e) {
            throw new IllegalStateException("SecurityException", e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("NoSuchFieldException", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IllegalAccessException", e);
        }
    }

// private <S> S consumeItem(Class<? extends CacheEntryEvent> type,
// Integer key, String value, long sequenceId) {
// CacheEntryEvent<?,?> event = consumeItem(type, sequenceId);
// assertEquals(key, event.getKey());
// assertEquals(value, event.getValue());
// return (S) event;
// }

    @Test
    public void testNoTests() {

    }

}
