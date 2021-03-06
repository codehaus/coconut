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
 * A prefix view of ParallelArray that causes operations to apply
 * to mappings of elements, not to the elements themselves.
 * Instances of this class may be constructed only via prefix
 * methods of ParallelDoubleArray or its other prefix classes.
 */
public abstract class ParallelDoubleArrayWithDoubleMapping extends PAS.DPrefix {
    ParallelDoubleArrayWithDoubleMapping
        (ForkJoinExecutor ex, int firstIndex, int upperBound, double[] array) {
        super(ex, firstIndex, upperBound, array);
    }

    /**
     * Applies the given procedure to elements
     * @param procedure the procedure
     */
    public void apply(DoubleProcedure procedure) {
        ex.invoke(new PAS.FJDApply
                  (this, firstIndex, upperBound, null, procedure));
    }

    /**
     * Returns reduction of elements
     * @param reducer the reducer
     * @param base the result for an empty array
     * @return reduction
     */
    public double reduce(DoubleReducer reducer, double base) {
        PAS.FJDReduce f = new PAS.FJDReduce
            (this, firstIndex, upperBound, null, reducer, base);
        ex.invoke(f);
        return f.result;
    }

    /**
     * Returns the minimum element, or Double.MAX_VALUE if empty
     * @return minimum element, or Double.MAX_VALUE if empty
     */
    public double min() {
        return reduce(naturalDoubleMinReducer(), Double.MAX_VALUE);
    }

    /**
     * Returns the minimum element, or Double.MAX_VALUE if empty
     * @param comparator the comparator
     * @return minimum element, or Double.MAX_VALUE if empty
     */
    public double min(DoubleComparator comparator) {
        return reduce(doubleMinReducer(comparator), Double.MAX_VALUE);
    }

    /**
     * Returns the maximum element, or -Double.MAX_VALUE if empty
     * @return maximum element, or -Double.MAX_VALUE if empty
     */
    public double max() {
        return reduce(naturalDoubleMaxReducer(), -Double.MAX_VALUE);
    }

    /**
     * Returns the maximum element, or -Double.MAX_VALUE if empty
     * @param comparator the comparator
     * @return maximum element, or -Double.MAX_VALUE if empty
     */
    public double max(DoubleComparator comparator) {
        return reduce(doubleMaxReducer(comparator), -Double.MAX_VALUE);
    }

    /**
     * Returns the sum of elements
     * @return the sum of elements
     */
    public double sum() {
        return reduce(Ops.doubleAdder(), 0);
    }

    /**
     * Returns summary statistics
     * @param comparator the comparator to use for
     * locating minimum and maximum elements
     * @return the summary.
     */
    public ParallelDoubleArray.SummaryStatistics summary
        (DoubleComparator comparator) {
        PAS.FJDStats f = new PAS.FJDStats
            (this, firstIndex, upperBound, null, comparator);
        ex.invoke(f);
        return f;
    }

    /**
     * Returns summary statistics, using natural comparator
     * @return the summary.
     */
    public ParallelDoubleArray.SummaryStatistics summary() {
        PAS.FJDStats f = new PAS.FJDStats
            (this, firstIndex, upperBound, null,naturalDoubleComparator());
        ex.invoke(f);
        return f;
    }

    /**
     * Returns a new ParallelDoubleArray holding elements
     * @return a new ParallelDoubleArray holding elements
     */
    public ParallelDoubleArray all() {
        return new ParallelDoubleArray(ex, allDoubles());
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
     * Returns the index of some element matching bound and filter
     * constraints, or -1 if none.
     * @return index of matching element, or -1 if none.
     */
    public int anyIndex() {
        return super.computeAnyIndex();
    }

    /**
     * Returns an operation prefix that causes a method to operate
     * on mapped elements of the array using the given op.
     * @param op the op
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithDoubleMapping withMapping(DoubleOp op);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mapped elements of the array using the given op.
     * @param op the op
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithLongMapping withMapping(DoubleToLong op);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mapped elements of the array using the given op.
     * @param op the op
     * @return operation prefix
     */
    public abstract <U> ParallelDoubleArrayWithMapping<U> withMapping
        (DoubleToObject<? extends U> op);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V,W> ParallelDoubleArrayWithMapping<W> withMapping
        (DoubleAndObjectToObject<? super V, ? extends W> combiner,
         ParallelArray<V> other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelDoubleArrayWithMapping<V> withMapping
        (DoubleAndDoubleToObject<? extends V> combiner,
         ParallelDoubleArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelDoubleArrayWithMapping<V> withMapping
        (DoubleAndLongToObject<? extends V> combiner,
         ParallelLongArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelDoubleArrayWithDoubleMapping withMapping
        (DoubleAndObjectToDouble<? super V> combiner,
         ParallelArray<V> other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithDoubleMapping withMapping
        (BinaryDoubleOp combiner,
         ParallelDoubleArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithDoubleMapping withMapping
        (DoubleAndLongToDouble combiner,
         ParallelLongArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract <V> ParallelDoubleArrayWithLongMapping withMapping
        (DoubleAndObjectToLong<? super V> combiner,
         ParallelArray<V> other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithLongMapping withMapping
        (DoubleAndDoubleToLong combiner,
         ParallelDoubleArray other);

    /**
     * Returns an operation prefix that causes a method to operate
     * on binary mappings of this array and the other array.
     * @param combiner the combiner
     * @param other the other array
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithLongMapping withMapping
        (DoubleAndLongToLong combiner,
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
    public abstract <V> ParallelDoubleArrayWithMapping<V> withIndexedMapping
        (IntAndDoubleToObject<? extends V> mapper);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mappings of this array using the given mapper that
     * accepts as arguments an element's current index and value
     * (as mapped by preceding mappings, if any), and produces a
     * new value.
     * @param mapper the mapper
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithDoubleMapping withIndexedMapping
        (IntAndDoubleToDouble mapper);

    /**
     * Returns an operation prefix that causes a method to operate
     * on mappings of this array using the given mapper that
     * accepts as arguments an element's current index and value
     * (as mapped by preceding mappings, if any), and produces a
     * new value.
     * @param mapper the mapper
     * @return operation prefix
     */
    public abstract ParallelDoubleArrayWithLongMapping withIndexedMapping
        (IntAndDoubleToLong mapper);

    /**
     * Returns an Iterable view to sequentially step through mapped
     * elements also obeying bound and filter constraints, without
     * performing computations to evaluate them in parallel
     * @return the Iterable view
     */
    public Iterable<Double> sequentially() {
        return new SequentiallyAsDouble();
    }

}
