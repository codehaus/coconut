/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package jsr166y.forkjoin;
import static jsr166y.forkjoin.Ops.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.lang.reflect.Array;

/**
 * A prefix view of ParallelLongArray that causes operations to apply
 * to mappings of elements, not to the elements themselves.
 * Instances of this class may be constructed only via prefix
 * methods of ParallelLongArray or its other prefix classes.
 */
public abstract class ParallelLongArrayWithMapping<U> extends PAS.LPrefix {
    ParallelLongArrayWithMapping
        (ForkJoinExecutor ex, int firstIndex, int upperBound, long[] array) {
        super(ex, firstIndex, upperBound, array);
    }

    /**
     * Applies the given procedure to mapped elements
     * @param procedure the procedure
     */
    public void apply(Procedure<? super U> procedure) {
        ex.invoke(new PAS.FJOApply
                  (this, firstIndex, upperBound, null, procedure));
    }

    /**
     * Returns reduction of mapped elements
     * @param reducer the reducer
     * @param base the result for an empty array
     * @return reduction
     */
    public U reduce(Reducer<U> reducer, U base) {
        PAS.FJOReduce f = new PAS.FJOReduce
            (this, firstIndex, upperBound, null, reducer, base);
        ex.invoke(f);
        return (U)(f.result);
    }

    /**
     * Returns the index of some element matching bound and filter
     * constraints, or -1 if none.
     * @return index of matching element, or -1 if none.
     */
    public int anyIndex() {
        return super.computeAnyIndex();
    }

    /**
     * Returns mapping of some element matching bound and filter
     * constraints, or null if none.
     * @return mapping of matching element, or null if none.
     */
    public U any() {
        int i = super.computeAnyIndex();
        return (i < 0)? null : (U)oget(i);
    }

    /**
     * Returns the minimum mapped element, or null if empty
     * @param comparator the comparator
     * @return minimum mapped element, or null if empty
     */
    public U min(Comparator<? super U> comparator) {
        return reduce(Ops.<U>minReducer(comparator), null);
    }

    /**
     * Returns the minimum mapped element, or null if empty,
     * assuming that all elements are Comparables
     * @return minimum mapped element, or null if empty
     * @throws ClassCastException if any element is not Comparable.
     */
    public U min() {
        return reduce((Reducer<U>)(Ops.castedMinReducer()), null);
    }

    /**
     * Returns the maximum mapped element, or null if empty
     * @param comparator the comparator
     * @return maximum mapped element, or null if empty
     */
    public U max(Comparator<? super U> comparator) {
        return reduce(Ops.<U>maxReducer(comparator), null);
    }

    /**
     * Returns the maximum mapped element, or null if empty
     * assuming that all elements are Comparables
     * @return maximum mapped element, or null if empty
     * @throws ClassCastException if any element is not Comparable.
     */
    public U max() {
        return reduce((Reducer<U>)(Ops.castedMaxReducer()), null);
    }

    /**
     * Returns summary statistics, using the given comparator
     * to locate minimum and maximum elements.
     * @param comparator the comparator to use for
     * locating minimum and maximum elements
     * @return the summary.
     */
    public ParallelArray.SummaryStatistics<U> summary
        (Comparator<? super U> comparator) {
        PAS.FJOStats f = new PAS.FJOStats
            (this, firstIndex, upperBound, null, comparator);
        ex.invoke(f);
        return (ParallelArray.SummaryStatistics<U>)f;
    }

    /**
     * Returns summary statistics, assuming that all elements are
     * Comparables
     * @return the summary.
     */
    public ParallelArray.SummaryStatistics<U> summary() {
        PAS.FJOStats f = new PAS.FJOStats
            (this, firstIndex, upperBound, null,
             (Comparator<? super U>)(Ops.castedComparator()));
        ex.invoke(f);
        return (ParallelArray.SummaryStatistics<U>)f;
    }

