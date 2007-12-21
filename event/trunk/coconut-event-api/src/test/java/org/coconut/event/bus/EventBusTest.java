/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus;

import java.util.Collection;

import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;
import org.junit.Test;

/**
 * Tests the signature of the {@link EventBus} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheConfigurationTest.java 475 2007-11-20 17:22:26Z kasper $
 */
@SuppressWarnings( { "unchecked", "unused" })
public class EventBusTest {

    protected EventBus<Number> createNew() {
        return new DummyEventBus<Number>();
    }

    @Test
    public void testSubscribe() {
        EventBus<Number> bus = createNew();

        Procedure<Integer> hi = null;
        Procedure<Number> hn = null;
        Procedure<Object> ho = null;
        Procedure hall = null;
        Procedure<String> hs = null;

        // bus.subscribe(hi, Filters.clazz(Integer.class));
        bus.subscribe(hn, Predicates.isType(Integer.class));
        bus.subscribe(ho, Predicates.isType(Integer.class));
        bus.subscribe(hall, Predicates.isType(Integer.class));
        // bus.subscribe(hs, Filters.clazz(Integer.class));
    }
    @Test
    public void testSubscribeFilter() {
        EventBus<Number> bus = createNew();

        Procedure<Object> o = null;
        Predicate<Integer> hi = null;
        Predicate<Number> hn = null;
        Predicate<Object> ho = null;
        Predicate hall = null;
        Predicate<String> hs = null;

        // bus.subscribe(o, hi); //shouldn't work
        bus.subscribe(o, hn);
        bus.subscribe(o, ho);
        bus.subscribe(o, hall);
        // bus.subscribe(o, hs); //shouldn't work
    }

    static class DummyEventBus<E> implements EventBus<E> {

        public Collection<EventSubscription<E>> getSubscribers() {
            return null;
        }

        public boolean offerAll(Collection<? extends E> events) {
            return false;
        }

        public EventSubscription<E> subscribe(Procedure<? super E> eventHandler) {
            return null;
        }

        public EventSubscription<E> subscribe(Procedure<? super E> eventHandler,
                Predicate<? super E> filter) {
            return null;
        }

        public EventSubscription<E> subscribe(Procedure<? super E> listener,
                Predicate<? super E> filter, String name) {
            return null;
        }

        public Collection<EventSubscription<E>> unsubscribeAll() {
            return null;
        }

        public boolean offer(E element) {
            return false;
        }

        public void apply(E event) {}

    }
}
