package org.coconut.event.bus;

import org.coconut.core.EventHandler;
import org.coconut.filter.Filter;

/**
 * This interface represent a subcription of a particular sets of events by its
 * filter delivered to its destination.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public interface EventSubscription<E> {

    /**
     * Returns the listener for this subscription.
     * 
     * @return the listener for this subscription.
     */
    EventHandler<? super E> getEventHandler();

    /**
     * Returns the filter that is used for this subscription or <tt>null</tt>
     * all events are accepted.
     * 
     * @return the filter that is used for this subscription.
     */
    Filter<? super E> getFilter();

    /**
     * Returns a unique name used for this subscription. If no name is specified
     * at construction time the EventBus will automatically generate a unique
     * name.
     * 
     * @return a unique name used for this subscription.
     */
    String getName();

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
