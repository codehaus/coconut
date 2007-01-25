/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.util;

import java.io.Serializable;

import org.coconut.filter.Filter;

public class StringFilters {

    public static Filter<String> stringContains(CharSequence charSequence) {
        return new ContainsFilter(charSequence);
    }

    public static Filter<String> stringStartsWith(String string) {
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
