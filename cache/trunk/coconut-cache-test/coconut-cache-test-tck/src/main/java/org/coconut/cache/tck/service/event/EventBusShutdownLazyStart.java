/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.core.EventProcessor;
import org.coconut.core.EventUtils;
import org.coconut.event.bus.EventSubscription;
import org.coconut.predicate.Predicates;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

public class EventBusShutdownLazyStart extends AbstractEventTestBundle {

    @Before
    public void init() {
        c = newCache(newConf().event().setEnabled(true));
    }

    @Test
    public void unsubscribeAllOnShutdown() {
        EventSubscription es = event().subscribe(EventUtils.ignoreEventProcessor());
        assertTrue(es.isValid());
        assertEquals(1, event().getSubscribers().size());
        shutdownAndAwait();
        assertFalse(es.isValid());
        assertEquals(0, event().getSubscribers().size());
    }

    @Test(expected = IllegalStateException.class)
    public void subscribe1Shutdown() {
        prestart();
        c.shutdown();
        event().subscribe(MockTestCase.mockDummy(EventProcessor.class));
    }

    @Test
    public void subscribe1LazyStart() {
        assertFalse(c.isStarted());
        event().subscribe(MockTestCase.mockDummy(EventProcessor.class));
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void subscribe2Shutdown() {
        prestart();
        c.shutdown();
        event().subscribe(MockTestCase.mockDummy(EventProcessor.class), Predicates.TRUE);
    }

    @Test
    public void subscribe2LazyStart() {
        assertFalse(c.isStarted());
        event().subscribe(MockTestCase.mockDummy(EventProcessor.class), Predicates.TRUE);
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void subscribe3Shutdown() {
        prestart();
        c.shutdown();
        event().subscribe(MockTestCase.mockDummy(EventProcessor.class), Predicates.TRUE, "foo");
    }

    @Test
    public void subscribe3LazyStart() {
        assertFalse(c.isStarted());
        event().subscribe(MockTestCase.mockDummy(EventProcessor.class), Predicates.TRUE, "foo");
        assertTrue(c.isStarted());
    }

    @Test
    public void unsubscribeShutdown() {
        prestart();
        c.shutdown();
        event().unsubscribeAll();// should not fail
    }

    @Test
    public void unsubscribeLazyStart() {
        assertFalse(c.isStarted());
        event().unsubscribeAll();
        assertTrue(c.isStarted());
    }

    @Test
    public void getSubscribersShutdown() {
        prestart();
        c.shutdown();
        assertEquals(0, event().getSubscribers().size());
    }

    @Test
    public void getSubscribersLazyStart() {
        assertFalse(c.isStarted());
        assertEquals(0, event().getSubscribers().size());
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void offerShutdown() {
        prestart();
        c.shutdown();
        event().offer(MockTestCase.mockDummy(CacheEvent.class));
    }

    @Test
    public void offerLazyStart() {
        assertFalse(c.isStarted());
        event().offer(MockTestCase.mockDummy(CacheEvent.class));
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void processShutdown() {
        prestart();
        c.shutdown();
        event().process(MockTestCase.mockDummy(CacheEvent.class));
    }

    @Test
    public void processLazyStart() {
        assertFalse(c.isStarted());
        event().process(MockTestCase.mockDummy(CacheEvent.class));
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void offerAllShutdown() {
        prestart();
        c.shutdown();
        event().offerAll(
                (Collection) Arrays.asList(MockTestCase.mockDummy(CacheEvent.class), MockTestCase
                        .mockDummy(CacheEvent.class)));
    }

    @Test
    public void offerAllLazyStart() {
        assertFalse(c.isStarted());
        event().offerAll(
                (Collection) Arrays.asList(MockTestCase.mockDummy(CacheEvent.class), MockTestCase
                        .mockDummy(CacheEvent.class)));
        assertTrue(c.isStarted());
    }
}