    /**
     * Returns a new ParallelArray holding mapped elements
     * @return a new ParallelArray holding mapped elements
     */
    public ParallelArray<U> all() {
        return new ParallelArray<U>(ex, (U[])allObjects(null));
    }

    /**
     * Returns a new ParallelArray with the given element type holding
     * all elements
     * @param elementType the type of the elements
     * @return a new ParallelArray holding all elements
     */
    public ParallelArray<U> all(Class<? super U> elementType) {
        return new ParallelArray<U>(ex, (U[])allObjects(elementType));
    }

    /**
     * Return the number of elements selected using bound or
     * filter restrictions. Note that this method must evaluate
     * all selectors to return its result.
     * @return the number of elements
     */
    public int size() {
        return super.computeSize();
    }

    /**
     * Returns an operation prefix that causes a method to operate
     * on mapped elements of the array using the given op
     * applied to current op's results
     * @param op the op
     * @return operation prefix
     */
    public abstract <V> ParallelLongArrayWithMapping<V> withMapping
        (Op<? super U, ? extends V> op);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mapped elements of the array using the given op
     * applied to current op's results
     * @param op the op
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithLongMapping withMapping(ObjectToLong<? super U> op);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mapped elements of the array using the given op
     * applied to current op's results
     * @param op the op
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithDoubleMapping withMapping
        (ObjectToDouble<? super U> op);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V,W> ParallelLongArrayWithMapping<W> withMapping
        (BinaryOp<? super U, ? super V, ? extends W> combiner,
         ParallelArray<V> other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelLongArrayWithMapping<V> withMapping
        (ObjectAndDoubleToObject<? super U, ? extends V> combiner,
         ParallelDoubleArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelLongArrayWithMapping<V> withMapping
        (ObjectAndLongToObject<? super U, ? extends V> combiner,
         ParallelLongArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelLongArrayWithDoubleMapping withMapping
        (ObjectAndObjectToDouble<? super U, ? super V> combiner,
         ParallelArray<V> other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithDoubleMapping withMapping
        (ObjectAndDoubleToDouble<? super U> combiner,
         ParallelDoubleArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithDoubleMapping withMapping
        (ObjectAndLongToDouble<? super U> combiner,
         ParallelLongArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelLongArrayWithLongMapping withMapping
        (ObjectAndObjectToLong<? super U, ? super V> combiner,
         ParallelArray<V> other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithLongMapping withMapping
        (ObjectAndDoubleToLong<? super U> combiner,
         ParallelDoubleArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithLongMapping withMapping
        (ObjectAndLongToLong<? super U> combiner,
         ParallelLongArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mappings of this array using the given mapper that
     * accepts as arguments an element's current index and value
     * (as mapped by preceding mappings, if any), and produces a
     * new value.
     * @param mapper the mapper
     * @return operation prefix
     */
    public abstract <V> ParallelLongArrayWithMapping<V> withIndexedMapping
        (IntAndObjectToObject<? super U, ? extends V> mapper);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mappings of this array using the given mapper that
     * accepts as arguments an element's current index and value
     * (as mapped by preceding mappings, if any), and produces a
     * new value.
     * @param mapper the mapper
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithDoubleMapping withIndexedMapping
        (IntAndObjectToDouble<? super U> mapper);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mappings of this array using the given mapper that
     * accepts as arguments an element's current index and value
     * (as mapped by preceding mappings, if any), and produces a
     * new value.
     * @param mapper the mapper
     * @return operation prefix
     */
    public abstract ParallelLongArrayWithLongMapping withIndexedMapping
        (IntAndObjectToLong<? super U> mapper);

    /**
     * Returns an Iterable view to sequentially step through mapped
     * elements also obeying bound and filter constraints, without
     * performing computations to evaluate them in parallel
     * @return the Iterable view
     */
    public Iterable<U> sequentially() {
        return new Sequentially<U>();
    }
}

