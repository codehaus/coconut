/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.coconut.core.EventProcessor;
import org.coconut.event.EventBus;
import org.coconut.event.EventBusConfiguration;
import org.coconut.event.EventSubscription;
import org.coconut.event.spi.AbstractEventBus;
import org.coconut.predicate.Predicate;
import org.coconut.predicate.matcher.DefaultPredicateMatcher;
import org.coconut.predicate.matcher.PredicateMatcher;

/**
 * The order of subscribers are maintained.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
/**
 * @param <E>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultEventBus<E> extends AbstractEventBus<E> implements EventBus<E> {

    final PredicateMatcher<DefaultEventSubscription<E>, E> indexer;

    private final Lock lock = new ReentrantLock();

    private final ConcurrentHashMap<String, DefaultEventSubscription<E>> subscribers = new ConcurrentHashMap<String, DefaultEventSubscription<E>>();

    // specify indexer, log, thread pool
    // management, ...
    public DefaultEventBus() {
        this(EventBusConfiguration.DEFAULT_CONFIGURATION);
    }

    public DefaultEventBus(EventBusConfiguration<E> conf) {
        super(conf);
        if (conf.getFilterMatcher() == null) {
            indexer = new DefaultPredicateMatcher<DefaultEventSubscription<E>, E>();
        } else {
            indexer = (PredicateMatcher<DefaultEventSubscription<E>, E>) conf.getFilterMatcher();
        }
    }

    protected void cancel(EventSubscription<E> aes) {

    }

    @SuppressWarnings("unchecked")
    public List<EventSubscription<E>> getSubscribers() {
        return Collections.unmodifiableList(new ArrayList(subscribers.values()));
    }

    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler,
            Predicate<? super E> filter) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        lock.lock();
        try {
            for (;;) {
                String name = getNextName(eventHandler, filter);
                DefaultEventSubscription<E> s = newSubscription(eventHandler, filter, name);
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

    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler,
            Predicate<? super E> filter, String name) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        } else if (name == null) {
            throw new NullPointerException("name is null");
        }
        lock.lock();
        try {
            DefaultEventSubscription<E> s = newSubscription(eventHandler, filter, name);
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

    public Collection<EventSubscription<E>> unsubscribeAll() {
        lock.lock();
        try {
            Collection<EventSubscription<E>> c = new ArrayList<EventSubscription<E>>(subscribers
                    .size());
            for (DefaultEventSubscription<E> s : subscribers.values()) {
                unsubscribed(s);
                s.setActive(false);
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
    void cancel(DefaultEventSubscription<E> s) {
        lock.lock();
        try {
            s.writeLock().lock();
            try {
                subscribers.remove(s.getName());
                indexer.remove(s);
                unsubscribed(s);
                s.setActive(false);
            } finally {
                s.writeLock().unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    protected void deliver(final E element, EventSubscription<E> s) {
        try {
            // check if still valid subscription??
            s.getEventProcessor().process(element);
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
        s.unsubscribe();
    }

    protected boolean doInform(final E element, boolean doThrow) {
        for (DefaultEventSubscription<E> s : indexer.match(element)) {
            ReadLock rl = s.readLock();
            rl.lock();
            try {
                deliver(element, s);
            } finally {
                rl.unlock();
            }
        }
        return true;
    }

    DefaultEventSubscription<E> newSubscription(EventProcessor<? super E> eventHandler,
            Predicate<? super E> filter, String name) {
        return new DefaultEventSubscription<E>(this, name, eventHandler, filter);
    }
}
