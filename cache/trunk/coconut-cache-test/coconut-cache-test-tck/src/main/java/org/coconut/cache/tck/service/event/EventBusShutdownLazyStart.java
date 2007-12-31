/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.event.bus.EventSubscription;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Procedure;
import org.coconut.test.TestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EventBusShutdownLazyStart extends AbstractEventTestBundle {

    @Before
    public void setupEventBus() {
        init();
    }

    @Test
    public void unsubscribeAllOnShutdown() {
        EventSubscription es = event().subscribe(TestUtil.dummy(Procedure.class));
        assertTrue(es.isValid());
        assertEquals(1, event().getSubscribers().size());
        shutdownAndAwaitTermination();
        assertFalse(es.isValid());
        assertEquals(0, event().getSubscribers().size());
    }

    @Test(expected = IllegalStateException.class)
    public void subscribe1Shutdown() {
        prestart();
        c.shutdown();
        event().subscribe(TestUtil.dummy(Procedure.class));
    }

    @Test
    public void subscribe1LazyStart() {
        assertFalse(c.isStarted());
        event().subscribe(TestUtil.dummy(Procedure.class));
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void subscribe2Shutdown() {
        prestart();
        c.shutdown();
        event().subscribe(TestUtil.dummy(Procedure.class), Predicates.TRUE);
    }

    @Test
    public void subscribe2LazyStart() {
        assertFalse(c.isStarted());
        event().subscribe(TestUtil.dummy(Procedure.class), Predicates.TRUE);
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void subscribe3Shutdown() {
        prestart();
        c.shutdown();
        event().subscribe(TestUtil.dummy(Procedure.class), Predicates.TRUE, "foo");
    }

    @Test
    public void subscribe3LazyStart() {
        assertFalse(c.isStarted());
        event().subscribe(TestUtil.dummy(Procedure.class), Predicates.TRUE, "foo");
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
    @Ignore
    public void offerShutdown() {
        prestart();
        c.shutdown();
//        event().offer(TestUtil.dummy(CacheEvent.class));
    }

    @Ignore
    @Test
    public void offerLazyStart() {
        assertFalse(c.isStarted());
    //    event().offer(TestUtil.dummy(CacheEvent.class));
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void processShutdown() {
        prestart();
        c.shutdown();
        event().apply(TestUtil.dummy(CacheEvent.class));
    }

    @Test
    public void processLazyStart() {
        assertFalse(c.isStarted());
        event().apply(TestUtil.dummy(CacheEvent.class));
        assertTrue(c.isStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void offerAllShutdown() {
        prestart();
        c.shutdown();
        event().offerAll(
                (Collection) Arrays.asList(TestUtil.dummy(CacheEvent.class), TestUtil
                        .dummy(CacheEvent.class)));
    }

    @Test
    public void offerAllLazyStart() {
        assertFalse(c.isStarted());
        event().offerAll(
                (Collection) Arrays.asList(TestUtil.dummy(CacheEvent.class), TestUtil
                        .dummy(CacheEvent.class)));
        assertTrue(c.isStarted());
    }
}
