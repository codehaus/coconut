/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.bus.util;

import java.util.Collection;

import org.coconut.core.EventHandler;
import org.coconut.event.bus.EventBus;
import org.coconut.event.bus.EventSubscription;
import org.coconut.filter.Filter;

/**
 * Hmm kind of special decorator where its not possible to control the input of
 * the bus only the output, that is why we need to create the decorated
 * subscribers.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EventBusDecorator<T> implements EventBus<T> {

    private final EventBus<T> bus;

    public EventBusDecorator(EventBus<T> bus) {
        this.bus = bus;
    }

    public Collection<EventSubscription<T>> getSubscribers() {
        return bus.getSubscribers();
    }

    public boolean offer(T element) {
        return bus.offer(element);
    }

    public boolean offerAll(T... events) {
        return bus.offerAll(events);
    }

    public void handle(T event) {
        bus.handle(event);
    }

    public EventSubscription<T> subscribe(EventHandler<? super T> listener,
            Filter<? super T> filter, String prefix) {
        return bus.subscribe(listener, filter, prefix);
    }

    public EventSubscription<T> subscribe(EventHandler<? super T> listener,
            Filter<? super T> filter) {
        return bus.subscribe(listener, filter);
    }

    public EventSubscription<T> subscribe(EventHandler<? super T> eventHandler) {
        return bus.subscribe(eventHandler);
    }

    public Collection<EventSubscription<T>> unsubscribeAll() {
        return bus.unsubscribeAll();
    }

    protected void decorateEvent(T event, EventSubscription<T> s) {
        s.getEventHandler().handle(event);
    }

    class DecoratedSubscription implements EventSubscription<T>, EventHandler<T> {
        EventSubscription s;

        public void cancel() {
            s.cancel();
        }

        public EventHandler<? super T> getEventHandler() {
            return this;
        }

        public Filter<? super T> getFilter() {
            return s.getFilter();
        }

        public String getName() {
            return s.getName();
        }

        DecoratedSubscription(EventSubscription<T> s) {
            this.s = s;
        }

        /**
         * @see org.coconut.core.EventHandler#handle(E)
         */
        public void handle(T event) {
            decorateEvent(event, s);
        }

    }

}
