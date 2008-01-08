/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Queue;

import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Procedure;

/**
 * Various implementations of {@link Procedure}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
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

    public static <F, T> Procedure<F> mapAndApply(final Mapper<F, T> mapper,
            Procedure<? super T> procedure) {
        return new MapAndApplyPredicate<F, T>(mapper, procedure);
    }

    /**
     * A Predicate that first applies the specified mapper to the argument before
     * evaluating the specified predicate.
     */
    final static class MapAndApplyPredicate<F, T> implements Procedure<F>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6292758840373110577L;

        /** The mapper used to map the element. */
        private final Mapper<F, T> mapper;

        /** The procedure to to apply the mapped value on. */
        private final Procedure<? super T> procedure;

        /**
         * Creates a new MapAndEvaluatePredicate.
         * 
         * @param mapper
         *            the mapper used to first map the argument
         * @param procedure
         *            the predicate used to evaluate the mapped argument
         */
        public MapAndApplyPredicate(Mapper<F, T> mapper, Procedure<? super T> procedure) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            } else if (procedure == null) {
                throw new NullPointerException("procedure is null");
            }
            this.procedure = procedure;
            this.mapper = mapper;
        }

        public void apply(F element) {
            procedure.apply(mapper.map(element));
        }

        /**
         * Returns the mapper that will map the object before applying the predicate on
         * it.
         * 
         * @return the mapper that will map the object before applying the predicate on it
         */
        public Mapper<F, T> getMapper() {
            return mapper;
        }

        /**
         * Returns the Procedure we are testing against.
         * 
         * @return the Procedure we are testing against.
         */
        public Procedure<? super T> getProcedure() {
            return procedure;
        }
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
        public void apply(E element) {
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
        public void apply(Object t) {}

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
        public void apply(E element) {
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
        public void apply(Object t) {
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
        public void apply(Object t) {
            System.out.print(t);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return SYS_OUT_PRINT_PROCEDURE;
        }
    }
}
