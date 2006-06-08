package org.coconut.cache.spi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheItemEvent;
import org.coconut.core.EventHandler;
import org.coconut.core.EventHandlers;
import org.coconut.test.MockTestCase;

@SuppressWarnings("unused")
public class EventDispatcherTest extends MockTestCase {

    Cache c;

    BlockingQueue<CacheEvent<Integer, String>> events;

    EventHandler<CacheEvent<Integer, String>> eventHandler;

    protected void setUp() throws Exception {
        super.setUp();
        c = (Cache) mock(Cache.class).proxy();
        events = new LinkedBlockingQueue<CacheEvent<Integer, String>>();
        eventHandler = EventHandlers.fromQueue(events);
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

    private <S> S consumeItem(Class<? extends CacheItemEvent> type,
            Integer key, String value, long sequenceId) {
        CacheItemEvent<?,?> event = consumeItem(type, sequenceId);
        assertEquals(key, event.getKey());
        assertEquals(value, event.getValue());
        return (S) event;
    }
    
    public void testNoTests() {
        
    }
    

}