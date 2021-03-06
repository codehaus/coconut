/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package org.coconut.forkjoin;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongArray;

import org.coconut.operations.Ops.Combiner;
import org.coconut.operations.Ops.DoubleComparator;
import org.coconut.operations.Ops.DoubleGenerator;
import org.coconut.operations.Ops.DoubleMapper;
import org.coconut.operations.Ops.DoublePredicate;
import org.coconut.operations.Ops.DoubleProcedure;
import org.coconut.operations.Ops.DoubleReducer;
import org.coconut.operations.Ops.Generator;
import org.coconut.operations.Ops.LongComparator;
import org.coconut.operations.Ops.LongGenerator;
import org.coconut.operations.Ops.LongMapper;
import org.coconut.operations.Ops.LongPredicate;
import org.coconut.operations.Ops.LongProcedure;
import org.coconut.operations.Ops.LongReducer;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.MapperFromInt;
import org.coconut.operations.Ops.MapperFromIntToDouble;
import org.coconut.operations.Ops.MapperFromIntToLong;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;
import org.coconut.operations.Ops.Reducer;

/**
 * Shared internal support for ParallelArray and specializations.
 *
 * The majority of operations take a similar form: Class Prefix serves
 * as the base of prefix classes, also serving as parameters for
 * single-step fork+join parallel tasks using subclasses of FJBase and
 * FJSearchBase. Prefix instances hold the non-operation-specific
 * control and data accessors needed for a task as a whole (as opposed
 * to subtasks), and also house leaf methods that perform the actual
 * array processing. The leaf methods are for the most part just plain
 * array operations. They are boringly repetitive in order to flatten
 * out and minimize inner-loop overhead, as well as to minimized
 * call-chain depth. This makes it more likely that dynamic compilers
 * can go the rest of the way, and hoist per-element method call
 * dispatch, so we have a good chance to speed up processing via
 * parallelism rather than lose due to dispatch and indirection
 * overhead. The dispatching from Prefix to FJ and back is otherwise
 * Visitor-pattern-like, allowing the basic parallelism control for
 * most FJ tasks to be centralized.
 *
 * Operations taking forms other than single-step fork/join
 * (SelectAll, sort, scan, etc) are organized in basically similar
 * ways, but don't always follow as regular patterns.
 *
 * Note the extensive use of raw types. Arrays and generics do not
 * work together very well. It is more manageable to avoid them here,
 * and let the public classes perform casts in and out to the
 * processing here.
 */
class PAS {
    private PAS() {} // all-static, non-instantiable

    /** Global default executor */
    private static ForkJoinPool defaultExecutor;
    /** Lock for on-demand initialization of defaultExecutor */
    private static final Object poolLock = new Object();

    static ForkJoinExecutor defaultExecutor() {
        synchronized(poolLock) {
            ForkJoinPool p = defaultExecutor;
            if (p == null) {
                // use ceil(7/8 * ncpus)
                int nprocs = Runtime.getRuntime().availableProcessors();
                int nthreads = nprocs - (nprocs >>> 3);
                defaultExecutor = p = new ForkJoinPool(nthreads);
            }
            return p;
        }
    }

    /**
     * Base of prefix classes.
     */
    static abstract class Prefix {
        final ForkJoinExecutor ex;
        final int firstIndex;
        final int upperBound;
        final int threshold; // subtask split control

        Prefix(ForkJoinExecutor ex, int firstIndex, int upperBound) {
            this.ex = ex;
            this.firstIndex = firstIndex;
            this.upperBound = upperBound;
            int n = upperBound - firstIndex;
            int p = ex.getParallelismLevel();
            this.threshold = defaultSequentialThreshold(n, p);
        }

        /**
         * Returns size threshold for splitting into subtask.  By
         * default, uses about 8 times as many tasks as threads
         */
        static int defaultSequentialThreshold(int size, int procs) {
            return (procs > 1) ? (1 + size / (procs << 3)) : size;
        }

        /**
         * Divide-and conquer split control. Returns true if subtask
         * of size n should be split in half.
         */
        final boolean shouldSplit(int n) {
            return n > threshold;
        }

        /**
         * Array access methods for ref, double, long. Checking
         * for null return is used as a sort of type test.
         */
        abstract Object[] rgetArray();
        abstract double[] dgetArray();
        abstract long[]  lgetArray();

        /*
         * Inner leaf methods. Most are no-ops overriddden in concrete
         * classes. Some of them can never be called from some
         * contexts and so are never overridden in the associated leaf
         * classes.  For example the double version of leafApply can't
         * be called from any ParallelArray.WithBounds method, but can
         * be in ParallelArray.WithBoundedDoubleMapping, Introducing
         * them all here, regardless, simplifies usage in the FJ* and
         * prefix classes.
         */

        void leafApply(int lo, int hi, Procedure procedure) {}
        void leafApply(int lo, int hi, DoubleProcedure procedure) {}
        void leafApply(int lo, int hi, LongProcedure procedure) {}

        Object leafReduce(int lo, int hi,
                          Reducer reducer, Object base) { return null; }
        double leafReduce(int lo, int hi,
                          DoubleReducer reducer, double base) { return 0.0; }
        long leafReduce(int lo, int hi,
                        LongReducer reducer, long base) { return 0L; }

        void leafStats(int lo, int hi, FJRStats task) {}
        void leafStats(int lo, int hi, FJDStats task) {}
        void leafStats(int lo, int hi, FJLStats task) {}

        // copy elements, ignoring selector, but applying mapping
        void leafTransfer(int lo, int hi, Object[] dest, int offset) {}
        void leafTransfer(int lo, int hi, double[] dest, int offset) {}
        void leafTransfer(int lo, int hi, long[] dest, int offset) {}

        // copy elements indexed in indices[loIdx..hiIdx], ignoring
        // selector, but applying mapping
        void leafTransferByIndex(int[] indices, int loIdx, int hiIdx,
                                 Object[] dest, int offset) {}
        void leafTransferByIndex(int[] indices, int loIdx, int hiIdx,
                                 double[] dest, int offset) {}
        void leafTransferByIndex(int[] indices, int loIdx, int hiIdx,
                                 long[] dest, int offset) {}

        void leafCombineInPlace(int lo, int hi, Object[] other,
                                int otherOffset, Reducer combiner) {}
        void leafCombineInPlace(int lo, int hi, double[] other,
                                int otherOffset, DoubleReducer combiner) {}
        void leafCombineInPlace(int lo, int hi, long[] other,
                                int otherOffset, LongReducer combiner) {}

        void leafCombine(int lo, int hi, Object[] other, int otherOffset,
                         Object[] dest, Combiner combiner) {}
        void leafCombine(int lo, int hi, long[] other, int otherOffset,
                         long[] dest, LongReducer combiner) {}
        void leafCombine(int lo, int hi, double[] other, int otherOffset,
                         double[] dest, DoubleReducer combiner) {}

        void leafTransform(int lo, int hi, Mapper mapper) {}
        void leafTransform(int lo, int hi, DoubleMapper mapper) {}
        void leafTransform(int lo, int hi, LongMapper mapper) {}

        void leafIndexMap(int lo, int hi, MapperFromInt mapper) {}
        void leafIndexMap(int lo, int hi, MapperFromIntToDouble mapper) {}
        void leafIndexMap(int lo, int hi, MapperFromIntToLong mapper) {}

        void leafGenerate(int lo, int hi, Generator generator) {}
        void leafGenerate(int lo, int hi, DoubleGenerator generator) {}
        void leafGenerate(int lo, int hi, LongGenerator generator) {}

        void leafFillValue(int lo, int hi, Object value) {}
        void leafFillValue(int lo, int hi, double value) {}
        void leafFillValue(int lo, int hi, long value) {}

        // add indices of selected elements to index array; return #added
        int leafIndexSelected(int lo, int hi, boolean positive,
                              int[] indices) { return 0; }

        // move selected elements to indices starting at offset,
        // return final offset
        int leafMoveSelected(int lo, int hi, int offset,
                             boolean positive) { return 0; }

        // move elements indexed by indices[loIdx...hiIdx] starting
        // at given offset
        abstract void leafMoveByIndex(int[] indices, int loIdx,
                                      int hiIdx, int offset);

    }

    /**
     * Base of ref array prefix classes
     */
    static abstract class RPrefix extends PAS.Prefix {
        final ParallelArray pa;
        RPrefix(ParallelArray pa, int firstIndex, int upperBound) {
            super(pa.ex, firstIndex, upperBound);
            this.pa = pa;
        }

        final Object[] rgetArray() { return pa.array; }
        final double[] dgetArray() { return null; }
        final long[]  lgetArray() { return null; }

        final void leafMoveByIndex(int[] indices, int loIdx,
                                   int hiIdx, int offset) {
            final Object[] array = pa.array;
            for (int i = loIdx; i < hiIdx; ++i)
                array[offset++] = array[indices[i]];
        }
    }

    /**
     * Base of double array prefix classes
     */
    static abstract class DPrefix extends PAS.Prefix {
        final ParallelDoubleArray pa;
        DPrefix(ParallelDoubleArray pa, int firstIndex, int upperBound) {
            super(pa.ex, firstIndex, upperBound);
            this.pa = pa;
        }

        final Object[] rgetArray() { return null; }
        final double[] dgetArray() { return pa.array; }
        final long[]  lgetArray() { return null; }

        final void leafMoveByIndex(int[] indices, int loIdx,
                                   int hiIdx, int offset) {
            final double[] array = pa.array;
            for (int i = loIdx; i < hiIdx; ++i)
                array[offset++] = array[indices[i]];
        }
    }

    /**
     * Base of long array prefix classes
     */
    static abstract class LPrefix extends PAS.Prefix {
        final ParallelLongArray pa;
        LPrefix(ParallelLongArray pa, int firstIndex, int upperBound) {
            super(pa.ex, firstIndex, upperBound);
            this.pa = pa;
        }

        final Object[] rgetArray() { return null; }
        final double[] dgetArray() { return null; }
        final long[]  lgetArray() { return pa.array; }

        final void leafMoveByIndex(int[] indices, int loIdx,
                                   int hiIdx, int offset) {
            final long[] array = pa.array;
            for (int i = loIdx; i < hiIdx; ++i)
                array[offset++] = array[indices[i]];
        }
    }

    /**
     * Base for most divide-and-conquer tasks used for computing
     * ParallelArray operations. Rather than pure recursion, it links
     * right-hand-sides and then joins up the tree, exploiting cases
     * where tasks aren't stolen.  This generates and joins tasks with
     * a bit less overhead than pure recursive style -- there are only
     * as many tasks as leaves (no strictly internal nodes).
     *
     * Split control relies on prefix.shouldSplit, which is expected
     * to err on the side of generating too many tasks. To
     * counterblance, if a task pops off its smallest subtask, it
     * directly runs its leaf action rather than possibly replitting.
     *
     * There are, with a few exceptions, three flavors of each FJBase
     * subclass, prefixed FJR (reference), FJD (double) and FJL
     * (long). These in turn normally dispatch to the ref-based,
     * double-based, or long-based leaf* methods.
     */
    static abstract class FJBase extends RecursiveAction {
        final Prefix prefix;
        final int lo;
        final int hi;
        final FJBase next; // the next task that creator should join
        FJBase(Prefix prefix, int lo, int hi, FJBase next) {
            this.prefix = prefix;
            this.lo = lo;
            this.hi = hi;
            this.next = next;
        }

        public final void compute() {
            FJBase r = null;
            int l = lo;
            int h = hi;
            while (prefix.shouldSplit(h - l)) {
                int rh = h;
                h = (l + h) >>> 1;
                (r = newSubtask(h, rh, r)).fork();
            }
            atLeaf(l, h);
            while (r != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(r))
                    r.atLeaf(r.lo, r.hi);
                else
                    r.join();
                onReduce(r);
                r = r.next;
            }
        }

