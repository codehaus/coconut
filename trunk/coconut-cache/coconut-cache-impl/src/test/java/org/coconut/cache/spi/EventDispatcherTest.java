/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheEntryEvent;
import org.coconut.core.EventProcessor;
import org.coconut.core.EventUtils;
import org.coconut.test.MockTestCase;

@SuppressWarnings("unused")
public class EventDispatcherTest extends MockTestCase {

    Cache c;

    BlockingQueue<CacheEvent<Integer, String>> events;

    EventProcessor<CacheEvent<Integer, String>> eventHandler;

    protected void setUp() throws Exception {
        super.setUp();
        c = (Cache) mock(Cache.class).proxy();
        events = new LinkedBlockingQueue<CacheEvent<Integer, String>>();
        eventHandler = EventUtils.fromQueue(events);
    }

    private <S> S consumeItem(Class<? extends CacheEvent> type, long sequenceId) {
        try {
            CacheEvent event = events.poll(1, TimeUnit.SECONDS);
            if (event == null) {
                throw new NullPointerException("event is null");
            }
            assertTrue(type.isAssignableFrom(event.getClass()));
            assertEquals(c, event.getCache());
            assertEquals(type.getDeclaredField("NAME").get(null), event
                    .getName());
            assertEquals(sequenceId, event.getSequenceID());
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

    private <S> S consumeItem(Class<? extends CacheEntryEvent> type,
            Integer key, String value, long sequenceId) {
        CacheEntryEvent<?,?> event = consumeItem(type, sequenceId);
        assertEquals(key, event.getKey());
        assertEquals(value, event.getValue());
        return (S) event;
    }
    
    public void testNoTests() {
        
    }
    

}
