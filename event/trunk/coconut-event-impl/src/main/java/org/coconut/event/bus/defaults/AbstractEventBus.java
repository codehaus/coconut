/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus.defaults;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.EventProcessor;
import org.coconut.event.bus.EventBus;
import org.coconut.event.bus.EventBusConfiguration;
import org.coconut.event.bus.EventSubscription;
import org.coconut.predicate.Predicate;
import org.coconut.predicate.Predicates;

/**
 * @param <E>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AbstractEventBus.java 415 2007-11-09 08:25:23Z kasper $
 */
public abstract class AbstractEventBus<E> implements EventBus<E> {

    private final static String SUBSCRIPTION_NAME_PREFIX = "Subscription-";

    private final ThreadLocal<Boolean> allowReentrance;

    private final AtomicLong idGenerator = new AtomicLong();

    AbstractEventBus(EventBusConfiguration<E> configuration) {
        if (configuration == null) {
            throw new NullPointerException("configuration is null");
        }
        if (configuration.getCheckReentrant()) {
            allowReentrance = new ThreadLocal<Boolean>();
            allowReentrance.set(false);
            throw new UnsupportedOperationException("check reentrant not supported yet");
        } else {
            allowReentrance = null;
        }
    }

    /** {@inheritDoc} */
    public boolean offer(final E element) {
        return inform(element, true);
    }

    /** {@inheritDoc} */
    public boolean offerAll(Collection<? extends E> c) {
        return informAll(c, false);
    }

    /** {@inheritDoc} */
    public void process(E event) {
        inform(event, false);
    }

    /** {@inheritDoc} */
    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler) {
        return subscribe(eventHandler, Predicates.truePredicate());
    }

    private boolean inform(E element, boolean doThrow) {
        if (element == null) {
            throw new NullPointerException("element is null");
        }
        if (allowReentrance != null) {
            Boolean current = allowReentrance.get();
            if (current.equals(Boolean.TRUE)) {
                if (doThrow) {
                    throw new IllegalStateException("Eventbus does not allow reentrence");
                } else {
                    return false;
                }
            }
            allowReentrance.set(Boolean.TRUE);
            try {
                return doInform(element, doThrow);
            } finally {
                allowReentrance.set(Boolean.FALSE);
            }
        } else {
            return doInform(element, doThrow);
        }
    }

    private boolean informAll(Collection<? extends E> c, boolean doThrow) {
        if (c == null) {
            throw new NullPointerException("c is null");
        }
        for (E element : c) {
            if (element == null) {
                throw new NullPointerException("collection contained a null");
            }
        }
        if (allowReentrance != null) {
            Boolean current = allowReentrance.get();
            if (current.equals(Boolean.TRUE)) {
                if (doThrow) {
                    throw new IllegalStateException("Eventbus does not allow reentrence");
                } else {
                    return false;
                }
            }
            allowReentrance.set(Boolean.TRUE);
            try {
                return doInformAll(c, doThrow);
            } finally {
                allowReentrance.set(Boolean.FALSE);
            }
        } else {
            return doInformAll(c, doThrow);
        }
    }

    void cancel(EventSubscription<E> aes) {}

    abstract boolean doInform(E element, boolean doThrow);

    boolean doInformAll(Collection<? extends E> col, boolean doThrow) {
        boolean ok = true;
        for (E element : col) {
            ok &= inform(element, doThrow);
        }
        return ok;
    }

    String getNextName(EventProcessor<? super E> eventHandler, Predicate<? super E> predicate) {
        return SUBSCRIPTION_NAME_PREFIX + idGenerator.incrementAndGet();
    }

    void subscribed(EventSubscription<E> s) {}

    void unsubscribed(EventSubscription<E> s) {}
}
