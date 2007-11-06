/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;
import org.coconut.predicate.CollectionPredicates;
import org.coconut.predicate.Predicate;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Events.java 212 2007-01-30 10:03:55Z kasper $
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

    public static <E> Runnable offerAsRunnable(final E element,
            final Offerable<? super E> o) {
        return new Runnable() {
            public void run() {
                o.offer(element);
            }
        };
    }

//    public static <E> Runnable offerAsRunnable(final Callable<E> c,
//            final Offerable<? super E> o) {
//        return new Runnable() {
//            public void run() {
//                try {
//                    E e = c.call();
//                    o.offer(e);
//                } catch (Exception e) {
//                    throw new EventBusException(e);
//                }
//            }
//
//        };
//    }

    public static <E> Callable<E> asCallable(E element) {
        return new StaticCallable<E>(element);
    }

    static class StaticCallable<E> implements Callable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6497274083600963110L;

        /** The object to return on each invocation of call. */
        private final E object;

        public StaticCallable(final E object) {
            if (object == null) {
                throw new NullPointerException("object is null");
            }
            this.object = object;
        }

        public E call() {
            return object;
        }
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
