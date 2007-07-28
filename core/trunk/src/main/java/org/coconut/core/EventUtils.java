/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.beans.EventHandler;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Queue;

/**
 * Factory and utility methods for {@link Callback}, {@link EventHandler} and
 * {@link Offerable} classes defined in this package. This class supports the following
 * kinds of methods:
 * <ul>
 * <li>Methods that create and return an {@link Offerable} that performs common tasks.
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class EventUtils {
    // /CLOVER:OFF
    /** Cannot instantiate. */
    private EventUtils() {}

    // /CLOVER:ON

    /**
     * Wraps an {@link EventProcessor} in an {@link Offerable}.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static class EventProcessor2OfferableAdaptor<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5555001640212350081L;

        /** The EventProcessor we are wrapping. */
        private final EventProcessor<E> offerable;

        /**
         * Creates a new EventProcessor2OfferableAdaptor.
         * 
         * @param eventProcessor
         *            the EventProcessor to wrap
         * @throws NullPointerException
         *             if the supplied eventHandler is <code>null</code>
         */
        public EventProcessor2OfferableAdaptor(final EventProcessor<E> eventProcessor) {
            if (eventProcessor == null) {
                throw new NullPointerException("offerable is null");
            }
            this.offerable = eventProcessor;
        }

        /** {@inheritDoc} */
        public boolean offer(E element) {
            offerable.process(element);
            return true;
        }
    }

    /**
     * An Offerable that returns false for any element parsed to its
     * {@link #offer(Object)} method.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static class IgnoreFalseOfferable<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -3497640264421275470L;

        /** {@inheritDoc} */
        public boolean offer(E element) {
            return false;
        }
    }

    /**
     * An Offerable that returns true for any element parsed to its {@link #offer(Object)}
     * method.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static class IgnoreTrueOfferable<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8883512217513983631L;

        /** {@inheritDoc} */
        public boolean offer(E element) {
            return true;
        }
    }

    /**
     * Wraps an {@link Offerable} in an {@link EventProcessor}.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static class Offerable2EventHandlerAdaptor<E> implements EventProcessor<E>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4293606104983956712L;

        /** The offerable that is being wrapped. */
        private final Offerable<E> offerable;

        /**
         * Creates a new Offerable2EventHandlerAdaptor.
         * 
         * @param offerable
         *            the Offerable to wrap
         * @throws NullPointerException
         *             if the specified offerable is <code>null</code>
         */
        public Offerable2EventHandlerAdaptor(final Offerable<E> offerable) {
            if (offerable == null) {
                throw new NullPointerException("offerable is null");
            }
            this.offerable = offerable;
        }

        /** {@inheritDoc} */
        public void process(E element) {
            offerable.offer(element);
        }
    }

    /**
     * Wraps a {@link Queue} in an {@link EventProcessor}.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static class QueueAdaptor<E> implements EventProcessor<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -467485596894009881L;

        /** The queue we are wrapping. */
        private final Queue<E> queue;

        /**
         * Creates a new QueueAdaptor.
         * 
         * @param queue
         *            the Queue to wrap
         * @throws NullPointerException
         *             if the specified queue is <code>null</code>
         */
        public QueueAdaptor(final Queue<E> queue) {
            if (queue == null) {
                throw new NullPointerException("queue is null");
            }
            this.queue = queue;
        }

        /** {@inheritDoc} */
        public void process(E element) {
            queue.offer(element); // ignore return value
        }
    }

    /**
     * Wraps an {@link Offerable} in an {@link EventHandler}.
     * 
     * @param offerable
     *            the offerable to wrap
     * @return an EventHandler wrapping an Offerable
     * @throws NullPointerException
     *             if the supplied offerable is <code>null</code>
     * @param <E>
     *            the types of parameters accepted by Offerable
     */
    public static <E> EventProcessor<E> fromOfferable(final Offerable<E> offerable) {
        return new Offerable2EventHandlerAdaptor<E>(offerable);
    }

    /**
     * Wraps an {@link java.util.Queue} in an {@link EventHandler}.
     * 
     * @param queue
     *            the queue to wrap
     * @return an EventHandler wrapping an Queue
     * @throws NullPointerException
     *             if the supplied offerable is <code>null</code>
     */
    public static <E> EventProcessor<E> fromQueue(final Queue<E> queue) {
        return new QueueAdaptor<E>(queue);
    }

    @SuppressWarnings("unchecked")
    public static <E> EventProcessor<E> ignoreEventHandler() {
        return (EventProcessor<E>) fromOfferable(ignoreFalse());
    }

    /**
     * Returns an {@link Offerable} that returns <tt>false</tt> for any element that is
     * offered to it.
     * 
     * @return an offerable that returns <tt>false</tt> for any element that is offered
     *         to it.
     * @param <E>
     *            the type of elements accepted by the offerable
     */
    public static <E> Offerable<E> ignoreFalse() {
        return new IgnoreFalseOfferable<E>();
    }

    /**
     * Returns an {@link Offerable} that returns <tt>true</tt> for any element that is
     * offered to it.
     * 
     * @return an {@link Offerable} that returns <tt>true</tt> for any element that is
     *         offered to it.
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    public static <E> Offerable<E> ignoreTrue() {
        return new IgnoreTrueOfferable<E>();
    }

    /**
     * Wraps an {@link EventProcessor} in an {@link Offerable}.
     * 
     * @param handler
     *            the EventProcessor to wrap
     * @return an Offerable wrapping an EventHandler
     * @throws NullPointerException
     *             if the supplied eventHandler is <code>null</code>
     */
    public static <E> Offerable<E> toOfferable(final EventProcessor<E> handler) {
        return new EventProcessor2OfferableAdaptor<E>(handler);
    }

    public static <E> Offerable<E> toOfferableSafe(final EventProcessor<E> eventHandler) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler is null");
        }
        return new Offerable<E>() {
            public boolean offer(E element) {
                try {
                    eventHandler.process(element);
                    return true;
                } catch (RuntimeException ex) {
                    return false;
                }
            }
        };
    }

    public static <E> EventProcessor<E> toPrintStream(final PrintStream ps) {
        if (ps == null) {
            throw new NullPointerException("ps is null");
        }
        return new EventProcessor<E>() {
            public void process(E element) {
                ps.println(element.toString());
            }
        };
    }

    public static <E> EventProcessor<E> toPrintStreamSafe(final PrintStream ps) {
        if (ps == null) {
            throw new NullPointerException("ps is null");
        }
        return new EventProcessor<E>() {
            public void process(E element) {
                try {
                    ps.println(element.toString());
                } catch (RuntimeException re) {
                    // ignore
                }
            }
        };
    }

    public static <E> EventProcessor<E> toSystemOut() {
        return toPrintStream(System.out);
    }

    public static <E> EventProcessor<E> toSystemOutSafe() {
        return toPrintStreamSafe(System.out);
    }
}
