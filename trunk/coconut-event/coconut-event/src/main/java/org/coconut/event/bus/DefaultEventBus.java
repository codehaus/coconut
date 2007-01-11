package org.coconut.event.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.core.EventProcessor;
import org.coconut.event.EventSubscription;
import org.coconut.filter.Filter;
import org.coconut.filter.LogicFilters;
import org.coconut.filter.matcher.DefaultFilterMatcher;
import org.coconut.filter.matcher.FilterMatcher;

/**
 * The order of subscribers are maintained.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class DefaultEventBus<E> implements EventBus<E> {

    private final static String SUBSCRIPTION_NAME_PREFIX = "Subscription-";

    private final AtomicLong idGenerator = new AtomicLong();

    private final FilterMatcher<DefaultSubscription<E>, E> indexer = new DefaultFilterMatcher<DefaultSubscription<E>, E>();

    private final Lock lock = new ReentrantLock();

    private final ConcurrentHashMap<String, DefaultSubscription<E>> subscribers = new ConcurrentHashMap<String, DefaultSubscription<E>>();

    private final EventBusConfiguration<E> conf;
    // specify indexer, log, thread pool
    // management, ...
    public DefaultEventBus() {
        conf=null;
    }

    /**
     * @see org.coconut.event.bus.EventBus#getSubscribers()
     */
    @SuppressWarnings("unchecked")
    public List<EventSubscription<E>> getSubscribers() {
        return Collections.unmodifiableList(new ArrayList(subscribers.values()));
    }

    /**
     * @see org.coconut.core.EventHandler#handle(java.lang.Object)
     */
    public void process(E event) {
        offer(event);
    }

    /**
     * @see org.coconut.core.Offerable#offer(java.lang.Object)
     */
    public boolean offer(final E element) {
        if (element == null) {
            throw new NullPointerException("element is null");
        }
        return inform(element);
    }

    public boolean offerAll(final E... elements) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] == null) {
                throw new NullPointerException("elements contained a null on index = "
                        + i);
            }
        }
        boolean ok = true;
        for (E element : elements) {
            ok &= inform(element);
        }
        return ok;
    }

    /**
     * @see org.coconut.event.bus.EventBus#subscribe(org.coconut.core.EventHandler)
     */
    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler) {
        return subscribe(eventHandler, LogicFilters.trueFilter());
    }

    /**
     * @see org.coconut.event.bus.EventBus#subscribe(org.coconut.core.EventHandler,
     *      org.coconut.filter.Filter)
     */
    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler,
            Filter<? super E> filter) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        lock.lock();
        try {
            for (;;) {
                String name = SUBSCRIPTION_NAME_PREFIX + idGenerator.incrementAndGet();
                DefaultSubscription<E> s = new DefaultSubscription<E>(this, name,
                        eventHandler, filter);
                // this will only fail if somebody has registered some stage
                // with a specified name starting with SUBSCRIPTION_NAME_PREFIX
                if (subscribers.putIfAbsent(name, s) == null) {
                    indexer.put(s, filter);
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
    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler,
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
            if (subscribers.putIfAbsent(name, s) != null) {
                throw new IllegalArgumentException("subscription with name '" + name
                        + "' already registered.");
            }
            indexer.put(s, filter);
            subscribed(s);
            return s;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @see org.coconut.event.bus.EventBus#unsubscribeAll()
     */
    public Collection<EventSubscription<E>> unsubscribeAll() {
        lock.lock();
        try {
            Collection<EventSubscription<E>> c = new ArrayList<EventSubscription<E>>(
                    subscribers.size());
            for (DefaultSubscription<E> s : subscribers.values()) {
                unsubscribed(s);
            }
            subscribers.clear();
            indexer.clear();
            return c;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @see org.coconut.event.bus.defaults.AbstractEventBus#cancel(org.coconut.event.bus.defaults.AbstractEventBus.DefaultSubscription)
     */
    private void cancel(DefaultSubscription<E> s) {
        lock.lock();
        try {
            subscribers.remove(s.name);
            indexer.remove(s);
            unsubscribed(s);
        } finally {
            lock.unlock();
        }
    }

    protected void deliver(final E element, EventSubscription<E> s) {
        try {
            // check if still valid subscription??
            s.getEventHandler().process(element);
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

    protected boolean inform(final E element) {
        for (EventSubscription<E> s : indexer.match(element)) {
            deliver(element, s);
        }
        return true;
    }

    protected void subscribed(EventSubscription<E> s) {

    }

    protected void unsubscribed(EventSubscription<E> s) {

    }

    @SuppressWarnings("hiding")
    static class DefaultSubscription<E> implements EventSubscription<E> {
        private final DefaultEventBus<E> bus;

        private final EventProcessor<? super E> destination;

        private final Filter<? super E> filter;

        private final String name;

        /**
         * @param destination
         * @param filter
         */
        DefaultSubscription(DefaultEventBus<E> bus, final String name,
                final EventProcessor<? super E> destination, final Filter<? super E> filter) {
            this.bus = bus;
            this.name = name;
            this.destination = destination;
            this.filter = filter;
        }

        /**
         * @see org.coconut.event.bus.Subscription#cancel()
         */
        public void cancel() {
            bus.cancel(this);
        }

        /**
         * @see org.coconut.event.bus.Subscription#getListener()
         */
        public EventProcessor<? super E> getEventHandler() {
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
    }

}
