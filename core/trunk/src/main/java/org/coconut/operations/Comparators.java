/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import java.io.Serializable;
import java.util.Comparator;

import org.coconut.operations.Ops.DoubleComparator;
import org.coconut.operations.Ops.IntComparator;
import org.coconut.operations.Ops.LongComparator;

public final class Comparators {
    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Comparators() {}

    // /CLOVER:ON
    public static final DoubleComparator DOUBLE_COMPARATOR = new NaturalDoubleComparator();

    public static final IntComparator INT_COMPARATOR = new NaturalIntComparator();

    public static final LongComparator LONG_COMPARATOR = new NaturalLongComparator();

    public static final Comparator NATURAL_COMPARATOR = new NaturalComparator();

    public static <T extends Comparable<? super T>> Comparator<T> naturalComparator() {
        return NATURAL_COMPARATOR;
    }

    /**
     * A Comparator for Comparable.objects
     */
    static final class NaturalComparator<T extends Comparable<? super T>> implements Comparator<T>,
            Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 949691819933412722L;

        public int compare(T a, T b) {
            return a.compareTo(b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return NATURAL_COMPARATOR;
        }
    }

    /**
     * A comparator for doubles relying on natural ordering
     */
    static final class NaturalDoubleComparator implements DoubleComparator, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 8763765406476535022L;

        public int compare(double a, double b) {
            return Double.compare(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_COMPARATOR;
        }
    }

    /**
     * A comparator for ints relying on natural ordering
     */
    static final class NaturalIntComparator implements IntComparator, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -937141628195281323L;

        public int compare(int a, int b) {
            return a < b ? -1 : ((a > b) ? 1 : 0);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_COMPARATOR;
        }
    }

    /**
     * A comparator for longs relying on natural ordering
     */
    static final class NaturalLongComparator implements LongComparator, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 948562823440773841L;

        public int compare(long a, long b) {
            return a < b ? -1 : ((a > b) ? 1 : 0);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_COMPARATOR;
        }
    }
}
