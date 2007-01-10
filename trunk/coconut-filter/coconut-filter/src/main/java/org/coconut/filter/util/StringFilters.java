/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.util;

import java.io.Serializable;

import org.coconut.filter.Filter;

public class StringFilters {

    public static Filter<String> stringContains(CharSequence charSequence) {
        return new ContainsFilter(charSequence);
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

}
