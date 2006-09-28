package org.coconut.event.bus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.core.EventHandler;
import org.coconut.event.EventSubscription;
import org.coconut.filter.Filter;
import org.coconut.filter.LogicFilters;

/**
 * The order of subscribers are maintained.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class DefaultEventBus<E> implements EventBus<E>, Serializable {

    private final static String subscriptionNamePrefix = "Subscription-";

    private final AtomicLong idGenerator = new AtomicLong();

    private final ConcurrentHashMap<String, DefaultSubscription<E>> list = new ConcurrentHashMap<String, DefaultSubscription<E>>();

    private final AtomicLong size = new AtomicLong();

    final Lock lock = new ReentrantLock();

    public DefaultEventBus() {
    }

    /**
     * @see org.coconut.event.bus.EventBus#getSubscribers()
     */
    @SuppressWarnings("unchecked")
    public List<EventSubscription<E>> getSubscribers() {
        return Collections.unmodifiableList(new ArrayList(list.values()));
    }

    /**
     * @see org.coconut.event.bus.EventBus#unsubscribeAll()
     */
    public Collection<EventSubscription<E>> unsubscribeAll() {
        lock.lock();
        try {
            Collection<EventSubscription<E>> c = new LinkedList<EventSubscription<E>>();
            for (DefaultSubscription<E> s : list.values()) {
                cancel(s);
                c.add(s);
            }
            size.set(0);
            return c;
        } finally {
            lock.unlock();
        }
    }

    public EventSubscription<E> subscribe(EventHandler<? super E> eventHandler) {
        return subscribe(eventHandler, LogicFilters.trueFilter());
    }

    /**
     * @see org.coconut.event.bus.EventBus#subscribe(org.coconut.core.EventHandler,
     *      org.coconut.filter.Filter)
     */
    public EventSubscription<E> subscribe(EventHandler<? super E> eventHandler,
            Filter<? super E> filter) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        lock.lock();
        try {
            for (;;) {
                String name = subscriptionNamePrefix + idGenerator.incrementAndGet();
                DefaultSubscription<E> s = new DefaultSubscription<E>(this, name,
                        eventHandler, filter);
                if (list.putIfAbsent(name, s) == null) {
                    size.incrementAndGet();
                    subscribed(s);
                    return s;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * @see org.coconut.event.bus.EventBus#subscribe(org.coconut.core.EventHandler,
     *      org.coconut.filter.Filter, java.lang.String)
     */
    public EventSubscription<E> subscribe(EventHandler<? super E> eventHandler,
            Filter<? super E> filter, String name) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        } else if (name == null) {
            throw new NullPointerException("name is null");
        }
        lock.lock();
        try {
            DefaultSubscription<E> s = new DefaultSubscription<E>(this, name,
                    eventHandler, filter);
            if (list.putIfAbsent(name, s) != null) {
                throw new IllegalArgumentException("subscription with name '" + name
                        + "' already registered.");
            }
            list.put(name, s);
            size.incrementAndGet();
            subscribed(s);
            return s;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @see org.coconut.core.Offerable#offer(java.lang.Object)
     */
    public boolean offer(final E element) {
        if (element == null) {
            throw new NullPointerException("element is null");
        }
        inform(element);
        return true;
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void handle(E event) {
        if (event == null) {
            throw new NullPointerException("element is null");
        }
        inform(event);
    }

    public boolean offerAll(final E... elements) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == null) {
                throw new NullPointerException("elements contained a null on index = "
                        + i);
            }
        }
        for (E element : elements) {
            inform(element);
        }
        return true;
    }

    protected void inform(final E element) {
        for (EventSubscription<E> s : list.values()) {
            if (s.getFilter().accept(element)) {
                deliver(element, s);
            }
        }
    }

    protected void deliver(final E element, EventSubscription<E> s) {
        try {
            // check if still valid subscription??
            s.getEventHandler().handle(element);
        } catch (RuntimeException e) {
            deliveryFailed(s, element, e);
        }
    }

    protected void deliveryFailed(EventSubscription<E> s, final E element, Throwable cause) {
        try {
            System.err.println("The delivery to " + s.getName()
                    + " failed with the following exception: ");
        } catch (RuntimeException re) {
            re.printStackTrace(System.out);
        }
        // we cancel
        s.cancel();
    }

    /**
     * @see org.coconut.event.bus.defaults.AbstractEventBus#cancel(org.coconut.event.bus.defaults.AbstractEventBus.DefaultSubscription)
     */
    private void cancel(DefaultSubscription<E> s) {
        lock.lock();
        try {
            list.remove(s.name);
            size.incrementAndGet();
        } finally {
            lock.unlock();
        }
        unsubscribed(s);
    }

    protected void unsubscribed(EventSubscription<E> s) {

    }

    protected void subscribed(EventSubscription<E> s) {

    }

    @SuppressWarnings("hiding")
    static class DefaultSubscription<E> implements EventSubscription<E> {
        private final String name;

        private final DefaultEventBus<E> bus;

        private final EventHandler<? super E> destination;

        private final Filter<? super E> filter;

        /**
         * @param destination
         * @param filter
         */
        DefaultSubscription(DefaultEventBus<E> bus, final String name,
                final EventHandler<? super E> destination, final Filter<? super E> filter) {
            this.bus = bus;
            this.name = name;
            this.destination = destination;
            this.filter = filter;
        }

        /**
         * @see org.coconut.event.bus.Subscription#getListener()
         */
        public EventHandler<? super E> getEventHandler() {
            return destination;
        }

        /**
         * @see org.coconut.event.bus.Subscription#getFilter()
         */
        public Filter<? super E> getFilter() {
            return filter;
        }

        /**
         * @see org.coconut.event.bus.Subscription#getName()
         */
        public String getName() {
            return name;
        }

        /**
         * @see org.coconut.event.bus.Subscription#cancel()
         */
        public void cancel() {
            bus.cancel(this);
        }
    }

    // what the fdu is this??
    public interface Marker<E> {
        Filter<E> getFilter(); // returns a filter for events we should keep

        long getNumberOfEventsProcessed();

        void release();
    }

    // subscribeFirst
    // subscribeBefore
    // subscribeLast
}
