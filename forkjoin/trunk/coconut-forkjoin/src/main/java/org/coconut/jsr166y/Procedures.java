/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.jsr166y;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Queue;

import jsr166y.forkjoin.Ops.Procedure;


/**
 * Various implementations of {@link Procedure}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Procedures {

    /**
     * A Procedure that does nothing.
     */
    public final static Procedure IGNORE_PROCEDURE = new NoopProcedure();

    /**
     * A Procedure that prints the argument object to {@link System#out} using its
     * {@link Object#toString()} and {@link PrintStream#print(String)}.
     */
    public final static Procedure SYS_OUT_PRINT_PROCEDURE = new SystemOutPrintProcedure();

    /**
     * A Procedure that prints the argument object to {@link System#out} using its
     * {@link Object#toString()} and {@link PrintStream#println(String)} .
     */
    public final static Procedure SYS_OUT_PRINTLN_PROCEDURE = new SystemOutPrintlnProcedure();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Procedures() {}

    // /CLOVER:ON
    /**
     * Wraps the {@link Collection#add(Object)} method in an {@link Procedure}.
     * <p>
     * The returned Procedure is serializable if the specified Collection is serializable.
     *
     * @param collection
     *            the collection to wrap
     * @return the newly created Procedure
     * @throws NullPointerException
     *             if the specified collection is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the specified Collection
     */
    public static <E> Procedure<E> collectionAdd(Collection<? super E> collection) {
        return new CollectionAdd<E>(collection);
    }

    /**
     * Returns a Procedure that does nothing.
     *
     * @return a Procedure that does nothing.
     * @param <T>
     *            the types of elements accepted by the specified Collection
     */
    public static <T> Procedure<T> ignore() {
        return IGNORE_PROCEDURE;
    }

    /**
     * Wraps the {@link Queue#offer(Object)} method in an {@link Procedure}.
     * <p>
     * The returned Procedure is serializable if the specified Queue is serializable.
     *
     * @param queue
     *            the queue to wrap
     * @return the newly created Procedure
     * @throws NullPointerException
     *             if the specified queue is <code>null</code>
     * @param <E>
     *            the types of elements accepted by the specified Queue
     */
    public static <E> Procedure<E> queueOffer(Queue<? super E> queue) {
        return new QueueOffer<E>(queue);
    }

    /**
     * Returns a Procedure that calls {@link PrintStream#print(boolean)} on
     * {@link System#out}.
     *
     * @return an eventhandler that prints all processed elements to {@link System#out}
     * @param <E>
     *            the types of elements accepted by the procedure
     */
    public static <E> Procedure<E> systemOutPrint() {
        return SYS_OUT_PRINT_PROCEDURE;
    }

    /**
     * Returns a Procedure that calls {@link PrintStream#println(boolean)} on
     * {@link System#out}.
     *
     * @return an eventhandler that prints all processed elements to {@link System#out}
     * @param <E>
     *            the types of elements accepted by the procedure
     */
    public static <E> Procedure<E> systemOutPrintln() {
        return SYS_OUT_PRINTLN_PROCEDURE;
    }

    /**
     * Wraps the {@link Collection#add(Object)} method in an {@link Procedure}.
     */
    static final class CollectionAdd<E> implements Procedure<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7596093393453935419L;

        /** The collection we are wrapping. */
        private final Collection<? super E> collection;

        /**
         * Creates a new CollectionAdd.
         *
         * @param collection
         *            the Collection to wrap
         * @throws NullPointerException
         *             if the specified collection is <code>null</code>
         */
        public CollectionAdd(Collection<? super E> collection) {
            if (collection == null) {
                throw new NullPointerException("collection is null");
            }
            this.collection = collection;
        }

        /** {@inheritDoc} */
        public void op(E element) {
            collection.add(element); // ignore return value
        }
    }

    /**
     * A Procedure that does nothing.
     */
    final static class NoopProcedure implements Procedure, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new NoopProcedure. */
        NoopProcedure() {}

        /** {@inheritDoc} */
        public void op(Object t) {}

        /** @return Preserves singleton property */
        private Object readResolve() {
            return IGNORE_PROCEDURE;
        }
    }

    /**
     * Wraps the {@link Queue#offer(Object)} method in an {@link Procedure}.
     */
    static final class QueueOffer<E> implements Procedure<E>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 2614647542607064042L;

        /** The queue we are wrapping. */
        private final Queue<? super E> queue;

        /**
         * Creates a new QueueOffer.
         *
         * @param queue
         *            the Queue to wrap
         * @throws NullPointerException
         *             if the specified queue is <code>null</code>
         */
        public QueueOffer(final Queue<? super E> queue) {
            if (queue == null) {
                throw new NullPointerException("queue is null");
            }
            this.queue = queue;
        }

        /** {@inheritDoc} */
        public void op(E element) {
            queue.offer(element); // ignore return value
        }
    }

    /**
     * A Procedure that calls {@link PrintStream#println(boolean)} on {@link System#out}.
     */
    final static class SystemOutPrintlnProcedure implements Procedure, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 2524986731849709296L;

        /** Creates a new NoopProcedure. */
        SystemOutPrintlnProcedure() {}

        /** {@inheritDoc} */
        public void op(Object t) {
            System.out.println(t);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return SYS_OUT_PRINTLN_PROCEDURE;
        }
    }

    /**
     * A Procedure that calls {@link PrintStream#print(boolean)} on {@link System#out}.
     */
    final static class SystemOutPrintProcedure implements Procedure, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 5504109727345743170L;

        /** Creates a new NoopProcedure. */
        SystemOutPrintProcedure() {}

        /** {@inheritDoc} */
        public void op(Object t) {
            System.out.print(t);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return SYS_OUT_PRINT_PROCEDURE;
        }
    }
}
