/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.util;

import org.coconut.filter.Filter;

public class StringFilters {

    public static void main(String[] args) {
        System.out.println("erer".contains(""));
    }

    public static Filter<String> StringContains(CharSequence charSequence) {
        return new ContainsFilter(charSequence);
    }

    public static class ContainsFilter implements Filter<String> {
        private final CharSequence charSequence;

        public ContainsFilter(CharSequence charSequence) {
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
