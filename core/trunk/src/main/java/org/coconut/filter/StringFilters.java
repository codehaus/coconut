/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import java.io.Serializable;

public class StringFilters {

    public static Filter<String> contains(CharSequence charSequence) {
        return new ContainsFilter(charSequence);
    }

    public static Filter<String> startsWith(String string) {
        return new ContainsFilter(string);
    }

    static final class ContainsFilter implements Filter<String>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 9017164210753456879L;

        private final CharSequence charSequence;

        ContainsFilter(CharSequence charSequence) {
            if (charSequence == null) {
                throw new NullPointerException("charSequence is null");
            }
            this.charSequence = charSequence;
        }

        public boolean accept(String element) {
            return element.contains(charSequence);
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ContainsFilter
                    && ((ContainsFilter) obj).charSequence.equals(charSequence);
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return charSequence.hashCode();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "String contains '" + charSequence + "'";
        }
    }

    static final class StartsWithFilter implements Filter<String>, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -4403651350378953066L;

        private final String string;

        StartsWithFilter(String string) {
            if (string == null) {
                throw new NullPointerException("string is null");
            }
            this.string = string;
        }

        public boolean accept(String element) {
            return element.startsWith(string);
        }
    }
}
