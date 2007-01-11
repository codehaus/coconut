package org.coconut.event;

import org.coconut.core.EventProcessor;
import org.coconut.core.Named;
import org.coconut.filter.Filter;

/**
 * This interface represent a subscription of a particular sets of events by its
 * filter delivered to its destination.
 * <p>
 * Each subscription has a unique name. If no name is specified at construction
 * time an implementation should automatically generate a unique name.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface EventSubscription<E> extends Named {

    /**
     * Returns the listener for this subscription.
     * 
     * @return the listener for this subscription.
     */
    EventProcessor<? super E> getEventHandler();

    /**
     * Returns the filter that is used for this subscription or <tt>null</tt>
     * all events are accepted.
     * 
     * @return the filter that is used for this subscription.
     */
    Filter<? super E> getFilter();

    /**
     * Cancels the subscription. No further events will be delivered to the
     * specified event handler.
     * <p>
     * Note: that unless otherwise noted this method gurantees that no pending
     * events will be delivered. A highly concurrent lock-free implementation
     * might not be able to gurantee that some pending events will not be
     * delivered.
     */
    void cancel();
}
