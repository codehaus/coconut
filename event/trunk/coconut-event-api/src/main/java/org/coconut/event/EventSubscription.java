/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import org.coconut.core.EventProcessor;
import org.coconut.filter.Filter;

/**
 * This interface represent a subscription of a particular sets of events by its filter
 * delivered to its destination.
 * <p>
 * Each subscription has a unique name. If no name is specified at construction time an
 * unique name will be generated.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface EventSubscription<E> {

    /**
     * Returns the unique name of this subscription.
     * 
     * @return the unique name of this subscription
     */
    String getName();

    /**
     * Returns the listener for this subscription.
     * 
     * @return the listener for this subscription
     */
    EventProcessor<? super E> getEventProcessor();

    /**
     * Returns the filter that is used for this subscription or <tt>null</tt> all events
     * are accepted.
     * 
     * @return the filter that is used for this subscription
     */
    Filter<? super E> getFilter();

    /**
     * Cancels the subscription. After this method has returned no further events will be
     * delivered to the specified event processor.
     * <p>
     * Implementations that cannot guarantee that no pending events will be delivered
     * should clearly specify this.
     */
    void unsubscribe();

    /**
     * Returns whether or not this subscription is still active.
     * 
     * @return
     */
    boolean isActive();
}
