/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.coconut.filter.Filters.isType;

import java.util.Collection;
import java.util.Map;

import org.coconut.filter.Filters.IsTypeFilter;
import org.coconut.filter.util.StringFilters.ContainsFilter;

/**
 * Not quite done yet.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CommonFilters {
    public final static IsTypeFilter IS_STRING = isType(String.class);

    public final static IsTypeFilter IS_LONG = isType(Long.class);

    public final static IsTypeFilter IS_INTEGER = isType(Integer.class);

    public static void main(String[] args) {
        Map<String, Integer> m = null;
        Filter<String> f = mapKeyContainedIn(m);
        f.accept("dd");
    }

    public static <T> Filter<T> mapKeyContainedIn(Map<? extends T, ?> m) {
        return null;
    }

    public void keyInMap(Map m) {

    }

    /**
     * The map is used as a backing map, any change to the map will be reflected
     * in the use of this method.
     * 
     * @param <V>
     * @param map
     * @return
     */
    public static <V> Filter<V> valueInMap(final Map<?, V> map) {
        if (map == null) {
            throw new NullPointerException("m is null");
        }
        return new Filter<V>() {
            public boolean accept(V element) {
                return map.containsValue(element);
            }
        };
    }

    public void entryInMap(Map m) {

    }

    public void containedIn(Collection col) {

    }

    public class ContainsKeyInMapFilter {

    }

    public class ContainsValueInMapFilter {

    }

    public class InCollectionFilter {
        // ThreadPoolExecutor
    }

    public static class EndsWithFilter {

    }

    public static Filter<String> stringContains(CharSequence charSequence) {
        return new ContainsFilter(charSequence);
    }

    public static class EqualsIgnoreCaseFilter {

    }

    public static class StartsWithFilter {

    }

    public static class CharAtFilter {

    }

    public static class Matches {

    }

}
