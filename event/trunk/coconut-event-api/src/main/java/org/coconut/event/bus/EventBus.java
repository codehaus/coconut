/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus;

import java.util.Collection;

import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

/**
 * An event bus is an api for the publish/subscribe messaging paradigm. Publishers post
 * events (messages) to an event bus, rather than sending messages to specific recipients.
 * The event bus then broadcasts the posted events to all interested parties
 * (subscribers).
 * <p>
 * Implementations might dispatch events to listeners in the same thread as the caller, in
 * another thread or both. Furthermore some implementations might provide transactional
 * gurantees, guarantee that events are delived to all subscribers or none at all. The
 * following tables summerizes the most common properties of an <tt>EventBus</tt>
 * implementation.
 * <p>
 * <b>Synchronous/Asynchronous delivery:</b>
 * <p>
 * Most implementations will deliver events to all the matching subscribers using the same
 * thread (synchronous delivery) that originally dispatched the event (called the
 * {@link #offer(Object) method}. Some implementations might choose to use another thread
 * (asynchronous delivery) to do the actual delivery of the event (by calling
 * {@link org.coconut.core.Procedure#apply(Object) on the subscriber}. It is also
 * possible to use a mixture of the two approaches where high priority events are
 * delivered synchronously and low priority are delivered asynchronusly by a low priority
 * thread.
 * <p>
 * <b>Message order:</b>
 * <p>
 * Because of multi-threading most implementations cannot guarantee that events are
 * delivered in the same order they where submitted. For example, if the two events
 * <code>A</code> and <code>B</code> are simultaneous submitted in different threads.
 * One subscriber might recieve <code>A</code> followed by <code>B</code> while
 * another subscriber might see <code>B</code> followed by <code>A</code>. If
 * subcribers are guranteed to see all events in the same order it is said to be TODO
 * (find name). However, any implementation is likely to pay a performance penalty for
 * guaranteeing this.
 * <p>
 * <b>Threadsafety:</b>
 * <p>
 * <p>
 * <b>Persistent:</b>
 * <p>
 * This is probably in connection with transactional delivery
 * <p>
 * <b>Transactional delivery:</b>
 * <p>
 * Most implementations does not have any transactional gurantees and a primary meant to
 * be a lightweight alternative to message frameworks such as JMS.
 * <p>
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: EventBusConfiguration.java 415 2007-11-09 08:25:23Z kasper $
 * @param <E>
 *            the type of events processed by this event bus
 */
public interface EventBus<E> extends Procedure<E> {

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
    boolean offerAll(Collection<? extends E> events);

    /**
     * Returns all subscribers that are registered for this EventBus.
     *
     * @return all subscribers that are registered for this EventBus
     */
    Collection<EventSubscription<E>> getSubscribers();

    /**
     * Cancels all subscriptions. This is analogues to calling cancel on every
     * subscription.
     *
     * @return A collection of all the subscribers that was unregistered.
     */
    Collection<EventSubscription<E>> unsubscribeAll();

    /**
     * Creates an subscription that will be notified for <tt>any</tt> event being
     * published to this event bus.
     *
     * @param eventHandler
     *            the event handler that will be notified of a published event.
     * @return a subscription that can be used to cancel any further notifications
     */
    EventSubscription<E> subscribe(Procedure<? super E> eventHandler);

    /**
     * Creates an subscription that will be notified for any event that is accepted by the
     * specified filter.
     *
     * @param eventHandler
     *            the event handler that will be notified of a published event.
     * @param filter
     *            the filter that will be used to test against events
     * @return a subscription that can be used to cancel any further notifications
     */
    EventSubscription<E> subscribe(Procedure<? super E> eventHandler,
            Predicate<? super E> filter);

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
     */
    EventSubscription<E> subscribe(Procedure<? super E> listener, Predicate<? super E> filter,
            String name);
}
