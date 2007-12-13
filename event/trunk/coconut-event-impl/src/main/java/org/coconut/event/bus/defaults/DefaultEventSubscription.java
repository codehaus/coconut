/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus.defaults;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.coconut.core.EventProcessor;
import org.coconut.event.bus.EventSubscription;
import org.coconut.operations.Ops.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DefaultEventSubscription.java 474 2007-11-20 13:09:23Z kasper $
 */
class DefaultEventSubscription<E> extends ReentrantReadWriteLock implements EventSubscription<E> {
    /** The EventBus this subscription is part of. */
    private final DefaultEventBus<E> bus;

    /** The destination of events for this subscription. */
    private final EventProcessor<? super E> destination;

    /**
     * The Predicate that is used to decide, if a given event should be delivered to this
     * subscriptions destination.
     */
    private final Predicate<? super E> predicate;

    /** The name of this subscription. */
    private final String name;

    private volatile boolean isActive = true;

    /**
     * Creates a new DefaultEventSubscription.
     * 
     * @param bus
     *            the EventBus this subscription is part of
     * @param name
     *            the unique name of this subscription
     * @param destination
     *            the destination of events for this subscription
     * @param predicate
     *            the Predicate that is used to decide, if a given event should be
     *            delivered to this subscriptions destination
     */
    DefaultEventSubscription(DefaultEventBus<E> bus, final String name,
            final EventProcessor<? super E> destination, final Predicate<? super E> predicate) {
        this.bus = bus;
        this.name = name;
        this.destination = destination;
        this.predicate = predicate;
    }

    /** {@inheritDoc} */
    public void unsubscribe() {
        bus.cancel(this);
    }

    /** {@inheritDoc} */
    public EventProcessor<? super E> getEventProcessor() {
        return destination;
    }

    /** {@inheritDoc} */
    public Predicate<? super E> getFilter() {
        return predicate;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public boolean isValid() {
        return isActive;
    }

    void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
