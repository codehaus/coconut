/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.eventbus;

import static org.coconut.test.CollectionUtils.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheItemEvent;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.core.EventHandler;
import org.coconut.core.EventHandlers;
import org.coconut.event.bus.Subscription;
import org.coconut.filter.Filter;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class AbstractEventTestBundle extends CacheTestBundle {
    private LinkedBlockingQueue<CacheEvent<Integer, String>> events;

    private EventHandler<CacheEvent<Integer, String>> eventHandler;

    private long prevId;

    private CacheEvent<?, ?> ev;

    int getPendingEvents() {
        return ev == null ? events.size() : events.size() + 1;
    }

    @Before
    public void setUpEvent() {
        events = new LinkedBlockingQueue<CacheEvent<Integer, String>>();
        eventHandler = EventHandlers.fromQueue(events);
    }

    @After
    public void stop() {
        if (!events.isEmpty()) {
            while (!events.isEmpty()) {
                System.out.println("Pending event: " + events.poll());
            }
            throw new AssertionFailedError("event queue was not empty");
        }
    }

    Subscription subscribe(Filter f) {
        Subscription s = c.getEventBus().subscribe(eventHandler, f);
        assertNotNull(s);
        return s;
    }

    protected boolean checkStrict() {
        return false;
    }

    void consumeItem() throws Exception {
        assertNotNull(events.poll(50, TimeUnit.MILLISECONDS));
    }

    <S extends CacheEvent> S consumeItem(Cache c, Class<S> type)
            throws Exception {
        CacheEvent<?, ?> event = ev != null ? ev : events.poll(50,
                TimeUnit.MILLISECONDS);
        ev = null;
        if (event == null) {
            fail("No events was delivered ");
        }
        assertTrue(type.isAssignableFrom(event.getClass()));
        assertEquals(c, event.getCache());
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
        return ((CacheItemEvent<Integer, String>) ev).getKey();
    }

    <S extends CacheItemEvent> S consumeItem(Class<S> type,
            Map.Entry<Integer, String> entry) throws Exception {
        return consumeItem(type, entry.getKey(), entry.getValue());
    }

    <S extends CacheItemEvent> S consumeItem(Class<S> type, Integer key,
            String value) throws Exception {
        S event = consumeItem(c, type);
        assertEquals(key, event.getKey());
        assertEquals(value, event.getValue());
        return (S) event;
    }

    void consumeItems(Class<? extends CacheItemEvent> type,
            Map.Entry<Integer, String>... entries) throws Exception {
        Map<Integer, String> map = asMap(entries);

        while (!map.isEmpty()) {
            Integer key = peekKey();
            String value = map.get(key);
            assertNotNull(value);
            CacheItemEvent<?, ?> event = consumeItem(c, type);
            assertEquals(key, event.getKey());
            assertEquals(value, event.getValue());
            map.remove(key);
        }
    }

}
