/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.eventbus;

import static org.coconut.test.CollectionUtils.asMap;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.core.EventProcessor;
import org.coconut.event.EventSubscription;
import org.coconut.filter.Filter;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class AbstractEventTestBundle extends CacheTestBundle {
    LinkedBlockingQueue<EventWrapper> events;

    private EventProcessor<CacheEvent<Integer, String>> eventHandler;

    private long prevId;

    private EventWrapper ev;

    int getPendingEvents() {
        return ev == null ? events.size() : events.size() + 1;
    }

    @Before
    public void setUpEvent() {
        events = new LinkedBlockingQueue<EventWrapper>();
        eventHandler = new EventProcessor<CacheEvent<Integer, String>>() {
            public void process(CacheEvent<Integer, String> event) {
                events.add(new EventWrapper(event));
            }
        };
    }

    @After
    public void stop() {
        if (!events.isEmpty()) {
            while (!events.isEmpty()) {
                EventWrapper ew = events.poll();
                System.err.println("Pending event: " + ew.event);
                ew.toErr();
            }
            throw new AssertionFailedError("event queue was not empty");
        }
    }

    static class EventWrapper {
        CacheEvent<Integer, String> event;

        private StackTraceElement[] elements;

        EventWrapper(CacheEvent<Integer, String> event) {
            this.event = event;
            elements = (new Exception()).fillInStackTrace().getStackTrace();
        }

        CacheEvent<Integer, String> event() {
            return event;
        }

        public void toErr() {
            Exception e = new Exception();
            e.setStackTrace(elements);
            e.printStackTrace(System.err);
        }
    }

    EventSubscription subscribe(Filter f) {
        EventSubscription s = c.getService(CacheEventService.class).getEventBus()
                .subscribe(eventHandler, f);
        assertNotNull(s);
        return s;
    }

    protected boolean checkStrict() {
        return false;
    }

    void consumeItem() throws Exception {
        assertNotNull(events.poll(50, TimeUnit.MILLISECONDS));
    }

    <S extends CacheEvent> S consumeItem(Cache c, Class<S> type) throws Exception {
        EventWrapper ew = ev != null ? ev : events.poll(50, TimeUnit.MILLISECONDS);
        CacheEvent<?, ?> event = ew.event;
        ev = null;
        if (event == null) {
            fail("No events was delivered ");
        }
        assertTrue(type.isAssignableFrom(event.getClass()));
        assertEquals(c, event.getCache());
        // name did not match this is mostlike because the items
        // class.NAME did not match its type
        assertEquals(type.getDeclaredField("NAME").get(null), event.getName());
        if (checkStrict()) {
            assertEquals(prevId + 1, event.getSequenceID());
        } else {
            assertTrue(event.getSequenceID() > prevId);
        }

        prevId = event.getSequenceID();
        event.toString(); // just test that it doesn't fail
        return (S) event;
    }

    Integer peekKey() throws InterruptedException {
        ev = events.poll(1, TimeUnit.SECONDS);
        return ((CacheEntryEvent<Integer, String>) ev.event).getKey();
    }

    <S extends CacheEntryEvent> S consumeItem(Class<S> type,
            Map.Entry<Integer, String> entry) throws Exception {
        return consumeItem(type, entry.getKey(), entry.getValue());
    }

    <S extends CacheEntryEvent> S consumeItem(Class<S> type, Integer key, String value)
            throws Exception {
        S event = consumeItem(c, type);
        assertEquals(key, event.getKey());
        assertEquals(value, event.getValue());
        return (S) event;
    }

    void consumeItems(Class<? extends CacheEntryEvent> type,
            Map.Entry<Integer, String>... entries) throws Exception {
        Map<Integer, String> map = asMap(entries);

        while (!map.isEmpty()) {
            Integer key = peekKey();
            String value = map.get(key);
            assertNotNull(value);
            CacheEntryEvent<?, ?> event = consumeItem(c, type);
            assertEquals(key, event.getKey());
            assertEquals(value, event.getValue());
            map.remove(key);
        }
    }

}
