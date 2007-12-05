/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import java.io.Serializable;

import org.coconut.operations.Ops.Predicate;

/**
 * Various String based {@link Predicate} methods and utility classes.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StringPredicates.java 472 2007-11-19 09:34:26Z kasper $
 */
public final class StringPredicates {

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private StringPredicates() {}

    // /CLOVER:ON

    /**
     * Creates a Predicate that will accept any String that contains the specified
     * CharSequence. The returned predicate is serializable.
     * 
     * @param contains
     *            the CharSequence the predicate will check for
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified charSequence is <code>null</code>
     * @see String#contains(CharSequence)
     */
    public static Predicate<String> contains(CharSequence contains) {
        return new ContainsPredicate(contains);
    }

    /**
     * Creates a Predicate that will accept any String that ends with the specified
     * String. The returned predicate is serializable.
     * 
     * @param endsWith
     *            the String the predicate will check against
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified String is <code>null</code>
     * @see String#endsWith(String)
     */
    public static Predicate<String> endsWith(String endsWith) {
        return new EndsWithPredicate(endsWith);
    }

    /**
     * Creates a Predicate that will accept any String that is equal to the specified
     * String ignoring case considerations. The returned predicate is serializable.
     * 
     * @param equalsToIgnoreCase
     *            the String the predicate will check against
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified String is <code>null</code>
     * @see String#equalsIgnoreCase(String)
     */
    public static Predicate<String> equalsToIgnoreCase(String equalsToIgnoreCase) {
        return new EqualsIgnoreCasePredicate(equalsToIgnoreCase);
    }

    /**
     * Creates a Predicate that will accept any String that starts with the specified
     * String. The returned predicate is serializable.
     * 
     * @param startsWith
     *            the String the predicate will check against
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified String is <code>null</code>
     * @see String#startsWith(String)
     */
    public static Predicate<String> startsWith(String startsWith) {
        return new StartsWithPredicate(startsWith);
    }

    /**
     * A Predicate that will accept any String that contains the specified CharSequence
     * 
     * @see String#contains(CharSequence)
     */
    static final class ContainsPredicate implements Predicate<String>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5349595721464596428L;

        /** The CharSequence the string must contain to be accepted. */
        private final CharSequence contains;

        /**
         * Creates a new ContainsPredicate.
         * 
         * @param contains
         *            the CharSequence the predicate will check for
         * @throws NullPointerException
         *             if the specified charSequence is <code>null</code>
         */
        ContainsPredicate(CharSequence contains) {
            if (contains == null) {
                throw new NullPointerException("contains is null");
            }
            this.contains = contains;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ContainsPredicate
                    && ((ContainsPredicate) obj).contains.equals(contains);
        }

        /** {@inheritDoc} */
        public boolean evaluate(String element) {
            return element.contains(contains);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return contains.hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "String contains '" + contains + "'";
        }
    }

    /**
     * A Predicate that will accept any String that ends with the specified String.
     */
    static final class EndsWithPredicate implements Predicate<String>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8903408398832899327L;

        /** The String the string must end with to be accepted. */
        private final String endsWith;

        /**
         * Creates a new EndsWithPredicate.
         * 
         * @param endsWith
         *            the String the predicate will check against
         * @throws NullPointerException
         *             if the specified String is <code>null</code>
         */
        EndsWithPredicate(String endsWith) {
            if (endsWith == null) {
                throw new NullPointerException("endsWith is null");
            }
            this.endsWith = endsWith;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof EndsWithPredicate
                    && ((EndsWithPredicate) obj).endsWith.equals(endsWith);
        }

        /** {@inheritDoc} */
        public boolean evaluate(String element) {
            return element.endsWith(endsWith);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return endsWith.hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "String ends with '" + endsWith + "'";
        }
    }

    /**
     * A Predicate that will accept any String that is equal to the specified String
     * ignoring case considerations
     * 
     * @see String#equalsIgnoreCase(String)
     */
    static final class EqualsIgnoreCasePredicate implements Predicate<String>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8903408398832899327L;

        /** The String the string must be equal to (ignoring case considerations). */
        private final String equalsIgnoreCase;

        /**
         * Creates a new EqualsIgnoreCasePredicate.
         * 
         * @param equalsIgnoreCase
         *            the String the predicate will check against
         * @throws NullPointerException
         *             if the specified String is <code>null</code>
         */
        EqualsIgnoreCasePredicate(String equalsIgnoreCase) {
            if (equalsIgnoreCase == null) {
                throw new NullPointerException("equalsIgnoreCase is null");
            }
            this.equalsIgnoreCase = equalsIgnoreCase;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof EqualsIgnoreCasePredicate
                    && ((EqualsIgnoreCasePredicate) obj).equalsIgnoreCase.equals(equalsIgnoreCase);
        }

        /** {@inheritDoc} */
        public boolean evaluate(String element) {
            return element.equalsIgnoreCase(equalsIgnoreCase);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return equalsIgnoreCase.hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "String equals ignore case '" + equalsIgnoreCase + "'";
        }
    }

    /**
     * A Predicate that will accept any String that starts with the specified String.
     */
    static final class StartsWithPredicate implements Predicate<String>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8903408398832899327L;

        /** The String the string must start with to be accepted. */
        private final String startsWith;

        /**
         * Creates a new StartsWithPredicate.
         * 
         * @param startsWith
         *            the String the predicate will check against
         * @throws NullPointerException
         *             if the specified String is <code>null</code>
         */
        StartsWithPredicate(String startsWith) {
            if (startsWith == null) {
                throw new NullPointerException("startsWith is null");
            }
            this.startsWith = startsWith;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof StartsWithPredicate
                    && ((StartsWithPredicate) obj).startsWith.equals(startsWith);
        }

        /** {@inheritDoc} */
        public boolean evaluate(String element) {
            return element.startsWith(startsWith);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return startsWith.hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "String starts with '" + startsWith + "'";
        }
    }
}
