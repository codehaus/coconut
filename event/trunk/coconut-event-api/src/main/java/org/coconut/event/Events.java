/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import java.io.Serializable;
import java.util.Collection;

import org.coconut.core.EventProcessor;
import org.coconut.predicate.CollectionPredicates;
import org.coconut.predicate.Predicate;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Events {


    /**
     * Returns a filter that can be used to filter a particular EventHandler.
     * <p>
     * 
     * <pre>
     * public void useIt() {
     *     EventBus eb;
     *     EventHandler lookFor;
     *     Filters.filter(eb.getSubscribers(), Bus.getListenerFilter(lookFor));
     * }
     * </pre>
     * 
     * As an alternative findSubscribers can be used.
     * 
     * @param eventHandler
     *            the EventHandler to filter on
     */
    public static <E> Predicate<EventSubscription<E>> getListenerFilter(
            EventProcessor<E> eventHandler) {
        return new EventListenerFilter<E>(eventHandler);
    }

    public static <E> Predicate<EventSubscription<E>> getEventTypeFilter(E event) {
        return new EventMatchFilter<E>(event);
    }

    public static <E> Collection<EventSubscription<E>> findSubscribers(
            EventBus<E> bus, EventProcessor<E> eventProcessor) {
        if (bus == null) {
            throw new NullPointerException("bus is null");
        } else if (eventProcessor == null) {
            throw new NullPointerException("eventProcessor is null");
        }
        return findSubscribers(bus, new EventListenerFilter<E>(eventProcessor));
    }

    /**
     * Returns a collection of all the subscribers that would recieve a
     * specified event if it was posted to the eventbus.
     * 
     */
    public static <E> Collection<EventSubscription<E>> matchSubscriberEvent(
            EventBus<E> bus, E event) {
        return findSubscribers(bus, new EventMatchFilter<E>(event));
    }

    public static <E> Collection<EventSubscription<E>> findSubscribers(
            EventBus<E> bus, Predicate<EventSubscription<E>> filter) {
        return CollectionPredicates.filter(bus.getSubscribers(), filter);
    }

    static class EventListenerFilter<E> implements Predicate<EventSubscription<E>>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 4194707549593512350L;

        /** The event-handler we are looking for. */
        private final EventProcessor<E> handler;

        /**
         * @param handler
         */
        EventListenerFilter(final EventProcessor<E> eventHandler) {
            if (eventHandler == null) {
                throw new NullPointerException("eventHandler is null");
            }
            this.handler = eventHandler;
        }

        /**
         * @see org.coconut.predicate.Predicate#evaluate(java.lang.Object)
         */
        public boolean evaluate(EventSubscription<E> element) {
            return handler.equals(element.getEventProcessor());
        }
    }

    private static class EventMatchFilter<E> implements Predicate<EventSubscription<E>> {
        private final E event;

        /**
         * @param event
         */
        public EventMatchFilter(final E event) {
            this.event = event;
        }

        /**
         * @see org.coconut.predicate.Predicate#evaluate(java.lang.Object)
         */
        public boolean evaluate(EventSubscription<E> element) {
            return element.getFilter().evaluate(event);
        }
    }
}