        /** Leaf computation */
        abstract void atLeaf(int l, int h);
        /** Operation performed after joining right subtask -- default noop */
        void onReduce(FJBase right) {}
        /** Factory method to create new subtask, normally of current type */
        abstract FJBase newSubtask(int l, int h, FJBase r);
    }

    // apply

    static final class FJRApply extends FJBase {
        final Procedure procedure;
        FJRApply(Prefix prefix, int lo, int hi, FJBase next,
                 Procedure procedure) {
            super(prefix, lo, hi, next);
            this.procedure = procedure;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRApply(prefix, l, h, r, procedure);
        }
        void atLeaf(int l, int h) {
            prefix.leafApply(l, h, procedure);
        }
    }

    static final class FJDApply extends FJBase {
        final DoubleProcedure procedure;
        FJDApply(Prefix prefix, int lo, int hi, FJBase next,
                 DoubleProcedure procedure) {
            super(prefix, lo, hi, next);
            this.procedure = procedure;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDApply(prefix, l, h, r, procedure);
        }
        void atLeaf(int l, int h) {
            prefix.leafApply(l, h, procedure);
        }
    }

    static final class FJLApply extends FJBase {
        final LongProcedure procedure;
        FJLApply(Prefix prefix, int lo, int hi, FJBase next,
                 LongProcedure procedure) {
            super(prefix, lo, hi, next);
            this.procedure = procedure;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLApply(prefix, l, h, r, procedure);
        }
        void atLeaf(int l, int h) {
            prefix.leafApply(l, h, procedure);
        }
    }

    // reduce

    static final class FJRReduce extends FJBase {
        final Reducer reducer;
        Object result;
        FJRReduce(Prefix prefix, int lo, int hi, FJBase next,
                  Reducer reducer, Object base) {
            super(prefix, lo, hi, next);
            this.reducer = reducer;
            this.result = base;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRReduce(prefix, l, h, r, reducer, result);
        }
        void atLeaf(int l, int h) {
            result = prefix.leafReduce(l, h, reducer, result);
        }
        void onReduce(FJBase right) {
            result = reducer.combine(result, ((FJRReduce)right).result);
        }
    }

    static final class FJDReduce extends FJBase {
        final DoubleReducer reducer;
        double result;
        FJDReduce(Prefix prefix, int lo, int hi, FJBase next,
                  DoubleReducer reducer, double base) {
            super(prefix, lo, hi, next);
            this.reducer = reducer;
            this.result = base;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDReduce(prefix, l, h, r, reducer, result);
        }
        void atLeaf(int l, int h) {
            result = prefix.leafReduce(l, h, reducer, result);
        }
        void onReduce(FJBase right) {
            result = reducer.combine(result, ((FJDReduce)right).result);
        }
    }

    static final class FJLReduce extends FJBase {
        final LongReducer reducer;
        long result;
        FJLReduce(Prefix prefix, int lo, int hi, FJBase next,
                  LongReducer reducer, long base) {
            super(prefix, lo, hi, next);
            this.reducer = reducer;
            this.result = base;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLReduce(prefix, l, h, r, reducer, result);
        }
        void atLeaf(int l, int h) {
            result = prefix.leafReduce(l, h, reducer, result);
        }
        void onReduce(FJBase right) {
            result = reducer.combine(result, ((FJLReduce)right).result);
        }
    }

    // map

    static final class FJRMap extends FJBase {
        final Object[] dest;
        final int offset;
        FJRMap(Prefix prefix, int lo, int hi, FJBase next, Object[] dest,
               int offset) {
            super(prefix, lo, hi, next);
            this.dest = dest;
            this.offset = offset;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRMap(prefix, l, h, r, dest, offset);
        }
        void atLeaf(int l, int h) {
            prefix.leafTransfer(l, h, dest, l - offset);
        }
    }

    static final class FJDMap extends FJBase {
        final double[] dest;
        final int offset;
        FJDMap(Prefix prefix, int lo, int hi, FJBase next, double[] dest,
               int offset) {
            super(prefix, lo, hi, next);
            this.dest = dest;
            this.offset = offset;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDMap(prefix, l, h, r, dest, offset);
        }
        void atLeaf(int l, int h) {
            prefix.leafTransfer(l, h, dest, l - offset);
        }
    }

    static final class FJLMap extends FJBase {
        final long[] dest;
        final int offset;
        FJLMap(Prefix prefix, int lo, int hi, FJBase next, long[] dest,
               int offset) {
            super(prefix, lo, hi, next);
            this.dest = dest;
            this.offset = offset;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLMap(prefix, l, h, r, dest, offset);
        }
        void atLeaf(int l, int h) {
            prefix.leafTransfer(l, h, dest, l - offset);
        }
    }

    // transform

    static final class FJRTransform extends FJBase {
        final Mapper mapper;
        FJRTransform(Prefix prefix, int lo, int hi, FJBase next,
                     Mapper mapper) {
            super(prefix, lo, hi, next);
            this.mapper = mapper;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRTransform(prefix, l, h, r, mapper);
        }
        void atLeaf(int l, int h) {
            prefix.leafTransform(l, h, mapper);
        }
    }

    static final class FJDTransform extends FJBase {
        final DoubleMapper mapper;
        FJDTransform(Prefix prefix, int lo, int hi, FJBase next,
                     DoubleMapper mapper) {
            super(prefix, lo, hi, next);
            this.mapper = mapper;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDTransform(prefix, l, h, r, mapper);
        }
        void atLeaf(int l, int h) {
            prefix.leafTransform(l, h, mapper);
        }
    }

    static final class FJLTransform extends FJBase {
        final LongMapper mapper;
        FJLTransform(Prefix prefix, int lo, int hi, FJBase next,
                     LongMapper mapper) {
            super(prefix, lo, hi, next);
            this.mapper = mapper;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLTransform(prefix, l, h, r, mapper);
        }
        void atLeaf(int l, int h) {
            prefix.leafTransform(l, h, mapper);
        }
    }

    // index map

    static final class FJRIndexMap extends FJBase {
        final MapperFromInt mapper;
        FJRIndexMap(Prefix prefix, int lo, int hi, FJBase next,
                    MapperFromInt mapper) {
            super(prefix, lo, hi, next);
            this.mapper = mapper;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRIndexMap(prefix, l, h, r, mapper);
        }
        void atLeaf(int l, int h) {
            prefix.leafIndexMap(l, h, mapper);
        }
    }

    static final class FJDIndexMap extends FJBase {
        final MapperFromIntToDouble mapper;
        FJDIndexMap(Prefix prefix, int lo, int hi, FJBase next,
                    MapperFromIntToDouble mapper) {
            super(prefix, lo, hi, next);
            this.mapper = mapper;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDIndexMap(prefix, l, h, r, mapper);
        }
        void atLeaf(int l, int h) {
            prefix.leafIndexMap(l, h, mapper);
        }
    }

    static final class FJLIndexMap extends FJBase {
        final MapperFromIntToLong mapper;
        FJLIndexMap(Prefix prefix, int lo, int hi, FJBase next,
                    MapperFromIntToLong mapper) {
            super(prefix, lo, hi, next);
            this.mapper = mapper;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLIndexMap(prefix, l, h, r, mapper);
        }
        void atLeaf(int l, int h) {
            prefix.leafIndexMap(l, h, mapper);
        }
    }

    // generate

    static final class FJRGenerate extends FJBase {
        final Generator generator;
        FJRGenerate(Prefix prefix, int lo, int hi, FJBase next,
                    Generator generator) {
            super(prefix, lo, hi, next);
            this.generator = generator;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRGenerate(prefix, l, h, r, generator);
        }
        void atLeaf(int l, int h) {
            prefix.leafGenerate(l, h, generator);
        }
    }

    static final class FJDGenerate extends FJBase {
        final DoubleGenerator generator;
        FJDGenerate(Prefix prefix, int lo, int hi, FJBase next,
                    DoubleGenerator generator) {
            super(prefix, lo, hi, next);
            this.generator = generator;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDGenerate(prefix, l, h, r, generator);
        }
        void atLeaf(int l, int h) {
            prefix.leafGenerate(l, h, generator);
        }
    }

    static final class FJLGenerate extends FJBase {
        final LongGenerator generator;
        FJLGenerate(Prefix prefix, int lo, int hi, FJBase next,
                    LongGenerator generator) {
            super(prefix, lo, hi, next);
            this.generator = generator;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLGenerate(prefix, l, h, r, generator);
        }
        void atLeaf(int l, int h) {
            prefix.leafGenerate(l, h, generator);
        }
    }

    // fill

    static final class FJRFill extends FJBase {
        final Object value;
        FJRFill(Prefix prefix, int lo, int hi, FJBase next, Object value) {
            super(prefix, lo, hi, next);
            this.value = value;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRFill(prefix, l, h, r, value);
        }
        void atLeaf(int l, int h) {
            prefix.leafFillValue(l, h, value);
        }
    }

    static final class FJDFill extends FJBase {
        final double value;
        FJDFill(Prefix prefix, int lo, int hi, FJBase next, double value) {
            super(prefix, lo, hi, next);
            this.value = value;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDFill(prefix, l, h, r, value);
        }
        void atLeaf(int l, int h) {
            prefix.leafFillValue(l, h, value);
        }
    }

    static final class FJLFill extends FJBase {
        final long value;
        FJLFill(Prefix prefix, int lo, int hi, FJBase next, long value) {
            super(prefix, lo, hi, next);
            this.value = value;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLFill(prefix, l, h, r, value);
        }
        void atLeaf(int l, int h) {
            prefix.leafFillValue(l, h, value);
        }
    }

    // combine in place

    static final class FJRCombineInPlace extends FJBase {
        final Object[] other;
        final int otherOffset;
        final Reducer combiner;
        FJRCombineInPlace(Prefix prefix, int lo, int hi, FJBase next,
                          Object[] other, int otherOffset,
                          Reducer combiner) {
            super(prefix, lo, hi, next);
            this.other = other;
            this.otherOffset = otherOffset;
            this.combiner = combiner;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRCombineInPlace
                (prefix, l, h, r, other, otherOffset, combiner);
        }
        void atLeaf(int l, int h) {
            prefix.leafCombineInPlace(l, h, other, otherOffset, combiner);
        }
    }

    static final class FJDCombineInPlace extends FJBase {
        final double[] other;
        final int otherOffset;
        final DoubleReducer combiner;
        FJDCombineInPlace(Prefix prefix, int lo, int hi, FJBase next,
                          double[] other, int otherOffset,
                          DoubleReducer combiner) {
            super(prefix, lo, hi, next);
            this.other = other;
            this.otherOffset = otherOffset;
            this.combiner = combiner;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDCombineInPlace
                (prefix, l, h, r, other, otherOffset, combiner);
        }
        void atLeaf(int l, int h) {
            prefix.leafCombineInPlace(l, h, other, otherOffset, combiner);
        }
    }

    static final class FJLCombineInPlace extends FJBase {
        final long[] other;
        final int otherOffset;
        final LongReducer combiner;
        FJLCombineInPlace(Prefix prefix, int lo, int hi, FJBase next,
                          long[] other, int otherOffset,
                          LongReducer combiner) {
            super(prefix, lo, hi, next);
            this.other = other;
            this.otherOffset = otherOffset;
            this.combiner = combiner;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLCombineInPlace
                (prefix, l, h, r, other, otherOffset, combiner);
        }
        void atLeaf(int l, int h) {
            prefix.leafCombineInPlace(l, h, other, otherOffset, combiner);
        }
    }

    // combine

    static final class FJRCombine extends FJBase {
        final Object[] other;
        final Object[] dest;
        final int otherOffset;
        final Combiner combiner;
        FJRCombine(Prefix prefix, int lo, int hi, FJBase next,
                   Object[] other, int otherOffset, Object[] dest,
                   Combiner combiner) {
            super(prefix, lo, hi, next);
            this.other = other;
            this.otherOffset = otherOffset;
            this.dest = dest;
            this.combiner = combiner;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRCombine(prefix, l, h, r, other,
                                  otherOffset, dest, combiner);
        }
        void  atLeaf(int l, int h) {
            prefix.leafCombine(l, h, other, otherOffset, dest, combiner);
        }
    }

    static final class FJDCombine extends FJBase {
        final double[] other;
        final double[] dest;
        final int otherOffset;
        final DoubleReducer combiner;
        FJDCombine(Prefix prefix, int lo, int hi, FJBase next,
                   double[] other, int otherOffset, double[] dest,
                   DoubleReducer combiner) {
            super(prefix, lo, hi, next);
            this.other = other;
            this.otherOffset = otherOffset;
            this.dest = dest;
            this.combiner = combiner;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDCombine(prefix, l, h, r, other,
                                  otherOffset, dest, combiner);
        }
        void  atLeaf(int l, int h) {
            prefix.leafCombine(l, h, other, otherOffset, dest, combiner);
        }
    }

    static final class FJLCombine extends FJBase {
        final long[] other;
        final long[] dest;
        final int otherOffset;
        final LongReducer combiner;
        FJLCombine(Prefix prefix, int lo, int hi, FJBase next,
                   long[] other, int otherOffset, long[] dest,
                   LongReducer combiner) {
            super(prefix, lo, hi, next);
            this.other = other;
            this.otherOffset = otherOffset;
            this.dest = dest;
            this.combiner = combiner;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLCombine(prefix, l, h, r, other,
                                  otherOffset, dest, combiner);
        }
        void  atLeaf(int l, int h) {
            prefix.leafCombine(l, h, other, otherOffset, dest, combiner);
        }
    }

    // stats

    static final class FJRStats extends FJBase
        implements ParallelArray.SummaryStatistics {
        final Comparator comparator;
        public int size() { return size; }
        public Object min() { return min; }
        public Object max() { return max; }
        public int indexOfMin() { return indexOfMin; }
        public int indexOfMax() { return indexOfMax; }
        int size;
        Object min;
        Object max;
        int indexOfMin;
        int indexOfMax;
        FJRStats(Prefix prefix, int lo, int hi, FJBase next,
                 Comparator comparator) {
            super(prefix, lo, hi, next);
            this.comparator = comparator;
            this.indexOfMin = -1;
            this.indexOfMax = -1;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRStats(prefix, l, h, r, comparator);
        }
        void  atLeaf(int l, int h) {
            prefix.leafStats(l, h, this);
        }
        void onReduce(FJBase right) {
            FJRStats r = (FJRStats)right;
            size += r.size;
            updateMin(r.indexOfMin, r.min);
            updateMax(r.indexOfMax, r.max);
        }
        void updateMin(int i, Object x) {
            if (i >= 0 &&
                (indexOfMin < 0 || comparator.compare(min, x) > 0)) {
                min = x;
                indexOfMin = i;
            }
        }
        void updateMax(int i, Object x) {
            if (i >= 0 &&
                (indexOfMax < 0 || comparator.compare(max, x) < 0)) {
                max = x;
                indexOfMax = i;
            }
        }

        public String toString() {
            return
                "size: " + size +
                " min: " + min + " (index " + indexOfMin +
                ") max: " + max + " (index " + indexOfMax + ")";
        }

    }

    static final class FJDStats extends FJBase
        implements ParallelDoubleArray.SummaryStatistics {
        final DoubleComparator comparator;
        public int size() { return size; }
        public double min() { return min; }
        public double max() { return max; }
        public double sum() { return sum; }
        public double average() { return sum / size; }
        public int indexOfMin() { return indexOfMin; }
        public int indexOfMax() { return indexOfMax; }
        int size;
        double min;
        double max;
        double sum;
        int indexOfMin;
        int indexOfMax;
        FJDStats(Prefix prefix, int lo, int hi, FJBase next,
                 DoubleComparator comparator) {
            super(prefix, lo, hi, next);
            this.comparator = comparator;
            this.indexOfMin = -1;
            this.indexOfMax = -1;
            this.min = Double.MAX_VALUE;
            this.max = -Double.MAX_VALUE;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDStats(prefix, l, h, r, comparator);
        }
        void  atLeaf(int l, int h) {
            prefix.leafStats(l, h, this);
        }
        void onReduce(FJBase right) {
            FJDStats r = (FJDStats)right;
            size += r.size;
            sum += r.sum;
            updateMin(r.indexOfMin, r.min);
            updateMax(r.indexOfMax, r.max);
        }
        void updateMin(int i, double x) {
            if (i >= 0 &&
                (indexOfMin < 0 || comparator.compare(min, x) > 0)) {
                min = x;
                indexOfMin = i;
            }
        }
        void updateMax(int i, double x) {
            if (i >= 0 &&
                (indexOfMax < 0 || comparator.compare(max, x) < 0)) {
                max = x;
                indexOfMax = i;
            }
        }
        public String toString() {
            return
                "size: " + size +
                " min: " + min + " (index " + indexOfMin +
                ") max: " + max + " (index " + indexOfMax +
                ") sum: " + sum;
        }
    }

    static final class FJLStats extends FJBase
        implements ParallelLongArray.SummaryStatistics {
        final LongComparator comparator;
        public int size() { return size; }
        public long min() { return min; }
        public long max() { return max; }
        public long sum() { return sum; }
        public double average() { return (double)sum / size; }
        public int indexOfMin() { return indexOfMin; }
        public int indexOfMax() { return indexOfMax; }
        int size;
        long min;
        long max;
        long sum;
        int indexOfMin;
        int indexOfMax;
        FJLStats(Prefix prefix, int lo, int hi, FJBase next,
                 LongComparator comparator) {
            super(prefix, lo, hi, next);
            this.comparator = comparator;
            this.indexOfMin = -1;
            this.indexOfMax = -1;
            this.min = Long.MAX_VALUE;
            this.max = Long.MIN_VALUE;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLStats(prefix, l, h, r, comparator);
        }
        void  atLeaf(int l, int h) {
            prefix.leafStats(l, h, this);
        }
        void onReduce(FJBase right) {
            FJLStats r = (FJLStats)right;
            size += r.size;
            sum += r.sum;
            updateMin(r.indexOfMin, r.min);
            updateMax(r.indexOfMax, r.max);
        }
        void updateMin(int i, long x) {
            if (i >= 0 &&
                (indexOfMin < 0 || comparator.compare(min, x) > 0)) {
                min = x;
                indexOfMin = i;
            }
        }
        void updateMax(int i, long x) {
            if (i >= 0 &&
                (indexOfMax < 0 || comparator.compare(max, x) < 0)) {
                max = x;
                indexOfMax = i;
            }
        }

        public String toString() {
            return
                "size: " + size +
                " min: " + min + " (index " + indexOfMin +
                ") max: " + max + " (index " + indexOfMax +
                ") sum: " + sum;
        }
    }

    // count

    static final class FJRCountSelected extends FJBase {
        final Predicate selector;
        int count;
        FJRCountSelected(Prefix prefix, int lo, int hi, FJBase next,
                         Predicate selector) {
            super(prefix, lo, hi, next);
            this.selector = selector;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJRCountSelected(prefix, l, h, r, selector);
        }
        void onReduce(FJBase right) {
            count += ((FJRCountSelected)right).count;
        }
        void atLeaf(int l, int h) {
            final Object[] array = prefix.rgetArray();
            if (array == null) return;
            final Predicate sel = this.selector;
            int n = 0;
            for (int i = l; i < h; ++i) {
                if (sel.evaluate(array[i]))
                    ++n;
            }
            count = n;
        }
    }

    static final class FJDCountSelected extends FJBase {
        final DoublePredicate selector;
        int count;
        FJDCountSelected(Prefix prefix, int lo, int hi, FJBase next,
                         DoublePredicate selector) {
            super(prefix, lo, hi, next);
            this.selector = selector;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJDCountSelected(prefix, l, h, r, selector);
        }
        void onReduce(FJBase right) {
            count += ((FJDCountSelected)right).count;
        }
        void atLeaf(int l, int h) {
            final double[] array = prefix.dgetArray();
            if (array == null) return;
            final DoublePredicate sel = this.selector;
            int n = 0;
            for (int i = l; i < h; ++i) {
                if (sel.evaluate(array[i]))
                    ++n;
            }
            count = n;
        }
    }

    static final class FJLCountSelected extends FJBase {
        final LongPredicate selector;
        int count;
        FJLCountSelected(Prefix prefix, int lo, int hi, FJBase next,
                         LongPredicate selector) {
            super(prefix, lo, hi, next);
            this.selector = selector;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJLCountSelected(prefix, l, h, r, selector);
        }
        void onReduce(FJBase right) {
            count += ((FJLCountSelected)right).count;
        }
        void atLeaf(int l, int h) {
            final long[] array = prefix.lgetArray();
            if (array == null) return;
            final LongPredicate sel = this.selector;
            int n = 0;
            for (int i = l; i < h; ++i) {
                if (sel.evaluate(array[i]))
                    ++n;
            }
            count = n;
        }
    }

    /**
     * Base for cancellable search tasks. Same idea as FJBase
     * but cancels tasks when result nonnegative.
     */
    static abstract class FJSearchBase extends RecursiveAction {
        final Prefix prefix;
        final int lo;
        final int hi;
        final FJSearchBase next;
        final AtomicInteger result;

        FJSearchBase(Prefix prefix, int lo, int hi,
                     FJSearchBase next,
                     AtomicInteger result) {
            this.prefix = prefix;
            this.lo = lo;
            this.hi = hi;
            this.next = next;
            this.result = result;
        }

        public void compute() {
            if (result.get() >= 0)
                return;
            FJSearchBase r = null;
            int l = lo;
            int h = hi;
            while (prefix.shouldSplit(h - l)) {
                int rh = h;
                h = (l + h) >>> 1;
                (r = newSubtask(h, rh, r)).fork();
            }
            atLeaf(l, h);
            boolean stopping = false;
            while (r != null) {
                stopping |= result.get() >= 0;
                if (ForkJoinWorkerThread.removeIfNextLocalTask(r)) {
                    if (!stopping)
                        r.atLeaf(r.lo, r.hi);
                }
                else if (stopping)
                    r.cancel();
                else
                    r.join();
                r = r.next;
            }
        }
        abstract FJSearchBase newSubtask(int l, int h, FJSearchBase r);
        abstract void atLeaf(int l, int h);
    }

    // select any

    static final class FJRSelectAny extends FJSearchBase {
        final Predicate selector;
        FJRSelectAny(Prefix prefix, int lo, int hi, FJSearchBase next,
                     AtomicInteger result, Predicate selector) {
            super(prefix, lo, hi, next, result);
            this.selector = selector;
        }
        FJSearchBase newSubtask(int l, int h, FJSearchBase r) {
            return new FJRSelectAny(prefix, l, h, r, result, selector);
        }
        void atLeaf(int l, int h) {
            final Object[] array = prefix.rgetArray();
            if (array == null) return;
            for (int i = l; i < h; ++i) {
                if (selector.evaluate(array[i])) {
                    result.compareAndSet(-1, i);
                    break;
                }
                else if (result.get() >= 0)
                    break;
            }
        }
    }

    static final class FJDSelectAny extends FJSearchBase {
        final DoublePredicate selector;
        FJDSelectAny(Prefix prefix, int lo, int hi, FJSearchBase next,
                     AtomicInteger result, DoublePredicate selector) {
            super(prefix, lo, hi, next, result);
            this.selector = selector;
        }
        FJSearchBase newSubtask(int l, int h, FJSearchBase r) {
            return new FJDSelectAny(prefix, l, h, r, result, selector);
        }
        void atLeaf(int l, int h) {
            final double[] array = prefix.dgetArray();
            if (array == null) return;
            for (int i = l; i < h; ++i) {
                if (selector.evaluate(array[i])) {
                    result.compareAndSet(-1, i);
                    break;
                }
                else if (result.get() >= 0)
                    break;
            }
        }
    }

    static final class FJLSelectAny extends FJSearchBase {
        final LongPredicate selector;
        FJLSelectAny(Prefix prefix, int lo, int hi, FJSearchBase next,
                     AtomicInteger result, LongPredicate selector) {
            super(prefix, lo, hi, next, result);
            this.selector = selector;
        }
        FJSearchBase newSubtask(int l, int h, FJSearchBase r) {
            return new FJLSelectAny(prefix, l, h, r, result, selector);
        }
        void atLeaf(int l, int h) {
            final long[] array = prefix.lgetArray();
            if (array == null) return;
            for (int i = l; i < h; ++i) {
                if (selector.evaluate(array[i])) {
                    result.compareAndSet(-1, i);
                    break;
                }
                else if (result.get() >= 0)
                    break;
            }
        }
    }

    // index of

    static final class FJRIndexOf extends FJSearchBase {
        final Object target;
        FJRIndexOf(Prefix prefix, int lo, int hi, FJSearchBase next,
                   AtomicInteger result, Object target) {
            super(prefix, lo, hi, next, result);
            this.target = target;
        }
        FJSearchBase newSubtask(int l, int h, FJSearchBase r) {
            return new FJRIndexOf(prefix, l, h, r, result, target);
        }
        void atLeaf(int l, int h) {
            final Object[] array = prefix.rgetArray();
            if (array == null) return;
            for (int i = l; i < h; ++i) {
                if (target.equals(array[i])) {
                    result.compareAndSet(-1, i);
                    break;
                }
                else if (result.get() >= 0)
                    break;
            }
        }
    }

    static final class FJDIndexOf extends FJSearchBase {
        final double target;
        FJDIndexOf(Prefix prefix, int lo, int hi, FJSearchBase next,
                   AtomicInteger result, double target) {
            super(prefix, lo, hi, next, result);
            this.target = target;
        }
        FJSearchBase newSubtask(int l, int h, FJSearchBase r) {
            return new FJDIndexOf(prefix, l, h, r, result, target);
        }
        void atLeaf(int l, int h) {
            final double[] array = prefix.dgetArray();
            if (array == null) return;
            for (int i = l; i < h; ++i) {
                if (target == (array[i])) {
                    result.compareAndSet(-1, i);
                    break;
                }
                else if (result.get() >= 0)
                    break;
            }
        }
    }

    static final class FJLIndexOf extends FJSearchBase {
        final long target;
        FJLIndexOf(Prefix prefix, int lo, int hi, FJSearchBase next,
                   AtomicInteger result, long target) {
            super(prefix, lo, hi, next, result);
            this.target = target;
        }
        FJSearchBase newSubtask(int l, int h, FJSearchBase r) {
            return new FJLIndexOf(prefix, l, h, r, result, target);
        }
        void atLeaf(int l, int h) {
            final long[] array = prefix.lgetArray();
            if (array == null) return;
            for (int i = l; i < h; ++i) {
                if (target == (array[i])) {
                    result.compareAndSet(-1, i);
                    break;
                }
                else if (result.get() >= 0)
                    break;
            }
        }
    }

    // select all

    /**
     * SelectAll proceeds in two passes. In the first pass, indices of
     * matching elements are recorded in indices array.  In second pass,
     * once the size of results is known and result array is
     * constructed in driver, the matching elements are placed into
     * corresponding result positions.
     */
    static final class FJSelectAll extends RecursiveAction {
        final FJSelectAllDriver driver;
        FJSelectAll left, right;
        final int lo;
        final int hi;
        int count;  // number of matching elements
        int offset;
        boolean isInternal; // true if this is a non-leaf node

        FJSelectAll(FJSelectAllDriver driver, int lo, int hi) {
            this.driver = driver;
            this.lo = lo;
            this.hi = hi;
        }

        public void compute() {
            FJSelectAllDriver d = driver;
            if (d.phase == 0) {
                Prefix p = d.prefix;
                if (isInternal = p.shouldSplit(hi - lo))
                    internalPhase0();
                else
                    count = p.leafIndexSelected(lo, hi, true, d.indices);
            }
            else if (count != 0) {
                if (isInternal)
                    internalPhase1();
                else
                    d.leafPhase1(lo, lo+count, offset);
            }
        }

        void internalPhase0() {
            int mid = (lo + hi) >>> 1;
            FJSelectAll l = new FJSelectAll(driver, lo, mid);
            FJSelectAll r = new FJSelectAll(driver, mid, hi);
            forkJoin(l, r);
            int ln = l.count;
            if (ln != 0)
                left = l;
            int rn = r.count;
            if (rn != 0)
                right = r;
            count = ln + rn;
        }

        void internalPhase1() {
            int k = offset;
            if (left != null) {
                int ln = left.count;
                left.offset = k;
                left.reinitialize();
                if (right != null) {
                    right.offset = k + ln;
                    right.reinitialize();
                    forkJoin(left, right);
                }
                else
                    left.compute();
            }
            else if (right != null) {
                right.offset = k;
                right.compute();
            }
        }
    }

    static abstract class FJSelectAllDriver extends RecursiveAction {
        final int[] indices;
        final Prefix prefix;
        int phase;
        FJSelectAllDriver(Prefix prefix) {
            this.prefix = prefix;
            int n = prefix.upperBound - prefix.firstIndex;
            indices = new int[n];
        }
        public final void compute() {
            FJSelectAll r = new FJSelectAll
                (this, prefix.firstIndex, prefix.upperBound);
            r.compute();
            createResults(r.count);
            phase = 1;
            r.compute();
        }
        abstract void createResults(int size);
        abstract void leafPhase1(int loIdx, int hiIdx, int offset);
    }

    static final class FJRSelectAllDriver extends FJSelectAllDriver {
        final Class elementType;
        Object[] results;
        FJRSelectAllDriver(Prefix prefix, Class elementType) {
            super(prefix);
            this.elementType = elementType;
        }
        void createResults(int size) {
            results = (Object[])Array.newInstance(elementType, size);
        }
        void leafPhase1(int loIdx, int hiIdx, int offset) {
            prefix.leafTransferByIndex(indices, loIdx, hiIdx, results, offset);
        }
    }

    static final class FJDSelectAllDriver extends FJSelectAllDriver {
        double[] results;
        FJDSelectAllDriver(Prefix prefix) {
            super(prefix);
        }
        void createResults(int size) {
            results = new double[size];
        }
        void leafPhase1(int loIdx, int hiIdx, int offset) {
            prefix.leafTransferByIndex(indices, loIdx, hiIdx, results, offset);
        }
    }

    static final class FJLSelectAllDriver extends FJSelectAllDriver {
        long[] results;
        FJLSelectAllDriver(Prefix prefix) {
            super(prefix);
        }
        void createResults(int size) {
            results = new long[size];
        }
        void leafPhase1(int loIdx, int hiIdx, int offset) {
            prefix.leafTransferByIndex(indices, loIdx, hiIdx, results, offset);
        }
    }

    /**
     * Root node for FJRemoveAll. Spawns subtasks and shifts elements
     * as indices become available, bypassing index array creation
     * when offsets are known. This differs from SelectAll mainly in
     * that data movement is all done by the driver rather than in a
     * second parallel pass.
     */
    static final class FJRemoveAllDriver extends RecursiveAction {
        final Prefix prefix;
        final int lo;
        final int hi;
        final int[] indices;
        int offset;
        FJRemoveAllDriver(Prefix prefix, int lo, int hi) {
            this.prefix = prefix;
            this.lo = lo;
            this.hi = hi;
            this.indices = new int[hi - lo];
        }

        public void compute() {
            FJRemoveAll r = null;
            int l = lo;
            int h = hi;
            while (prefix.shouldSplit(h - l)) {
                int rh = h;
                h = (l + h) >>> 1;
                (r = new FJRemoveAll(prefix, h, rh, r, indices)).fork();
            }
            int k = prefix.leafMoveSelected(l, h, l, false);
            while (r != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(r))
                    k = prefix.leafMoveSelected(r.lo, r.hi, k, false);
                else {
                    r.join();
                    int n = r.count;
                    if (n != 0)
                        prefix.leafMoveByIndex(indices, r.lo, r.lo+n, k);
                    k += n;
                    FJRemoveAll rr = r.right;
                    if (rr != null)
                        k = inorderMove(rr, k);
                }
                r = r.next;
            }
            offset = k;
        }

        /**
         * Inorder traversal to move indexed elements across reachable
         * nodes.  This guarantees that element shifts don't overwrite
         * those still being used by active subtasks.
         */
        static int inorderMove(FJRemoveAll t, int index) {
            while (t != null) {
                int n = t.count;
                if (n != 0)
                    t.prefix.leafMoveByIndex(t.indices, t.lo, t.lo+n, index);
                index += n;
                FJRemoveAll p = t.next;
                if (p != null)
                    index = inorderMove(p, index);
                t = t.right;
            }
            return index;
        }
    }

    /**
     * Basic FJ tssk for non-root FJRemoveAll nodes
     */
    static final class FJRemoveAll extends RecursiveAction {
        final Prefix prefix;
        final int lo;
        final int hi;
        final FJRemoveAll next;
        final int[] indices;
        int count;
        FJRemoveAll right;
        FJRemoveAll(Prefix prefix, int lo, int hi, FJRemoveAll next,
                    int[] indices) {
            this.prefix = prefix;
            this.lo = lo;
            this.hi = hi;
            this.next = next;
            this.indices = indices;
        }

        public void compute() {
            FJRemoveAll r = null;
            int l = lo;
            int h = hi;
            while (prefix.shouldSplit(h - l)) {
                int rh = h;
                h = (l + h) >>> 1;
                (r = new FJRemoveAll(prefix, h, rh, r, indices)).fork();
            }
            right = r;
            count = prefix.leafIndexSelected(l, h, false, indices);
            while (r != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(r))
                    r.count = prefix.leafIndexSelected
                        (r.lo, r.hi, false, indices);
                else
                    r.join();
                r = r.next;
            }
        }
    }

    // unique elements

    static final class FJUniquifier extends FJBase {
        final UniquifierTable table;
        int count;
        FJUniquifier(Prefix prefix, int lo, int hi, FJBase next,
                     UniquifierTable table) {
            super(prefix, lo, hi, next);
            this.table = table;
        }
        FJBase newSubtask(int l, int h, FJBase r) {
            return new FJUniquifier(prefix, l, h, r, table);
        }
        void atLeaf(int l, int h) {
            count = table.addElements(l, h);
        }
        void onReduce(FJBase right) {
            count += ((FJUniquifier)right).count;
        }
    }

    /**
     * Base class of fixed-size hash tables for
     * uniquification. Opportunistically subclasses
     * AtomicLongArray. The high word of each slot is the cached
     * massaged hash of an element, and the low word contains its
     * index, plus one, to ensure that a zero tab entry means
     * empty. The mechanics for this are just folded into the
     * main addElements method.
     * Each leaf step places source array elements into table,
     * Even though this table undergoes a lot of contention when
     * elements are concurrently inserted by parallel threads, it is
     * generally faster to do this than to have separate tables and
     * then merge them.
     */
    static abstract class UniquifierTable extends AtomicLongArray {
        UniquifierTable(int size) {
            super(tableSizeFor(size));
        }

        /** Returns a good size for table */
        static int tableSizeFor(int n) {
            int padded = n + (n >>> 1) + 1;
            if (padded < n) // int overflow
                throw new OutOfMemoryError();
            int s = 8;
            while (s < padded) s <<= 1;
            return s;
        }

        // Same hashcode conditioning as HashMap
        static int hash(int h) {
            h ^= (h >>> 20) ^ (h >>> 12);
            return h ^ (h >>> 7) ^ (h >>> 4);
        }

        /**
         * Add source elements from lo to hi; return count
         * of number of unique elements inserted
         */
        abstract int addElements(int lo, int hi);
    }

    static final class RUniquifierTable extends UniquifierTable {
        final Object[] source;
        final Predicate selector;
        final boolean byIdentity;
        RUniquifierTable(int size, Object[] array, Predicate selector,
                         boolean byIdentity) {
            super(size);
            this.source = array;
            this.selector = selector;
            this.byIdentity = byIdentity;
        }

        int addElements(int lo, int hi) {
            final Predicate selector = this.selector;
            final Object[] src = source;
            final int mask = length() - 1;
            int count = 0;
            for (int k = lo; k < hi; ++k) {
                Object x = src[k];
                if (x == null || (selector != null && !selector.evaluate(x)))
                    continue;
                int hc = byIdentity? System.identityHashCode(x): x.hashCode();
                int hash = hash(hc);
                long entry = (((long)hash) << 32) + (k + 1);
                int idx = hash & mask;
                for (;;) {
                    long d = get(idx);
                    if (d != 0) {
                        if ((int)(d >>> 32) == hash) {
                            Object y = src[(int)((d - 1) & 0x7fffffffL)];
                            if (byIdentity? (x == y) : x.equals(y))
                                break;
                        }
                        idx = (idx + 1) & mask;
                    }
                    else if (compareAndSet(idx, 0, entry)) {
                        ++count;
                        break;
                    }
                }
            }
            return count;
        }

        /**
         * Return new array holding all elements.
         */
        Object[] uniqueElements(int size) {
            Object[] src = source;
            Class sclass = src.getClass().getComponentType();
            Object[] res = (Object[])Array.newInstance(sclass, size);
            int k = 0;
            int n = length();
            for (int i = 0; i < n && k < size; ++i) {
                long d = get(i);
                if (d != 0)
                    res[k++] = src[((int)((d - 1) & 0x7fffffffL))];
            }
            return res;
        }
    }

    static final class DUniquifierTable extends UniquifierTable {
        final double[] source;
        final DoublePredicate selector;
        DUniquifierTable(int size, double[] array,
                         DoublePredicate selector) {
            super(size);
            this.source = array;
            this.selector = selector;
        }

        int addElements(int lo, int hi) {
            final DoublePredicate selector = this.selector;
            final double[] src = source;
            final int mask = length() - 1;
            int count = 0;
            for (int k = lo; k < hi; ++k) {
                double x = src[k];
                if (selector != null && !selector.evaluate(x))
                    continue;
                long bits = Double.doubleToLongBits(x);
                int hash = hash((int)(bits ^ (bits >>> 32)));;
                long entry = (((long)hash) << 32) + (k + 1);
                int idx = hash & mask;
                for (;;) {
                    long d = get(idx);
                    if (d != 0) {
                        if ((int)(d >>> 32) == hash &&
                            x == (src[(int)((d - 1) & 0x7fffffffL)]))
                            break;
                        idx = (idx + 1) & mask;
                    }
                    else if (compareAndSet(idx, 0, entry)) {
                        ++count;
                        break;
                    }
                }
            }
            return count;
        }

        double[] uniqueElements(int size) {
            double[] res = new double[size];
            double[] src = source;
            int k = 0;
            int n = length();
            for (int i = 0; i < n && k < size; ++i) {
                long d = get(i);
                if (d != 0)
                    res[k++] = src[((int)((d - 1) & 0x7fffffffL))];
            }
            return res;
        }
    }

    static final class LUniquifierTable extends UniquifierTable {
        final long[] source;
        final LongPredicate selector;
        LUniquifierTable(int size, long[] array, LongPredicate selector) {
            super(size);
            this.source = array;
            this.selector = selector;
        }

        int addElements(int lo, int hi) {
            final LongPredicate selector = this.selector;
            final long[] src = source;
            final int mask = length() - 1;
            int count = 0;
            for (int k = lo; k < hi; ++k) {
                long x = src[k];
                if (selector != null && !selector.evaluate(x))
                    continue;
                int hash = hash((int)(x ^ (x >>> 32)));
                long entry = (((long)hash) << 32) + (k + 1);
                int idx = hash & mask;
                for (;;) {
                    long d = get(idx);
                    if (d != 0) {
                        if ((int)(d >>> 32) == hash &&
                            x == (src[(int)((d - 1) & 0x7fffffffL)]))
                            break;
                        idx = (idx + 1) & mask;
                    }
                    else if (compareAndSet(idx, 0, entry)) {
                        ++count;
                        break;
                    }
                }
            }
            return count;
        }

        long[] uniqueElements(int size) {
            long[] res = new long[size];
            long[] src = source;
            int k = 0;
            int n = length();
            for (int i = 0; i < n && k < size; ++i) {
                long d = get(i);
                if (d != 0)
                    res[k++] = src[((int)((d - 1) & 0x7fffffffL))];
            }
            return res;
        }
    }

    /**
     * Sorter classes based mainly on CilkSort
     * <A href="http://supertech.lcs.mit.edu/cilk/"> Cilk</A>:
     * Basic algorithm:
     * if array size is small, just use a sequential quicksort
     *         Otherwise:
     *         1. Break array in half.
     *         2. For each half,
     *             a. break the half in half (i.e., quarters),
     *             b. sort the quarters
     *             c. merge them together
     *         3. merge together the two halves.
     *
     * One reason for splitting in quarters is that this guarantees
     * that the final sort is in the main array, not the workspace
     * array.  (workspace and main swap roles on each subsort step.)
     * Leaf-level sorts use a Sequential quicksort, that in turn uses
     * insertion sort if under threshold.  Otherwise it uses median of
     * three to pick pivot, and loops rather than recurses along left
     * path.
     *
     * It is sad but true that sort and merge performance are
     * sensitive enough to inner comparison overhead to warrant
     * creating 6 versions (not just 3) -- one each for natural
     * comparisons vs supplied comparators.
     */
    static final class FJRSorter extends RecursiveAction {
        final Comparator cmp;
        final Object[] a;     // array to be sorted.
        final Object[] w;     // workspace for merge
        final int origin;     // origin of the part of array we deal with
        final int n;          // Number of elements in (sub)arrays.
        final int gran;       // split control
        FJRSorter(Comparator cmp,
                  Object[] a, Object[] w, int origin, int n, int gran) {
            this.cmp = cmp;
            this.a = a; this.w = w; this.origin = origin; this.n = n;
            this.gran = gran;
        }

        public void compute()  {
            int l = origin;
            int g = gran;
            if (n > g) {
                int h = n >>> 1; // half
                int q = n >>> 2; // lower quarter index
                int u = h + q;   // upper quarter
                forkJoin(new FJSubSorter
                         (new FJRSorter(cmp, a, w, l,   q,   g),
                          new FJRSorter(cmp, a, w, l+q, h-q, g),
                          new FJRMerger(cmp, a, w, l,   q,
                                        l+q, h-q, l, g, null)),
                         new FJSubSorter
                         (new FJRSorter(cmp, a, w, l+h, q,   g),
                          new FJRSorter(cmp, a, w, l+u, n-u, g),
                          new FJRMerger(cmp, a, w, l+h, q,
                                        l+u, n-u, l+h, g, null)));
                new FJRMerger(cmp, w, a, l, h,
                              l+h, n-h, l, g, null).compute();
            }
            else
                rquickSort(a, cmp, l, l+n-1);
        }
    }

    static final class FJRCSorter extends RecursiveAction {
        final Comparable[] a; final Comparable[] w;
        final int origin; final int n; final int gran;
        FJRCSorter(Comparable[] a, Comparable[] w,
                   int origin, int n, int gran) {
            this.a = a; this.w = w; this.origin = origin; this.n = n;
            this.gran = gran;
        }
        public void compute()  {
            int l = origin;
            int g = gran;
            if (n > g) {
                int h = n >>> 1;
                int q = n >>> 2;
                int u = h + q;
                forkJoin(new FJSubSorter
                         (new FJRCSorter(a, w, l,   q,   g),
                          new FJRCSorter(a, w, l+q, h-q, g),
                          new FJRCMerger(a, w, l,   q,
                                         l+q, h-q, l, g, null)),
                         new FJSubSorter
                         (new FJRCSorter(a, w, l+h, q,   g),
                          new FJRCSorter(a, w, l+u, n-u, g),
                          new FJRCMerger(a, w, l+h, q,
                                         l+u, n-u, l+h, g, null)));
                new FJRCMerger(w, a, l, h,
                               l+h, n-h, l, g, null).compute();
            }
            else
                rcquickSort(a, l, l+n-1);
        }
    }

    static final class FJDSorter extends RecursiveAction {
        final DoubleComparator cmp; final double[] a; final double[] w;
        final int origin; final int n; final int gran;
        FJDSorter(DoubleComparator cmp,
                  double[] a, double[] w, int origin, int n, int gran) {
            this.cmp = cmp;
            this.a = a; this.w = w; this.origin = origin; this.n = n;
            this.gran = gran;
        }
        public void compute()  {
            int l = origin;
            int g = gran;
            if (n > g) {
                int h = n >>> 1;
                int q = n >>> 2;
                int u = h + q;
                forkJoin(new FJSubSorter
                         (new FJDSorter(cmp, a, w, l,   q,   g),
                          new FJDSorter(cmp, a, w, l+q, h-q, g),
                          new FJDMerger(cmp, a, w, l,   q,
                                        l+q, h-q, l, g, null)),
                         new FJSubSorter
                         (new FJDSorter(cmp, a, w, l+h, q,   g),
                          new FJDSorter(cmp, a, w, l+u, n-u, g),
                          new FJDMerger(cmp, a, w, l+h, q,
                                        l+u, n-u, l+h, g, null)));
                new FJDMerger(cmp, w, a, l, h,
                              l+h, n-h, l, g, null).compute();
            }
            else
                dquickSort(a, cmp, l, l+n-1);
        }
    }

    static final class FJDCSorter extends RecursiveAction {
        final double[] a; final double[] w;
        final int origin; final int n; final int gran;
        FJDCSorter(double[] a, double[] w, int origin,
                   int n, int gran) {
            this.a = a; this.w = w; this.origin = origin; this.n = n;
            this.gran = gran;
        }
        public void compute()  {
            int l = origin;
            int g = gran;
            if (n > g) {
                int h = n >>> 1;
                int q = n >>> 2;
                int u = h + q;
                forkJoin(new FJSubSorter
                         (new FJDCSorter(a, w, l,   q,   g),
                          new FJDCSorter(a, w, l+q, h-q, g),
                          new FJDCMerger(a, w, l,   q,
                                         l+q, h-q, l, g, null)),
                         new FJSubSorter
                         (new FJDCSorter(a, w, l+h, q,   g),
                          new FJDCSorter(a, w, l+u, n-u, g),
                          new FJDCMerger(a, w, l+h, q,
                                         l+u, n-u, l+h, g, null)));
                new FJDCMerger(w, a, l, h,
                               l+h, n-h, l, g, null).compute();
            }
            else
                dcquickSort(a, l, l+n-1);
        }
    }

    static final class FJLSorter extends RecursiveAction {
        final LongComparator cmp; final long[] a; final long[] w;
        final int origin; final int n; final int gran;
        FJLSorter(LongComparator cmp,
                  long[] a, long[] w, int origin, int n, int gran) {
            this.cmp = cmp;
            this.a = a; this.w = w; this.origin = origin; this.n = n;
            this.gran = gran;
        }

        public void compute()  {
            int l = origin;
            int g = gran;
            if (n > g) {
                int h = n >>> 1;
                int q = n >>> 2;
                int u = h + q;
                forkJoin(new FJSubSorter
                         (new FJLSorter(cmp, a, w, l,   q,   g),
                          new FJLSorter(cmp, a, w, l+q, h-q, g),
                          new FJLMerger(cmp, a, w, l,   q,
                                        l+q, h-q, l, g, null)),
                         new FJSubSorter
                         (new FJLSorter(cmp, a, w, l+h, q,   g),
                          new FJLSorter(cmp, a, w, l+u, n-u, g),
                          new FJLMerger(cmp, a, w, l+h, q,
                                        l+u, n-u, l+h, g, null)));
                new FJLMerger(cmp, w, a, l, h,
                              l+h, n-h, l, g, null).compute();
            }
            else
                lquickSort(a, cmp, l, l+n-1);
        }
    }

    static final class FJLCSorter extends RecursiveAction {
        final long[] a; final long[] w;
        final int origin; final int n; final int gran;
        FJLCSorter(long[] a, long[] w, int origin,
                   int n, int gran) {
            this.a = a; this.w = w; this.origin = origin; this.n = n;
            this.gran = gran;
        }
        public void compute()  {
            int l = origin;
            int g = gran;
            if (n > g) {
                int h = n >>> 1;
                int q = n >>> 2;
                int u = h + q;
                forkJoin(new FJSubSorter
                         (new FJLCSorter(a, w, l,   q,   g),
                          new FJLCSorter(a, w, l+q, h-q, g),
                          new FJLCMerger(a, w, l,   q,
                                         l+q, h-q, l, g, null)),
                         new FJSubSorter
                         (new FJLCSorter(a, w, l+h, q,   g),
                          new FJLCSorter(a, w, l+u, n-u, g),
                          new FJLCMerger(a, w, l+h, q,
                                         l+u, n-u, l+h, g, null)));
                new FJLCMerger(w, a, l, h,
                               l+h, n-h, l, g, null).compute();
            }
            else
                lcquickSort(a, l, l+n-1);
        }
    }

    /** Utility class to sort half a partitioned array */
    static final class FJSubSorter extends RecursiveAction {
        final RecursiveAction left;
        final RecursiveAction right;
        final RecursiveAction merger;
        FJSubSorter(RecursiveAction left, RecursiveAction right,
                    RecursiveAction merger){
            this.left = left; this.right = right; this.merger = merger;
        }
        public void compute() {
            forkJoin(left, right);
            merger.compute();
        }
    }

    /**
     * Perform merging for FJSorter. If big enough, splits Left
     * partition in half; finds the greatest point in Right partition
     * less than the beginning of the second half of Left via binary
     * search; and then, in parallel, merges left half of Left with
     * elements of Right up to split point, and merges right half of
     * Left with elements of R past split point. At leaf, it just
     * sequentially merges. This is all messy to code; sadly we need
     * six versions.
     */
    static final class FJRMerger extends RecursiveAction {
        final Comparator cmp;
        final Object[] a;      // partitioned  array.
        final Object[] w;      // Output array.
        final int lo;          // relative origin of left side of a
        final int ln;          // number of elements on left of a
        final int ro;          // relative origin of right side of a
        final int rn;          // number of elements on right of a
        final int wo;          // origin for output
        final int gran;
        final FJRMerger next;

        FJRMerger(Comparator cmp, Object[] a, Object[] w,
                  int lo, int ln, int ro, int rn, int wo,
                  int gran, FJRMerger next) {
            this.cmp = cmp;
            this.a = a;    this.w = w;
            this.lo = lo;  this.ln = ln;
            this.ro = ro;  this.rn = rn;
            this.wo = wo;
            this.gran = gran;
            this.next = next;
        }

        public void compute() {
            // spawn right subtasks
            FJRMerger rights = null;
            int nleft = ln;
            int nright = rn;
            while (nleft > gran) {
                int lh = nleft >>> 1;
                int splitIndex = lo + lh;
                Object split = a[splitIndex];
                // binary search r for split
                int rl = 0;
                int rh = nright;
                while (rl < rh) {
                    int mid = (rl + rh) >>> 1;
                    if (cmp.compare(split, a[ro + mid]) <= 0)
                        rh = mid;
                    else
                        rl = mid + 1;
                }
                (rights = new FJRMerger
                 (cmp, a, w, splitIndex, nleft-lh, ro+rh,
                  nright-rh, wo+lh+rh, gran, rights)).fork();
                nleft = lh;
                nright = rh;
            }

            // sequentially merge
            int l = lo;
            int lFence = lo + nleft;
            int r = ro;
            int rFence = ro + nright;
            int k = wo;
            while (l < lFence && r < rFence) {
                Object al = a[l];
                Object ar = a[r];
                Object t;
                if (cmp.compare(al, ar) <= 0) {++l; t=al;} else {++r; t=ar;}
                w[k++] = t;
            }
            while (l < lFence)
                w[k++] = a[l++];
            while (r < rFence)
                w[k++] = a[r++];

            // join subtasks
            while (rights != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(rights))
                    rights.compute();
                else
                    rights.join();
                rights = rights.next;
            }
        }
    }

    static final class FJRCMerger extends RecursiveAction {
        final Comparable[] a; final Comparable[] w;
        final int lo; final int ln; final int ro;  final int rn; final int wo;
        final int gran;
        final FJRCMerger next;
        FJRCMerger(Comparable[] a, Comparable[] w, int lo,
                   int ln, int ro, int rn, int wo,
                   int gran, FJRCMerger next) {
            this.a = a;    this.w = w;
            this.lo = lo;  this.ln = ln; this.ro = ro; this.rn = rn;
            this.wo = wo;
            this.gran = gran;
            this.next = next;
        }

        public void compute() {
            FJRCMerger rights = null;
            int nleft = ln;
            int nright = rn;
            while (nleft > gran) {
                int lh = nleft >>> 1;
                int splitIndex = lo + lh;
                Comparable split = a[splitIndex];
                int rl = 0;
                int rh = nright;
                while (rl < rh) {
                    int mid = (rl + rh) >>> 1;
                    if (split.compareTo(a[ro + mid]) <= 0)
                        rh = mid;
                    else
                        rl = mid + 1;
                }
                (rights = new FJRCMerger
                 (a, w, splitIndex, nleft-lh, ro+rh,
                  nright-rh, wo+lh+rh, gran, rights)).fork();
                nleft = lh;
                nright = rh;
            }

            int l = lo;
            int lFence = lo + nleft;
            int r = ro;
            int rFence = ro + nright;
            int k = wo;
            while (l < lFence && r < rFence) {
                Comparable al = a[l];
                Comparable ar = a[r];
                Comparable t;
                if (al.compareTo(ar) <= 0) {++l; t=al;} else {++r; t=ar; }
                w[k++] = t;
            }
            while (l < lFence)
                w[k++] = a[l++];
            while (r < rFence)
                w[k++] = a[r++];

            while (rights != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(rights))
                    rights.compute();
                else
                    rights.join();
                rights = rights.next;
            }
        }
    }

    static final class FJDMerger extends RecursiveAction {
        final DoubleComparator cmp; final double[] a; final double[] w;
        final int lo; final int ln; final int ro; final int rn; final int wo;
        final int gran;
        final FJDMerger next;
        FJDMerger(DoubleComparator cmp, double[] a, double[] w,
                  int lo, int ln, int ro, int rn, int wo,
                  int gran, FJDMerger next) {
            this.cmp = cmp;
            this.a = a;    this.w = w;
            this.lo = lo;  this.ln = ln;
            this.ro = ro;  this.rn = rn;
            this.wo = wo;
            this.gran = gran;
            this.next = next;
        }
        public void compute() {
            FJDMerger rights = null;
            int nleft = ln;
            int nright = rn;
            while (nleft > gran) {
                int lh = nleft >>> 1;
                int splitIndex = lo + lh;
                double split = a[splitIndex];
                int rl = 0;
                int rh = nright;
                while (rl < rh) {
                    int mid = (rl + rh) >>> 1;
                    if (cmp.compare(split, a[ro + mid]) <= 0)
                        rh = mid;
                    else
                        rl = mid + 1;
                }
                (rights = new FJDMerger
                 (cmp, a, w, splitIndex, nleft-lh, ro+rh,
                  nright-rh, wo+lh+rh, gran, rights)).fork();
                nleft = lh;
                nright = rh;
            }

            int l = lo;
            int lFence = lo + nleft;
            int r = ro;
            int rFence = ro + nright;
            int k = wo;
            while (l < lFence && r < rFence) {
                double al = a[l];
                double ar = a[r];
                double t;
                if (cmp.compare(al, ar) <= 0) {++l; t=al;} else {++r; t=ar; }
                w[k++] = t;
            }
            while (l < lFence)
                w[k++] = a[l++];
            while (r < rFence)
                w[k++] = a[r++];

            while (rights != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(rights))
                    rights.compute();
                else
                    rights.join();
                rights = rights.next;
            }
        }
    }

    static final class FJDCMerger extends RecursiveAction {
        final double[] a; final double[] w;
        final int lo; final int ln; final int ro; final int rn; final int wo;
        final int gran;
        final FJDCMerger next;
        FJDCMerger(double[] a, double[] w, int lo,
                   int ln, int ro, int rn, int wo,
                   int gran, FJDCMerger next) {
            this.a = a;    this.w = w;
            this.lo = lo;  this.ln = ln;
            this.ro = ro;  this.rn = rn;
            this.wo = wo;
            this.gran = gran;
            this.next = next;
        }
        public void compute() {
            FJDCMerger rights = null;
            int nleft = ln;
            int nright = rn;
            while (nleft > gran) {
                int lh = nleft >>> 1;
                int splitIndex = lo + lh;
                double split = a[splitIndex];
                int rl = 0;
                int rh = nright;
                while (rl < rh) {
                    int mid = (rl + rh) >>> 1;
                    if (split <= a[ro + mid])
                        rh = mid;
                    else
                        rl = mid + 1;
                }
                (rights = new FJDCMerger
                 (a, w, splitIndex, nleft-lh, ro+rh,
                  nright-rh, wo+lh+rh, gran, rights)).fork();
                nleft = lh;
                nright = rh;
            }

            int l = lo;
            int lFence = lo + nleft;
            int r = ro;
            int rFence = ro + nright;
            int k = wo;
            while (l < lFence && r < rFence) {
                double al = a[l];
                double ar = a[r];
                double t;
                if (al <= ar) {++l; t=al;} else {++r; t=ar; }
                w[k++] = t;
            }
            while (l < lFence)
                w[k++] = a[l++];
            while (r < rFence)
                w[k++] = a[r++];

            while (rights != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(rights))
                    rights.compute();
                else
                    rights.join();
                rights = rights.next;
            }
        }
    }

    static final class FJLMerger extends RecursiveAction {
        final LongComparator cmp; final long[] a; final long[] w;
        final int lo; final int ln; final int ro; final int rn; final int wo;
        final int gran;
        final FJLMerger next;
        FJLMerger(LongComparator cmp, long[] a, long[] w,
                  int lo, int ln, int ro, int rn, int wo,
                  int gran, FJLMerger next) {
            this.cmp = cmp;
            this.a = a;    this.w = w;
            this.lo = lo;  this.ln = ln;
            this.ro = ro;  this.rn = rn;
            this.wo = wo;
            this.gran = gran;
            this.next = next;
        }
        public void compute() {
            FJLMerger rights = null;
            int nleft = ln;
            int nright = rn;
            while (nleft > gran) {
                int lh = nleft >>> 1;
                int splitIndex = lo + lh;
                long split = a[splitIndex];
                int rl = 0;
                int rh = nright;
                while (rl < rh) {
                    int mid = (rl + rh) >>> 1;
                    if (cmp.compare(split, a[ro + mid]) <= 0)
                        rh = mid;
                    else
                        rl = mid + 1;
                }
                (rights = new FJLMerger
                 (cmp, a, w, splitIndex, nleft-lh, ro+rh,
                  nright-rh, wo+lh+rh, gran, rights)).fork();
                nleft = lh;
                nright = rh;
            }

            int l = lo;
            int lFence = lo + nleft;
            int r = ro;
            int rFence = ro + nright;
            int k = wo;
            while (l < lFence && r < rFence) {
                long al = a[l];
                long ar = a[r];
                long t;
                if (cmp.compare(al, ar) <= 0) {++l; t=al;} else {++r; t=ar;}
                w[k++] = t;
            }
            while (l < lFence)
                w[k++] = a[l++];
            while (r < rFence)
                w[k++] = a[r++];

            while (rights != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(rights))
                    rights.compute();
                else
                    rights.join();
                rights = rights.next;
            }
        }
    }

    static final class FJLCMerger extends RecursiveAction {
        final long[] a; final long[] w;
        final int lo; final int ln; final int ro; final int rn; final int wo;
        final int gran;
        final FJLCMerger next;
        FJLCMerger(long[] a, long[] w, int lo,
                   int ln, int ro, int rn, int wo,
                   int gran, FJLCMerger next) {
            this.a = a;    this.w = w;
            this.lo = lo;  this.ln = ln;
            this.ro = ro;  this.rn = rn;
            this.wo = wo;
            this.gran = gran;
            this.next = next;
        }
        public void compute() {
            FJLCMerger rights = null;
            int nleft = ln;
            int nright = rn;
            while (nleft > gran) {
                int lh = nleft >>> 1;
                int splitIndex = lo + lh;
                long split = a[splitIndex];
                int rl = 0;
                int rh = nright;
                while (rl < rh) {
                    int mid = (rl + rh) >>> 1;
                    if (split <= a[ro + mid])
                        rh = mid;
                    else
                        rl = mid + 1;
                }
                (rights = new FJLCMerger
                 (a, w, splitIndex, nleft-lh, ro+rh,
                  nright-rh, wo+lh+rh, gran, rights)).fork();
                nleft = lh;
                nright = rh;
            }

            int l = lo;
            int lFence = lo + nleft;
            int r = ro;
            int rFence = ro + nright;
            int k = wo;
            while (l < lFence && r < rFence) {
                long al = a[l];
                long ar = a[r];
                long t;
                if (al <= ar) {++l; t=al;} else {++r; t = ar;}
                w[k++] = t;
            }
            while (l < lFence)
                w[k++] = a[l++];
            while (r < rFence)
                w[k++] = a[r++];

            while (rights != null) {
                if (ForkJoinWorkerThread.removeIfNextLocalTask(rights))
                    rights.compute();
                else
                    rights.join();
                rights = rights.next;
            }
        }
    }

    /** Cutoff for when to use insertion-sort instead of quicksort */
    static final int INSERTION_SORT_THRESHOLD = 8;

    // Six nearly identical versions of quicksort

    static void rquickSort(Object[] a, Comparator cmp, int lo, int hi) {
        for (;;) {
            if (hi - lo <= INSERTION_SORT_THRESHOLD) {
                for (int i = lo + 1; i <= hi; i++) {
                    Object t = a[i];
                    int j = i - 1;
                    while (j >= lo && cmp.compare(t, a[j]) < 0) {
                        a[j+1] = a[j];
                        --j;
                    }
                    a[j+1] = t;
                }
                return;
            }

            int mid = (lo + hi) >>> 1;
            if (cmp.compare(a[lo], a[mid]) > 0) {
                Object t = a[lo]; a[lo] = a[mid]; a[mid] = t;
            }
            if (cmp.compare(a[mid], a[hi]) > 0) {
                Object t = a[mid]; a[mid] = a[hi]; a[hi] = t;
                if (cmp.compare(a[lo], a[mid]) > 0) {
                    Object u = a[lo]; a[lo] = a[mid]; a[mid] = u;
                }
            }

            Object pivot = a[mid];
            int left = lo+1;
            int right = hi-1;
            for (;;) {
                while (cmp.compare(pivot, a[right]) < 0)
                    --right;
                while (left < right && cmp.compare(pivot, a[left]) >= 0)
                    ++left;
                if (left < right) {
                    Object t = a[left]; a[left] = a[right]; a[right] = t;
                    --right;
                }
                else break;
            }

            rquickSort(a, cmp, lo, left);
            lo = left + 1;
        }
    }

    static void rcquickSort(Comparable[] a, int lo, int hi) {
        for (;;) {
            if (hi - lo <= INSERTION_SORT_THRESHOLD) {
                for (int i = lo + 1; i <= hi; i++) {
                    Comparable t = a[i];
                    int j = i - 1;
                    while (j >= lo && t.compareTo(a[j]) < 0) {
                        a[j+1] = a[j];
                        --j;
                    }
                    a[j+1] = t;
                }
                return;
            }

            int mid = (lo + hi) >>> 1;
            if (a[lo].compareTo(a[mid]) > 0) {
                Comparable t = a[lo]; a[lo] = a[mid]; a[mid] = t;
            }
            if (a[mid].compareTo(a[hi]) > 0) {
                Comparable t = a[mid]; a[mid] = a[hi]; a[hi] = t;
                if (a[lo].compareTo(a[mid]) > 0) {
                    Comparable u = a[lo]; a[lo] = a[mid]; a[mid] = u;
                }
            }

            Comparable pivot = a[mid];
            int left = lo+1;
            int right = hi-1;
            for (;;) {
                while (pivot.compareTo(a[right]) < 0)
                    --right;
                while (left < right && pivot.compareTo(a[left]) >= 0)
                    ++left;
                if (left < right) {
                    Comparable t = a[left]; a[left] = a[right]; a[right] = t;
                    --right;
                }
                else break;
            }

            rcquickSort(a, lo, left);
            lo = left + 1;
        }
    }

    static void dquickSort(double[] a, DoubleComparator cmp, int lo, int hi) {
        for (;;) {
            if (hi - lo <= INSERTION_SORT_THRESHOLD) {
                for (int i = lo + 1; i <= hi; i++) {
                    double t = a[i];
                    int j = i - 1;
                    while (j >= lo && cmp.compare(t, a[j]) < 0) {
                        a[j+1] = a[j];
                        --j;
                    }
                    a[j+1] = t;
                }
                return;
            }

            int mid = (lo + hi) >>> 1;
            if (cmp.compare(a[lo], a[mid]) > 0) {
                double t = a[lo]; a[lo] = a[mid]; a[mid] = t;
            }
            if (cmp.compare(a[mid], a[hi]) > 0) {
                double t = a[mid]; a[mid] = a[hi]; a[hi] = t;
                if (cmp.compare(a[lo], a[mid]) > 0) {
                    double u = a[lo]; a[lo] = a[mid]; a[mid] = u;
                }
            }

            double pivot = a[mid];
            int left = lo+1;
            int right = hi-1;
            for (;;) {
                while (cmp.compare(pivot, a[right]) < 0)
                    --right;
                while (left < right && cmp.compare(pivot, a[left]) >= 0)
                    ++left;
                if (left < right) {
                    double t = a[left]; a[left] = a[right]; a[right] = t;
                    --right;
                }
                else break;
            }

            dquickSort(a, cmp, lo, left);
            lo = left + 1;
        }
    }

    static void dcquickSort(double[] a, int lo, int hi) {
        for (;;) {
            if (hi - lo <= INSERTION_SORT_THRESHOLD) {
                for (int i = lo + 1; i <= hi; i++) {
                    double t = a[i];
                    int j = i - 1;
                    while (j >= lo && t < a[j]) {
                        a[j+1] = a[j];
                        --j;
                    }
                    a[j+1] = t;
                }
                return;
            }

            int mid = (lo + hi) >>> 1;
            if (a[lo] > a[mid]) {
                double t = a[lo]; a[lo] = a[mid]; a[mid] = t;
            }
            if (a[mid] > a[hi]) {
                double t = a[mid]; a[mid] = a[hi]; a[hi] = t;
                if (a[lo] > a[mid]) {
                    double u = a[lo]; a[lo] = a[mid]; a[mid] = u;
                }
            }

            double pivot = a[mid];
            int left = lo+1;
            int right = hi-1;
            for (;;) {
                while (pivot < a[right])
                    --right;
                while (left < right && pivot >= a[left])
                    ++left;
                if (left < right) {
                    double t = a[left]; a[left] = a[right]; a[right] = t;
                    --right;
                }
                else break;
            }

            dcquickSort(a, lo, left);
            lo = left + 1;
        }
    }

    static void lquickSort(long[] a, LongComparator cmp, int lo, int hi) {
        for (;;) {
            if (hi - lo <= INSERTION_SORT_THRESHOLD) {
                for (int i = lo + 1; i <= hi; i++) {
                    long t = a[i];
                    int j = i - 1;
                    while (j >= lo && cmp.compare(t, a[j]) < 0) {
                        a[j+1] = a[j];
                        --j;
                    }
                    a[j+1] = t;
                }
                return;
            }

            int mid = (lo + hi) >>> 1;
            if (cmp.compare(a[lo], a[mid]) > 0) {
                long t = a[lo]; a[lo] = a[mid]; a[mid] = t;
            }
            if (cmp.compare(a[mid], a[hi]) > 0) {
                long t = a[mid]; a[mid] = a[hi]; a[hi] = t;
                if (cmp.compare(a[lo], a[mid]) > 0) {
                    long u = a[lo]; a[lo] = a[mid]; a[mid] = u;
                }
            }

            long pivot = a[mid];
            int left = lo+1;
            int right = hi-1;
            for (;;) {
                while (cmp.compare(pivot, a[right]) < 0)
                    --right;
                while (left < right && cmp.compare(pivot, a[left]) >= 0)
                    ++left;
                if (left < right) {
                    long t = a[left]; a[left] = a[right]; a[right] = t;
                    --right;
                }
                else break;
            }

            lquickSort(a, cmp, lo, left);
            lo = left + 1;
        }
    }

    static void lcquickSort(long[] a, int lo, int hi) {
        for (;;) {
            if (hi - lo <= INSERTION_SORT_THRESHOLD) {
                for (int i = lo + 1; i <= hi; i++) {
                    long t = a[i];
                    int j = i - 1;
                    while (j >= lo && t < a[j]) {
                        a[j+1] = a[j];
                        --j;
                    }
                    a[j+1] = t;
                }
                return;
            }

            int mid = (lo + hi) >>> 1;
            if (a[lo] > a[mid]) {
                long t = a[lo]; a[lo] = a[mid]; a[mid] = t;
            }
            if (a[mid] > a[hi]) {
                long t = a[mid]; a[mid] = a[hi]; a[hi] = t;
                if (a[lo] > a[mid]) {
                    long u = a[lo]; a[lo] = a[mid]; a[mid] = u;
                }
            }

            long pivot = a[mid];
            int left = lo+1;
            int right = hi-1;
            for (;;) {
                while (pivot < a[right])
                    --right;
                while (left < right && pivot >= a[left])
                    ++left;
                if (left < right) {
                    long t = a[left]; a[left] = a[right]; a[right] = t;
                    --right;
                }
                else break;
            }

            lcquickSort(a, lo, left);
            lo = left + 1;
        }
    }

    /**
     * Cumulative scan
     *
     * A basic version of scan is straightforward.
     *  Keep dividing by two to threshold segment size, and then:
     *   Pass 1: Create tree of partial sums for each segment
     *   Pass 2: For each segment, cumulate with offset of left sibling
     * See G. Blelloch's http://www.cs.cmu.edu/~scandal/alg/scan.html
     *
     * This version improves performance within FJ framework mainly by
     * allowing second pass of ready left-hand sides to proceed even
     * if some right-hand side first passes are still executing.  It
     * also combines first and second pass for leftmost segment, and
     * for cumulate (not precumulate) also skips first pass for
     * rightmost segment (whose result is not needed for second pass).
     *
     * To manage this, it relies on "phase" phase/state control field
     * maintaining bits CUMULATE, SUMMED, and FINISHED. CUMULATE is
     * main phase bit. When false, segments compute only their sum.
     * When true, they cumulate array elements. CUMULATE is set at
     * root at beginning of second pass and then propagated down. But
     * it may also be set earlier for subtrees with lo==firstIndex (the
     * left spine of tree). SUMMED is a one bit join count. For leafs,
     * set when summed. For internal nodes, becomes true when one
     * child is summed.  When second child finishes summing, it then
     * moves up tree to trigger cumulate phase. FINISHED is also a one
     * bit join count. For leafs, it is set when cumulated. For
     * internal nodes, it becomes true when one child is cumulated.
     * When second child finishes cumulating, it then moves up tree,
     * excecuting finish() at the root.
     *
     * This class maintains only the basic control logic.  Subclasses
     * maintain the "in" and "out" fields, and *Ops classes perform
     * computations
     */
    static abstract class FJScan extends AsyncAction {
        static final int CUMULATE = 1;
        static final int SUMMED   = 2;
        static final int FINISHED = 4;

        final FJScan parent;
        final FJScanOp op;
        FJScan left, right;
        volatile int phase;  // phase/state
        final int lo;
        final int hi;

        static final AtomicIntegerFieldUpdater<FJScan> phaseUpdater =
            AtomicIntegerFieldUpdater.newUpdater(FJScan.class, "phase");

        FJScan(FJScan parent, FJScanOp op, int lo, int hi) {
            this.parent = parent;
            this.op = op;
            this.lo = lo;
            this.hi = hi;
        }

        /** Returns true if can CAS CUMULATE bit true */
        final boolean transitionToCumulate() {
            int c;
            while (((c = phase) & CUMULATE) == 0)
                if (phaseUpdater.compareAndSet(this, c, c | CUMULATE))
                    return true;
            return false;
        }

        public final void compute() {
            if (hi - lo > op.threshold) {
                if (left == null) { // first pass
                    int mid = (lo + hi) >>> 1;
                    left =  op.newSubtask(this, lo, mid);
                    right = op.newSubtask(this, mid, hi);
                }

                boolean cumulate = (phase & CUMULATE) != 0;
                if (cumulate)
                    op.pushDown(this, left, right);

                if (!cumulate || right.transitionToCumulate())
                    right.fork();
                if (!cumulate || left.transitionToCumulate())
                    left.compute();
            }
            else {
                int cb;
                for (;;) { // Establish action: sum, cumulate, or both
                    int b = phase;
                    if ((b & FINISHED) != 0) // already done
                        return;
                    if ((b & CUMULATE) != 0)
                        cb = FINISHED;
                    else if (lo == op.firstIndex) // combine leftmost
                        cb = (SUMMED|FINISHED);
                    else
                        cb = SUMMED;
                    if (phaseUpdater.compareAndSet(this, b, b|cb))
                        break;
                }

                if (cb == SUMMED)
                    op.sumLeaf(lo, hi, this);
                else if (cb == FINISHED)
                    op.cumulateLeaf(lo, hi, this);
                else if (cb == (SUMMED|FINISHED))
                    op.sumAndCumulateLeaf(lo, hi, this);

                // propagate up
                FJScan ch = this;
                FJScan par = parent;
                for (;;) {
                    if (par == null) {
                        if ((cb & FINISHED) != 0)
                            ch.finish();
                        break;
                    }
                    int pb = par.phase;
                    if ((pb & cb & FINISHED) != 0) { // both finished
                        ch = par;
                        par = par.parent;
                    }
                    else if ((pb & cb & SUMMED) != 0) { // both summed
                        op.pushUp(par, par.left, par.right);
                        int refork =
                            ((pb & CUMULATE) == 0 &&
                             par.lo == op.firstIndex)? CUMULATE : 0;
                        int nextPhase = pb|cb|refork;
                        if (pb == nextPhase ||
                            phaseUpdater.compareAndSet(par, pb, nextPhase)) {
                            if (refork != 0)
                                par.fork();
                            cb = SUMMED; // drop finished bit
                            ch = par;
                            par = par.parent;
                        }
                    }
                    else if (phaseUpdater.compareAndSet(par, pb, pb|cb))
                        break;
                }
            }
        }

        // no-op versions of methods to get/set in/out, overridden as
        // appropriate in subclasses
        Object rgetIn() { return null; }
        Object rgetOut() { return null; }
        void rsetIn(Object x) { }
        void rsetOut(Object x) { }

        double dgetIn() { return 0; }
        double dgetOut() { return 0; }
        void dsetIn(double x) { }
        void dsetOut(double x) { }

        long lgetIn() { return 0; }
        long lgetOut() { return 0; }
        void lsetIn(long x) { }
        void lsetOut(long x) { }
    }

    // Subclasses adding in/out fields of the appropriate type
    static final class FJRScan extends FJScan {
        Object in;
        Object out;
        FJRScan(FJScan parent, FJScanOp op, int lo, int hi) {
            super(parent, op, lo, hi);
        }
        Object rgetIn() { return in; }
        Object rgetOut() { return out; }
        void rsetIn(Object x) { in = x; }
        void rsetOut(Object x) { out = x; }
    }

    static final class FJDScan extends FJScan {
        double in;
        double out;
        FJDScan(FJScan parent, FJScanOp op, int lo, int hi) {
            super(parent, op, lo, hi);
        }
        double dgetIn() { return in; }
        double dgetOut() { return out; }
        void dsetIn(double x) { in = x; }
        void dsetOut(double x) { out = x; }

    }

    static final class FJLScan extends FJScan {
        long in;
        long out;
        FJLScan(FJScan parent, FJScanOp op, int lo, int hi) {
            super(parent, op, lo, hi);
        }
        long lgetIn() { return in; }
        long lgetOut() { return out; }
        void lsetIn(long x) { in = x; }
        void lsetOut(long x) { out = x; }
    }

    /**
     * Computational operations for FJSCan
     */
    static abstract class FJScanOp {
        final int threshold;
        final int firstIndex;
        final int upperBound;
        FJScanOp(Prefix prefix) {
            this.firstIndex = prefix.firstIndex;
            this.upperBound = prefix.upperBound;
            this.threshold = prefix.threshold;
        }
        abstract void pushDown(FJScan parent, FJScan left, FJScan right);
        abstract void pushUp(FJScan parent, FJScan left, FJScan right);
        abstract void sumLeaf(int lo, int hi, FJScan f);
        abstract void cumulateLeaf(int lo, int hi, FJScan f);
        abstract void sumAndCumulateLeaf(int lo, int hi, FJScan f);
        abstract FJScan newSubtask(FJScan parent, int lo, int hi);
    }

    static abstract class FJRScanOp extends FJScanOp {
        final Object[] array;
        final Reducer reducer;
        final Object base;
        FJRScanOp(RPrefix prefix, Reducer reducer, Object base) {
            super(prefix);
            this.array = prefix.pa.array;
            this.reducer = reducer;
            this.base = base;
        }
        final void pushDown(FJScan parent, FJScan left, FJScan right) {
            Object pin = parent.rgetIn();
            left.rsetIn(pin);
            right.rsetIn(reducer.combine(pin, left.rgetOut()));
        }
        final void pushUp(FJScan parent, FJScan left, FJScan right) {
            parent.rsetOut(reducer.combine(left.rgetOut(),
                                           right.rgetOut()));
        }
        final FJScan newSubtask(FJScan parent, int lo, int hi) {
            FJRScan f = new FJRScan(parent, this, lo, hi);
            f.in = base;
            f.out = base;
            return f;
        }
    }

    static final class FJRCumulateOp extends FJRScanOp {
        FJRCumulateOp(RPrefix prefix, Reducer reducer, Object base) {
            super(prefix, reducer, base);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            Object sum = base;
            if (hi != upperBound) {
                Object[] arr = array;
                for (int i = lo; i < hi; ++i)
                    sum = reducer.combine(sum, arr[i]);
            }
            f.rsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            Object[] arr = array;
            Object sum = f.rgetIn();
            for (int i = lo; i < hi; ++i)
                arr[i] = sum = reducer.combine(sum, arr[i]);
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            Object[] arr = array;
            Object sum = base;
            for (int i = lo; i < hi; ++i)
                arr[i] = sum = reducer.combine(sum, arr[i]);
            f.rsetOut(sum);
        }
    }

    static final class FJRPrecumulateOp extends FJRScanOp {
        FJRPrecumulateOp(RPrefix prefix, Reducer reducer, Object base) {
            super(prefix, reducer, base);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            Object[] arr = array;
            Object sum = base;
            for (int i = lo; i < hi; ++i)
                sum = reducer.combine(sum, arr[i]);
            f.rsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            Object[] arr = array;
            Object sum = f.rgetIn();
            for (int i = lo; i < hi; ++i) {
                Object x = arr[i];
                arr[i] = sum;
                sum = reducer.combine(sum, x);
            }
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            Object[] arr = array;
            Object sum = base;
            for (int i = lo; i < hi; ++i) {
                Object x = arr[i];
                arr[i] = sum;
                sum = reducer.combine(sum, x);
            }
            f.rsetOut(sum);
        }
    }

    static abstract class FJDScanOp extends FJScanOp {
        final double[] array;
        final DoubleReducer reducer;
        final double base;
        FJDScanOp(DPrefix prefix, DoubleReducer reducer, double base) {
            super(prefix);
            this.array = prefix.pa.array;
            this.reducer = reducer;
            this.base = base;
        }
        final void pushDown(FJScan parent, FJScan left, FJScan right) {
            double pin = parent.dgetIn();
            left.dsetIn(pin);
            right.dsetIn(reducer.combine(pin, left.dgetOut()));
        }
        final void pushUp(FJScan parent, FJScan left, FJScan right) {
            parent.dsetOut(reducer.combine(left.dgetOut(),
                                           right.dgetOut()));
        }
        final FJScan newSubtask(FJScan parent, int lo, int hi) {
            FJDScan f = new FJDScan(parent, this, lo, hi);
            f.in = base;
            f.out = base;
            return f;
        }
    }

    static final class FJDCumulateOp extends FJDScanOp {
        FJDCumulateOp(DPrefix prefix, DoubleReducer reducer, double base) {
            super(prefix, reducer, base);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            double sum = base;
            if (hi != upperBound) {
                double[] arr = array;
                for (int i = lo; i < hi; ++i)
                    sum = reducer.combine(sum, arr[i]);
            }
            f.dsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = f.dgetIn();
            for (int i = lo; i < hi; ++i)
                arr[i] = sum = reducer.combine(sum, arr[i]);
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = base;
            for (int i = lo; i < hi; ++i)
                arr[i] = sum = reducer.combine(sum, arr[i]);
            f.dsetOut(sum);
        }
    }

    static final class FJDPrecumulateOp extends FJDScanOp {
        FJDPrecumulateOp(DPrefix prefix, DoubleReducer reducer, double base) {
            super(prefix, reducer, base);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = base;
            for (int i = lo; i < hi; ++i)
                sum = reducer.combine(sum, arr[i]);
            f.dsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = f.dgetIn();
            for (int i = lo; i < hi; ++i) {
                double x = arr[i];
                arr[i] = sum;
                sum = reducer.combine(sum, x);
            }
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = base;
            for (int i = lo; i < hi; ++i) {
                double x = arr[i];
                arr[i] = sum;
                sum = reducer.combine(sum, x);
            }
            f.dsetOut(sum);
        }
    }

    static abstract class FJLScanOp extends FJScanOp {
        final long[] array;
        final LongReducer reducer;
        final long base;
        FJLScanOp(LPrefix prefix, LongReducer reducer, long base) {
            super(prefix);
            this.array = prefix.pa.array;
            this.reducer = reducer;
            this.base = base;
        }
        final void pushDown(FJScan parent, FJScan left, FJScan right) {
            long pin = parent.lgetIn();
            left.lsetIn(pin);
            right.lsetIn(reducer.combine(pin, left.lgetOut()));
        }
        final void pushUp(FJScan parent, FJScan left, FJScan right) {
            parent.lsetOut(reducer.combine(left.lgetOut(),
                                           right.lgetOut()));
        }
        final FJScan newSubtask(FJScan parent, int lo, int hi) {
            FJLScan f = new FJLScan(parent, this, lo, hi);
            f.in = base;
            f.out = base;
            return f;
        }
    }

    static final class FJLCumulateOp extends FJLScanOp {
        FJLCumulateOp(LPrefix prefix, LongReducer reducer, long base) {
            super(prefix, reducer, base);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            long sum = base;
            if (hi != upperBound) {
                long[] arr = array;
                for (int i = lo; i < hi; ++i)
                    sum = reducer.combine(sum, arr[i]);
            }
            f.lsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = f.lgetIn();
            for (int i = lo; i < hi; ++i)
                arr[i] = sum = reducer.combine(sum, arr[i]);
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = base;
            for (int i = lo; i < hi; ++i)
                arr[i] = sum = reducer.combine(sum, arr[i]);
            f.lsetOut(sum);
        }
    }

    static final class FJLPrecumulateOp extends FJLScanOp {
        FJLPrecumulateOp(LPrefix prefix, LongReducer reducer, long base) {
            super(prefix, reducer, base);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = base;
            for (int i = lo; i < hi; ++i)
                sum = reducer.combine(sum, arr[i]);
            f.lsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = f.lgetIn();
            for (int i = lo; i < hi; ++i) {
                long x = arr[i];
                arr[i] = sum;
                sum = reducer.combine(sum, x);
            }
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = base;
            for (int i = lo; i < hi; ++i) {
                long x = arr[i];
                arr[i] = sum;
                sum = reducer.combine(sum, x);
            }
            f.lsetOut(sum);
        }
    }

    // specialized versions for plus

    static abstract class FJDScanPlusOp extends FJScanOp {
        final double[] array;
        FJDScanPlusOp(DPrefix prefix) {
            super(prefix);
            this.array = prefix.pa.array;
        }
        final void pushDown(FJScan parent, FJScan left, FJScan right) {
            double pin = parent.dgetIn();
            left.dsetIn(pin);
            right.dsetIn(pin + left.dgetOut());
        }
        final void pushUp(FJScan parent, FJScan left, FJScan right) {
            parent.dsetOut(left.dgetOut() + right.dgetOut());
        }
        final FJScan newSubtask(FJScan parent, int lo, int hi) {
            FJDScan f = new FJDScan(parent, this, lo, hi);
            f.in = 0.0;
            f.out = 0.0;
            return f;
        }
    }

    static final class FJDCumulatePlusOp extends FJDScanPlusOp {
        FJDCumulatePlusOp(DPrefix prefix) {
            super(prefix);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            double sum = 0.0;
            if (hi != upperBound) {
                double[] arr = array;
                for (int i = lo; i < hi; ++i)
                    sum += arr[i];
            }
            f.dsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = f.dgetIn();
            for (int i = lo; i < hi; ++i)
                arr[i] = sum += arr[i];
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = 0.0;
            for (int i = lo; i < hi; ++i)
                arr[i] = sum += arr[i];
            f.dsetOut(sum);
        }
    }

    static final class FJDPrecumulatePlusOp extends FJDScanPlusOp {
        FJDPrecumulatePlusOp(DPrefix prefix) {
            super(prefix);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = 0.0;
            for (int i = lo; i < hi; ++i)
                sum += arr[i];
            f.dsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = f.dgetIn();
            for (int i = lo; i < hi; ++i) {
                double x = arr[i];
                arr[i] = sum;
                sum += x;
            }
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            double[] arr = array;
            double sum = 0.0;
            for (int i = lo; i < hi; ++i) {
                double x = arr[i];
                arr[i] = sum;
                sum += x;
            }
            f.dsetOut(sum);
        }
    }

    static abstract class FJLScanPlusOp extends FJScanOp {
        final long[] array;
        FJLScanPlusOp(LPrefix prefix) {
            super(prefix);
            this.array = prefix.pa.array;
        }
        final void pushDown(FJScan parent, FJScan left, FJScan right) {
            long pin = parent.lgetIn();
            left.lsetIn(pin);
            right.lsetIn(pin + left.lgetOut());
        }

        final void pushUp(FJScan parent, FJScan left, FJScan right) {
            parent.lsetOut(left.lgetOut() + right.lgetOut());
        }

        final FJScan newSubtask(FJScan parent, int lo, int hi) {
            FJLScan f = new FJLScan(parent, this, lo, hi);
            f.in = 0L;
            f.out = 0L;
            return f;
        }
    }

    static final class FJLCumulatePlusOp extends FJLScanPlusOp {
        FJLCumulatePlusOp(LPrefix prefix) {
            super(prefix);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            long sum = 0L;
            if (hi != upperBound) {
                long[] arr = array;
                for (int i = lo; i < hi; ++i)
                    sum += arr[i];
            }
            f.lsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = f.lgetIn();
            for (int i = lo; i < hi; ++i)
                arr[i] = sum += arr[i];
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = 0L;
            for (int i = lo; i < hi; ++i)
                arr[i] = sum += arr[i];
            f.lsetOut(sum);
        }
    }

    static final class FJLPrecumulatePlusOp extends FJLScanPlusOp {
        FJLPrecumulatePlusOp(LPrefix prefix) {
            super(prefix);
        }
        void sumLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = 0L;
            for (int i = lo; i < hi; ++i)
                sum += arr[i];
            f.lsetOut(sum);
        }
        void cumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = f.lgetIn();
            for (int i = lo; i < hi; ++i) {
                long x = arr[i];
                arr[i] = sum;
                sum += x;
            }
        }
        void sumAndCumulateLeaf(int lo, int hi, FJScan f) {
            long[] arr = array;
            long sum = 0L;
            for (int i = lo; i < hi; ++i) {
                long x = arr[i];
                arr[i] = sum;
                sum += x;
            }
            f.lsetOut(sum);
        }
    }
}
