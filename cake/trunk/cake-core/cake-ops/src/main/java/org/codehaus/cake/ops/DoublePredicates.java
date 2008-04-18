package org.codehaus.cake.ops;

import java.io.Serializable;

import org.codehaus.cake.ops.Ops.DoublePredicate;

public class DoublePredicates {
    /**
     * Returns a predicate that accepts any long that is equal to the value specified.
     * <p>
     * The predicate is serializable.
     *
     * @param element
     *            the value of the equals predicate
     * @return a predicate that accepts any long that is equal to the value specified
     */
    public static DoublePredicate equalsTo(double element) {
        return new EqualsToDoublePredicate(element);
    }
    /**
     * Creates a LongPredicate that performs a logical logical NOT on the supplied
     * LongPredicate. More formally
     *
     * <pre>
     * !predicate.evaluate(value);
     * </pre>
     *
     * <p>
     * If the specified predicate is serializable the returned predicate will also be
     * serializable.
     *
     * @param predicate
     *            the predicate to negate
     * @return the newly created LongPredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static DoublePredicate not(DoublePredicate predicate) {
        return new NotDoublePredicate(predicate);
    }
    static class EqualsToDoublePredicate implements DoublePredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final double equalsTo;

        EqualsToDoublePredicate(double equalsTo) {
            this.equalsTo = equalsTo;
        }

        /**
         * Returns <code>true</code> if the specified value is equal to the value that
         * was used when constructing this predicate, otherwise <code>false</code>.
         *
         * @param t
         *            the value to compare with
         * @return <code>true</code> if the specified value is equal to the value that
         *         was used when constructing this predicate, otherwise <code>false</code>.
         */
        public boolean op(double t) {
            return equalsTo == t;
        }

        /**
         * @return the value we are comparing with
         */
        public double getEqualsTo() {
            return equalsTo;
        }
    }
    
    /**
     * A LongPredicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    static final class NotDoublePredicate implements DoublePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The LongPredicate to negate. */
        private final DoublePredicate predicate;

        /**
         * Creates a new NotLongPredicate.
         *
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        NotDoublePredicate(DoublePredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * LongPredicate.
         *
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied LongPredicate
         */
        public boolean op(double element) {
            return !predicate.op(element);
        }

        /**
         * Returns the predicate that is being negated.
         *
         * @return the predicate that is being negated.
         */
        public DoublePredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }
}
