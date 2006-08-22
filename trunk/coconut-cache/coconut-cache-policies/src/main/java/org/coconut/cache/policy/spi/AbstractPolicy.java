/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.spi;

import java.util.ArrayList;
import java.util.List;

import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractPolicy<T> implements ReplacementPolicy<T> {

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#clear()
     */
    public void clear() {
        while (evictNext() != null) {
            /* ignore */
        }
    }

    // to slow use in practice.
    // /**
    // * Slow
    // * @return
    // */
    // public int getSize() {
    // return peekAll().size();
    // }

    public double getEvictionScore() {
        return -1;
    }

    // TODO don't know if its usefull at all???
    // /**
    // * Adds all the elements to the policy. The elements will be added
    // * accordingly to the returning order of the iterator.
    // *
    // * @param items
    // * the elements to add
    // * @return the references of each element that was added. The first
    // element
    // * returned by the lists iterator will have index 0 in the array.
    // * The last element returned by the iterator will have index
    // */
    // public int[] addAll(List<T> items) {
    // int[] result = new int[items.size()];
    // int count = 0;
    // for (T t : items) {
    // result[count++] = add(t);
    // }
    // return result;
    // }

    /**
     * Evict entries.
     * 
     * @param number
     *            the number of entries to evict
     * @return a list containing the elements that was evicted
     */
    public List<T> evict(int number) {
        int count = number;
        // do not use number as initial capacity, might be Integer.MAX_VALUE
        ArrayList<T> list = new ArrayList<T>(Math.min(100, number));
        T i = evictNext();
        while (i != null && count-- > 0) {
            list.add(i);
            i = evictNext();
        }
        return list;
    }

    public List<T> evictAll() {
        return evict(Integer.MAX_VALUE);
    }

    public int getSize() {
        return peekAll().size();
    }
}
