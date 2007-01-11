package org.coconut.event.bus;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;
import org.coconut.event.EventSubscription;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * Factory and utility methods for for creating different types of
 * {@link EventBus}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class Bus {

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
    public static <E> Filter<EventSubscription<E>> getListenerFilter(
            EventProcessor<E> eventHandler) {
        return new EventListenerFilter<E>(eventHandler);
    }

    public static <E> Filter<EventSubscription<E>> getEventTypeFilter(E event) {
        return new EventMatchFilter<E>(event);
    }

    public static <E> Collection<EventSubscription<E>> findSubscribers(
            EventBus<E> bus, EventProcessor<E> eventHandler) {
        if (bus == null) {
            throw new NullPointerException("bus is null");
        } else if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        }
        return findSubscribers(bus, new EventListenerFilter<E>(eventHandler));
    }

    /**
     * Returns a collection of all the subscribers that would recieve a
     * specified event if it was posted to the eventbus.
     * 
     * @param bus
     * @param event
     * @return
     */
    public static <E> Collection<EventSubscription<E>> matchSubscriberEvent(
            EventBus<E> bus, E event) {
        return findSubscribers(bus, new EventMatchFilter<E>(event));
    }

    public static <E> Collection<EventSubscription<E>> findSubscribers(
            EventBus<E> bus, Filter<EventSubscription<E>> filter) {
        return Filters.filter(bus.getSubscribers(), filter);
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

    /**
     * Returns a new event bus.
     * 
     * @return a new event bus
     */
    public static <E> EventBus<E> newEventBus() {
        return new DefaultEventBus<E>();
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

    static class EventListenerFilter<E> implements Filter<EventSubscription<E>>,
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
         * @see org.coconut.filter.Filter#accept(java.lang.Object)
         */
        public boolean accept(EventSubscription<E> element) {
            return handler.equals(element.getEventHandler());
        }
    }

    private static class EventMatchFilter<E> implements Filter<EventSubscription<E>> {
        private final E event;

        /**
         * @param event
         */
        public EventMatchFilter(final E event) {
            this.event = event;
        }

        /**
         * @see org.coconut.filter.Filter#accept(java.lang.Object)
         */
        public boolean accept(EventSubscription<E> element) {
            return element.getFilter().accept(event);
        }
    }
}
