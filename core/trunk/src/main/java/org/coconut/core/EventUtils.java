/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Queue;

/**
 * Factory and utility methods for {@link EventProcessor} and {@link Offerable} classes
 * defined in this package. This class supports the following kinds of methods:
 * <ul>
 * <li>Methods that create and return an {@link Offerable} that performs common tasks.
 * <li>Methods that create and return an {@link EventProcessor} that performs common
 * tasks.
 * </ul>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class EventUtils {
    ///CLOVER:OFF
    /** Cannot instantiate. */
    private EventUtils() {}
    ///CLOVER:ON

    /**
     * Returns an EventProcessor that ignores any call to
     * {@link EventProcessor#process(Object)}.
     * <p>
     * The returned EventProcessor is serializable.
     * 
     * @return an EventProcessor that ignores any call to
     *         {@link EventProcessor#process(Object)}
     * @param <E>
     *            the types of elements accepted by EventProcessor
     */
    @SuppressWarnings("unchecked")
    public static <E> EventProcessor<E> dummyEventProcessor() {
        return (EventProcessor<E>) fromOfferable(dummyOfferableFalse());
    }

    /**
     * Returns an {@link Offerable} that returns <tt>false</tt> for any element that is
     * offered to it.
     * <p>
     * The returned Offerable is serializable.
     * 
     * @return an offerable that returns <tt>false</tt> for any element that is offered
     *         to it.
     * @param <E>
     *            the type of elements accepted by the offerable
     */
    public static <E> Offerable<E> dummyOfferableFalse() {
        return new DummyOfferableFalse<E>();
    }

    /**
     * Returns an {@link Offerable} that returns <tt>true</tt> for any element that is
     * offered to it.
     * <p>
     * The returned Offerable is serializable.
     * 
     * @return an {@link Offerable} that returns <tt>true</tt> for any element that is
     *         offered to it.
     * @param <E>
     *            the types of elements accepted by the offer method
     */
    public static <E> Offerable<E> dummyOfferableTrue() {
        return new DummyOfferableTrue<E>();
    }

    /**
     * Wraps an {@link Offerable} in an {@link EventProcessor}.
     * <p>
     * The returned EventProcessor is serializable if the specified Offerable is
     * serializable.
     * 
     * @param offerable
     *            the offerable to wrap
     * @return an EventProcessor wrapping an Offerable
     * @throws NullPointerException
     *             if the specified offerable is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the specified Offerable
     */
    public static <E> EventProcessor<E> fromOfferable(final Offerable<E> offerable) {
        return new Offerable2EventProcessor<E>(offerable);
    }

    /**
     * Wraps an {@link java.util.Queue} in an {@link EventProcessor}.
     * <p>
     * The returned EventProcessor is serializable if the specified Queue is serializable.
     * 
     * @param queue
     *            the queue to wrap
     * @return an EventProcessor wrapping an Queue
     * @throws NullPointerException
     *             if the supplied queue is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the specified Queue
     */
    public static <E> EventProcessor<E> fromQueue(final Queue<E> queue) {
        return new Queue2EventProcessor<E>(queue);
    }

    /**
     * Wraps an {@link EventProcessor} in an {@link Offerable}. The offer method of the
     * returned Offerable will return <code>true</code> for all values parsed to the
     * specified event processor.
     * <p>
     * The returned Offerable is serializable if the specified EventProcessor is
     * serializable.
     * 
     * @param processor
     *            the EventProcessor to wrap
     * @return an Offerable wrapping an EventProcessor
     * @throws NullPointerException
     *             if the specified processor is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the process method
     */
    public static <E> Offerable<E> toOfferable(final EventProcessor<E> processor) {
        return new EventProcessor2Offerable<E>(processor);
    }

    /**
     * Wraps an {@link Offerable} in an {@link EventProcessor}.The offer method of the
     * returned Offerable will return <code>true</code> for all values parsed to the
     * specified event processor. However, unlike {@link #toOfferable(EventProcessor)} any
     * runtime thrown by the eventprocessor will be catched in the returned offerable and
     * <code>false</code> will be returned.
     * <p>
     * The returned Offerable is serializable if the specified EventProcessor is
     * serializable.
     * 
     * @param processor
     *            the EventProcessor to wrap
     * @return an EventProcessor wrapped in a Offerable
     * @throws NullPointerException
     *             if the specified processor is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the process method
     */
    public static <E> Offerable<E> toOfferableSafe(final EventProcessor<E> processor) {
        return new EventProcessor2OfferableSafe<E>(processor);
    }

    /**
     * Returns an event processor that will print all elements processor to the specified
     * printstream using the elements {@link Object#toString()} method.
     * 
     * @param ps
     *            the PrintStream to write to
     * @return an EventProcessor where all elements processor will be written to the
     *         specified printstream.
     * @throws NullPointerException
     *             if the specified printstream is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the process method
     */
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

    /**
     * Works as {@link #toPrintStream(PrintStream)} except that any
     * {@link RuntimeException} will be ignored.
     * 
     * @param ps
     *            the PrintStream to write to
     * @return an EventProcessor where all elements processor will be written to the
     *         specified printstream.
     * @throws NullPointerException
     *             if the specified printstream is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the process method
     */
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

    /**
     * Shorthand for calling {@link #toPrintStream(PrintStream)} with {@link System#out}
     * as the output printstream.
     * 
     * @return an eventhandler that prints all processed elements to {@link System#out}
     * @param <E>
     *            the types of elements accepted by the process method
     */
    public static <E> EventProcessor<E> toSystemOut() {
        return toPrintStream(System.out);
    }

    /**
     * Shorthand for calling {@link #toPrintStreamSafe(PrintStream)} with
     * {@link System#out} as the output printstream.
     * 
     * @return an eventhandler that prints all processed elements to {@link System#out}
     * @param <E>
     *            the types of elements accepted by the process method
     */
    public static <E> EventProcessor<E> toSystemOutSafe() {
        return toPrintStreamSafe(System.out);
    }

    /**
     * An Offerable that returns false for any element parsed to its
     * {@link #offer(Object)} method.
     * 
     * @param <E>
     *            the types of elements accepted by the offer method
     */
    static final class DummyOfferableFalse<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7759759407095347462L;

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
     *            the types of elements accepted by the offer method
     */
    static final class DummyOfferableTrue<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8883512217513983631L;

        /** {@inheritDoc} */
        public boolean offer(E element) {
            return true;
        }
    }

    /**
     * Wraps an {@link EventProcessor} in an {@link Offerable}.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static final class EventProcessor2Offerable<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5555001640212350081L;

        /** The EventProcessor we are wrapping. */
        private final EventProcessor<E> processor;

        /**
         * Creates a new EventProcessor2Offerable.
         * 
         * @param processor
         *            the EventProcessor to wrap
         * @throws NullPointerException
         *             if the supplied eventProcessor is <code>null</code>
         */
        public EventProcessor2Offerable(final EventProcessor<E> processor) {
            if (processor == null) {
                throw new NullPointerException("processor is null");
            }
            this.processor = processor;
        }

        /** {@inheritDoc} */
        public boolean offer(E element) {
            processor.process(element);
            return true;
        }
    }

    /**
     * Wraps an {@link EventProcessor} in an {@link Offerable}. Catching any
     * {@link RuntimeException}'s.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static final class EventProcessor2OfferableSafe<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5555001640212350081L;

        /** The EventProcessor we are wrapping. */
        private final EventProcessor<E> processor;

        /**
         * Creates a new EventProcessor2OfferableSafe.
         * 
         * @param processor
         *            the EventProcessor to wrap
         * @throws NullPointerException
         *             if the supplied eventProcessor is <code>null</code>
         */
        public EventProcessor2OfferableSafe(final EventProcessor<E> processor) {
            if (processor == null) {
                throw new NullPointerException("processor is null");
            }
            this.processor = processor;
        }

        /** {@inheritDoc} */
        public boolean offer(E element) {
            try {
                processor.process(element);
                return true;
            } catch (RuntimeException ex) {
                return false;
            }
        }
    }

    /**
     * Wraps an {@link Offerable} in an {@link EventProcessor}.
     * 
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static final class Offerable2EventProcessor<E> implements EventProcessor<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5017539960531630701L;

        /** The offerable that is being wrapped. */
        private final Offerable<E> offerable;

        /**
         * Creates a new Offerable2EventProcessor.
         * 
         * @param offerable
         *            the Offerable to wrap
         * @throws NullPointerException
         *             if the specified offerable is <code>null</code>
         */
        public Offerable2EventProcessor(final Offerable<E> offerable) {
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
    static final class Queue2EventProcessor<E> implements EventProcessor<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 2614647542607064042L;

        /** The queue we are wrapping. */
        private final Queue<E> queue;

        /**
         * Creates a new QueueToEventProcessor.
         * 
         * @param queue
         *            the Queue to wrap
         * @throws NullPointerException
         *             if the specified queue is <code>null</code>
         */
        public Queue2EventProcessor(final Queue<E> queue) {
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
}
