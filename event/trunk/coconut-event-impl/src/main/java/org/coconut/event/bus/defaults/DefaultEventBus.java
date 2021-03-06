/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.coconut.event.bus.EventBus;
import org.coconut.event.bus.EventSubscription;
import org.coconut.internal.predicatematcher.DefaultPredicateMatcher;
import org.coconut.internal.predicatematcher.PredicateMatcher;
import org.coconut.operations.Ops.Generator;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

/**
 * The order of subscribers are maintained.
 *
 * @param <E>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DefaultEventBus.java 415 2007-11-09 08:25:23Z kasper $
 */
public class DefaultEventBus<E> extends AbstractEventBus<E> implements EventBus<E> {

    private final PredicateMatcher<DefaultEventSubscription<E>, E> indexer;

    private final Lock lock = new ReentrantLock();

    private final ConcurrentHashMap<String, DefaultEventSubscription<E>> subscribers = new ConcurrentHashMap<String, DefaultEventSubscription<E>>();

    private final Generator<String> nameGenerator = new NameGenerator("Subscription-");

    public DefaultEventBus() {
        indexer = new DefaultPredicateMatcher<DefaultEventSubscription<E>, E>();
    }

    @SuppressWarnings("unchecked")
    public List<EventSubscription<E>> getSubscribers() {
        return Collections.unmodifiableList(new ArrayList(subscribers.values()));
    }

    /** {@inheritDoc} */
    public EventSubscription<E> subscribe(Procedure<? super E> eventHandler,
            Predicate<? super E> filter) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        lock.lock();
        try {
            for (;;) {
                String name = nameGenerator.generate();
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

    /** {@inheritDoc} */
    public EventSubscription<E> subscribe(Procedure<? super E> eventHandler,
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

    /** {@inheritDoc} */
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

    private DefaultEventSubscription<E> newSubscription(Procedure<? super E> eventHandler,
            Predicate<? super E> filter, String name) {
        return new DefaultEventSubscription<E>(this, name, eventHandler, filter);
    }

    protected void deliver(final E element, DefaultEventSubscription<E> s) {
        try {
            // check if still valid subscription??
            s.getEventProcessor().apply(element);
        } catch (RuntimeException e) {
            deliveryFailed(s, element, e);
        }
    }

    protected void deliveryFailed(EventSubscription<E> s, final E element, Throwable cause) {
        System.err.println("The delivery to " + s.getName()
                + " failed with the following exception: ");
        cause.printStackTrace();
    }

    @Override
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
}
