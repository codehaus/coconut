/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import java.util.Collection;

import org.coconut.core.EventProcessor;
import org.coconut.event.EventBus;
import org.coconut.event.EventSubscription;
import org.coconut.predicate.Predicate;

/**
 * The CacheEventService can be used to subscribe to CacheEvent's raised by the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public interface CacheEventService<K, V> extends EventBus<CacheEvent<K, V>> {

    /**
     * Inserts the specified element into the underlying data structure, if possible. Some
     * implementations may impose insertion restrictions (for example capacity bounds),
     * which can fail to insert an element by returning false.
     * 
     * @param element
     *            the element to add.
     * @return <tt>true</tt> if it was possible to add the element to the data
     *         structure, else <tt>false</tt>
     * @throws NullPointerException
     *             if the specified element is <tt>null</tt>
     */
    boolean offer(CacheEvent<K, V> event);

    /**
     * Handles an event.
     * 
     * @param event
     *            The event that the EventHandler must process
     * @throws ClassCastException
     *             the class of the specified element prevents it from handled by this
     *             event-handler.
     * @throws NullPointerException
     *             if the specified element is null
     * @throws IllegalArgumentException
     *             some aspect of this element prevents it from being handled by this
     *             event-handler.
     * @throws IllegalStateException
     *             if the cache has been shutdown
     */
    void process(CacheEvent<K, V> event);

    /**
     * A failure encountered while attempting to offering elements to an event bus may
     * result in some elements having already been processed. when the associated
     * exception is thrown. The behavior of this operation is unspecified if the specified
     * collection is modified while the operation is in progress.
     * 
     * @param events
     *            the events to process
     * @return a boolean indicating if all events was succesfully processed.
     */
    boolean offerAll(Collection<? extends CacheEvent<K, V>> events);

    /**
     * Returns all subscribers that are registered for this EventBus.
     * 
     * @return all subscribers that are registered for this EventBus
     */
    Collection<EventSubscription<CacheEvent<K, V>>> getSubscribers();

    /**
     * Cancels all subscriptions. This is analogues to calling cancel on every
     * subscription.
     * 
     * @return A collection of all the subscribers that was unregistered.
     */
    Collection<EventSubscription<CacheEvent<K, V>>> unsubscribeAll();

    /**
     * Creates an subscription that will be notified for <tt>any</tt> event being
     * published to this event bus.
     * 
     * @param eventHandler
     *            the event handler that will be notified of a published event.
     * @return a subscription that can be used to cancel any further notifications
     * @throws IllegalStateException
     *             if the cache has been shutdown
     */
    EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler);

    /**
     * Creates an subscription that will be notified for any event that is accepted by the
     * specified filter.
     * 
     * @param eventHandler
     *            the event handler that will be notified of a published event.
     * @param filter
     *            the filter that will be used to test against events
     * @return a subscription that can be used to cancel any further notifications
     * @throws IllegalStateException
     *             if the cache has been shutdown
     */
    EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler,
            Predicate<? super CacheEvent<K, V>> filter);

    /**
     * Creates an subscription that will be notified for any event that is accepted by the
     * specified filter. This method also specifies the name of the subscription which is
     * usefull for remote monitoring and controlling.
     * 
     * @param listener
     *            the event handler that will be notified of a published event.
     * @param filter
     *            the filter that will be used to test against events
     * @param name
     *            the name of the subscription as returned by
     *            {@link EventSubscription#getName()}
     * @return a subscription that can be used to cancel any further notifications
     * @throws IllegalArgumentException
     *             if the specified name is not unique within all the subscriptions
     * @throws IllegalStateException
     *             if the cache has been shutdown
     */
    EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> listener,
            Predicate<? super CacheEvent<K, V>> filter, String name);
}
