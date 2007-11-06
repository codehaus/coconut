/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

import java.io.Serializable;

/**
 * Various instances of String based filters.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class StringPredicates {

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private StringPredicates() {}

    // /CLOVER:ON

    /**
     * Returns a filter that will accept any String that contains the specified
     * CharSequence.
     * 
     * @param charSequence
     *            the CharSequence the filter will check for
     * @return a filter that will accept any String that contains the specified
     *         CharSequence
     * @throws NullPointerException
     *             if the specified charSequence is <code>null</code>
     */
    public static Predicate<String> contains(CharSequence charSequence) {
        return new ContainsFilter(charSequence);
    }

    /**
     * Returns a filter that will accept any String that starts with the specified String.
     * 
     * @param startsWith
     *            the String the filter will check against
     * @return a filter that will accept any String that contains the specified
     *         CharSequence
     * @throws NullPointerException
     *             if the specified String is <code>null</code>
     */
    public static Predicate<String> startsWith(String startsWith) {
        return new ContainsFilter(startsWith);
    }

    /**
     * A filter that will accept any String that contains the specified CharSequence.
     */
    static final class ContainsFilter implements Predicate<String>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 9017164210753456879L;

        /** The CharSequence the string must contain with to be accepted. */
        private final CharSequence charSequence;

        /**
         * Creates a new ContainsFilter.
         * 
         * @param charSequence
         *            the CharSequence the string must contain with to be accepted
         * @throws NullPointerException
         *             if the specified charSequence is <code>null</code>
         */
        ContainsFilter(CharSequence charSequence) {
            if (charSequence == null) {
                throw new NullPointerException("charSequence is null");
            }
            this.charSequence = charSequence;
        }

        /** {@inheritDoc} */
        public boolean evaluate(String element) {
            return element.contains(charSequence);
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ContainsFilter
                    && ((ContainsFilter) obj).charSequence.equals(charSequence);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return charSequence.hashCode();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "String contains '" + charSequence + "'";
        }
    }

    /**
     * A filter that will accept any String that starts with the specified String.
     */
    static final class StartsWithFilter implements Predicate<String>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -4403651350378953066L;

        /** The String the string must start with to be accepted. */
        private final String startsWith;

        /**
         * Creates a new StartsWithFilter.
         * 
         * @param startsWith
         *            the String the filter will check against
         * @throws NullPointerException
         *             if the specified String is <code>null</code>
         */
        StartsWithFilter(String startsWith) {
            if (startsWith == null) {
                throw new NullPointerException("startsWith is null");
            }
            this.startsWith = startsWith;
        }

        /** {@inheritDoc} */
        public boolean evaluate(String element) {
            return element.startsWith(startsWith);
        }
    }
}
