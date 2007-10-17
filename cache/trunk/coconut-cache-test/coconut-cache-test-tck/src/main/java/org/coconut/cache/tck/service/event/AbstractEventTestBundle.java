/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.event;

import static org.coconut.test.CollectionUtils.asMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.core.EventProcessor;
import org.coconut.event.EventSubscription;
import org.coconut.filter.Filter;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class AbstractEventTestBundle extends AbstractCacheTCKTest {

    static final CacheConfiguration<Integer, String> INCLUDE_ALL_CONFIGURATION;

    static {
        INCLUDE_ALL_CONFIGURATION = CacheConfiguration.create();
        INCLUDE_ALL_CONFIGURATION.event().setEnabled(true).include(CacheEvent.class);
    }

    LinkedBlockingQueue<EventWrapper> events;

    private EventProcessor<CacheEvent<Integer, String>> eventHandler;

    private EventWrapper ev;

    static CacheConfiguration<Integer, String> includeAll() {
        CacheConfiguration<Integer, String> c = CacheConfiguration.create();
        c.event().setEnabled(true).include(CacheEvent.class);
        return c;
    }

    protected int getPendingEvents() {
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
            throw new AssertionFailedError("event queue was not empty " + events);
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

    protected EventSubscription<?> subscribe(Filter f) {
        EventSubscription s = c.getService(CacheEventService.class).subscribe(
                eventHandler, f);
        assertNotNull(s);
        return s;
    }

    protected void consumeItem() throws Exception {
        assertNotNull(events.poll(50, TimeUnit.MILLISECONDS));
    }

    protected <S extends CacheEvent> S consumeItem(Cache c, Class<S> type) {
        EventWrapper ew;
        try {
            ew = ev != null ? ev : events.poll(50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
            throw new IllegalStateException("Thread was interrupted", ie);
        }
        if (ew == null) {
            throw new IllegalStateException("No event of type " + type + " was posted");
        }
        CacheEvent<?, ?> event = ew.event;
        ev = null;
        if (event == null) {
            fail("No events was delivered ");
        }
        assertTrue("expected type " + type + ", but got type " + event.getClass(), type
                .isAssignableFrom(event.getClass()));
        assertEquals(c, event.getCache());
        // name did not match this is mostlike because the items
        // class.NAME did not match its type
        try {
            assertEquals(type.getDeclaredField("NAME").get(null), event.getName());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        event.toString(); // just test that it doesn't fail
        return (S) event;
    }

    protected void assertQueueEmpty() {
        if (events.size() > 0) {
            events.clear();
            throw new AssertionFailedError("event queue not empty");
        }
    }

    protected Integer peekKey() throws InterruptedException {
        ev = events.poll(1, TimeUnit.SECONDS);
        return ((CacheEntryEvent<Integer, String>) ev.event).getKey();
    }

    protected <S extends CacheEntryEvent> S consumeItem(Class<S> type,
            Map.Entry<Integer, String> entry) {
        return consumeItem(type, entry.getKey(), entry.getValue());
    }

    protected <S extends CacheEntryEvent> S consumeItem(Class<S> type, Integer key,
            String value) {
        S event = consumeItem(c, type);
        assertEquals(key, event.getKey());
        assertEquals(value, event.getValue());
        return event;
    }

    protected <S extends CacheEntryEvent> java.util.Collection<S> consumeItems(
            Class<S> type, Map.Entry<Integer, String>... entries) throws Exception {
        Map<Integer, S> result = new HashMap<Integer, S>();
        Map<Integer, String> map = asMap(entries);
        while (!map.isEmpty()) {
            Integer key = peekKey();
            String value = map.get(key);
            if (value == null) {
                throw new IllegalStateException("Could not find item for key = " + key);
            }
            S event = consumeItem(c, type);
            assertEquals(key, event.getKey());
            assertEquals(value, event.getValue());
            result.put(key, event);
            map.remove(key);
        }
        if (result.size() == map.size()) {
            throw new IllegalStateException("sizes differ");
        }
        return result.values();
    }

}
