/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.impl;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.EventProcessor;
import org.coconut.event.EventBus;
import org.coconut.event.EventBusConfiguration;
import org.coconut.event.EventSubscription;
import org.coconut.filter.Filter;
import org.coconut.filter.LogicFilters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractEventBus<E> implements EventBus<E> {

    private final static String SUBSCRIPTION_NAME_PREFIX = "Subscription-";

    private final EventBusConfiguration<E> conf;

    private final AtomicLong idGenerator = new AtomicLong();

    private final ThreadLocal<Boolean> allowReentrant;

    public AbstractEventBus(EventBusConfiguration<E> conf) {
        this.conf = conf;
        if (conf.getCheckReentrant()) {
            allowReentrant = new ThreadLocal<Boolean>();
        } else {
            allowReentrant = null;
        }
    }

    public EventBusConfiguration<E> getConfiguration() {
        // unmodifiable...
        return conf;
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

    /**
     * @see org.coconut.event.bus.EventBus#subscribe(org.coconut.core.EventHandler)
     */
    public EventSubscription<E> subscribe(EventProcessor<? super E> eventHandler) {
        return subscribe(eventHandler, LogicFilters.trueFilter());
    }

    public boolean offerAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("c is null");
        }
        for (E element : c) {
            if (element == null) {
                throw new NullPointerException("collection contained a null");
            }
        }
        boolean ok = true;
        for (E element : c) {
            if (element == null) {
                throw new NullPointerException("element is null");
            }
            ok &= inform(element);
        }
        return ok;
    }

    private boolean inform(E element) {
        if (allowReentrant != null) {
            Boolean current = allowReentrant.get();
            if (current.equals(Boolean.TRUE)) {
                throw new IllegalStateException("Eventbus does not allow reentrence");
            }
            allowReentrant.set(Boolean.TRUE);
            try {
                return doInform(element);
            } finally {
                allowReentrant.set(Boolean.FALSE);
            }
        } else {
            return doInform(element);
        }
    }

    protected abstract boolean doInform(E element);

    protected String getNextName(EventProcessor<? super E> eventHandler,
            Filter<? super E> filter) {
        return SUBSCRIPTION_NAME_PREFIX + idGenerator.incrementAndGet();
    }

    protected void subscribed(EventSubscription<E> s) {

    }

    protected void unsubscribed(EventSubscription<E> s) {

    }

    void cancel(DefaultEventSubscription<E> aes) {

    }
}
