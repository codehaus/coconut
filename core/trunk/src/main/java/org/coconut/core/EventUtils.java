/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.Serializable;

import org.coconut.operations.Ops.Procedure;

/**
 * Factory and utility methods for {@link Procedure} and {@link Offerable} classes defined
 * in this package. This class supports the following kinds of methods:
 * <ul>
 * <li>Methods that create and return an {@link Offerable} that performs common tasks.
 * <li>Methods that create and return an {@link Procedure} that performs common tasks.
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
     * Returns an Procedure that ignores any call to {@link Procedure#apply(Object)}.
     * <p>
     * The returned Procedure is serializable.
     *
     * @return an Procedure that ignores any call to {@link Procedure#apply(Object)}
     * @param <E>
     *            the types of elements accepted by Procedure
     */
    @SuppressWarnings("unchecked")
    public static <E> Procedure<E> dummyProcedure() {
        return (Procedure<E>) fromOfferable(dummyOfferableFalse());
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
     * Wraps an {@link Offerable} in an {@link Procedure}.
     * <p>
     * The returned Procedure is serializable if the specified Offerable is
     * serializable.
     *
     * @param offerable
     *            the offerable to wrap
     * @return an Procedure wrapping an Offerable
     * @throws NullPointerException
     *             if the specified offerable is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the specified Offerable
     */
    public static <E> Procedure<E> fromOfferable(final Offerable<E> offerable) {
        return new Offerable2Procedure<E>(offerable);
    }

    /**
     * Wraps an {@link Procedure} in an {@link Offerable}. The offer method of the
     * returned Offerable will return <code>true</code> for all values parsed to the
     * specified event processor.
     * <p>
     * The returned Offerable is serializable if the specified Procedure is
     * serializable.
     *
     * @param processor
     *            the Procedure to wrap
     * @return an Offerable wrapping an Procedure
     * @throws NullPointerException
     *             if the specified processor is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the process method
     */
    public static <E> Offerable<E> toOfferable(final Procedure<E> processor) {
        return new Procedure2Offerable<E>(processor);
    }

    /**
     * Wraps an {@link Offerable} in an {@link Procedure}.The offer method of the
     * returned Offerable will return <code>true</code> for all values parsed to the
     * specified event processor. However, unlike {@link #toOfferable(Procedure)} any
     * runtime thrown by the Procedure will be catched in the returned offerable and
     * <code>false</code> will be returned.
     * <p>
     * The returned Offerable is serializable if the specified Procedure is
     * serializable.
     *
     * @param processor
     *            the Procedure to wrap
     * @return an Procedure wrapped in a Offerable
     * @throws NullPointerException
     *             if the specified processor is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the process method
     */
    public static <E> Offerable<E> toOfferableSafe(final Procedure<E> processor) {
        return new Procedure2OfferableSafe<E>(processor);
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
     * Wraps an {@link Procedure} in an {@link Offerable}.
     *
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static final class Procedure2Offerable<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5555001640212350081L;

        /** The Procedure we are wrapping. */
        private final Procedure<E> processor;

        /**
         * Creates a new Procedure2Offerable.
         *
         * @param processor
         *            the Procedure to wrap
         * @throws NullPointerException
         *             if the supplied Procedure is <code>null</code>
         */
        public Procedure2Offerable(final Procedure<E> processor) {
            if (processor == null) {
                throw new NullPointerException("processor is null");
            }
            this.processor = processor;
        }

        /** {@inheritDoc} */
        public boolean offer(E element) {
            processor.apply(element);
            return true;
        }
    }

    /**
     * Wraps an {@link Procedure} in an {@link Offerable}. Catching any
     * {@link RuntimeException}'s.
     *
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static final class Procedure2OfferableSafe<E> implements Offerable<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5555001640212350081L;

        /** The Procedure we are wrapping. */
        private final Procedure<E> processor;

        /**
         * Creates a new Procedure2OfferableSafe.
         *
         * @param processor
         *            the Procedure to wrap
         * @throws NullPointerException
         *             if the supplied Procedure is <code>null</code>
         */
        public Procedure2OfferableSafe(final Procedure<E> processor) {
            if (processor == null) {
                throw new NullPointerException("processor is null");
            }
            this.processor = processor;
        }

        /** {@inheritDoc} */
        public boolean offer(E element) {
            try {
                processor.apply(element);
                return true;
            } catch (RuntimeException ex) {
                return false;
            }
        }
    }

    /**
     * Wraps an {@link Offerable} in an {@link Procedure}.
     *
     * @param <E>
     *            the types of parameters accepted by the offer method
     */
    static final class Offerable2Procedure<E> implements Procedure<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5017539960531630701L;

        /** The offerable that is being wrapped. */
        private final Offerable<E> offerable;

        /**
         * Creates a new Offerable2Procedure.
         *
         * @param offerable
         *            the Offerable to wrap
         * @throws NullPointerException
         *             if the specified offerable is <code>null</code>
         */
        public Offerable2Procedure(final Offerable<E> offerable) {
            if (offerable == null) {
                throw new NullPointerException("offerable is null");
            }
            this.offerable = offerable;
        }

        /** {@inheritDoc} */
        public void apply(E element) {
            offerable.offer(element);
        }
    }
}
