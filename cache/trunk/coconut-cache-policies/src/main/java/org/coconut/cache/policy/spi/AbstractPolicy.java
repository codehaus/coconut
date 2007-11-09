/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.spi;

import java.util.ArrayList;
import java.util.List;

import org.coconut.cache.spi.ReplacementPolicy;
import org.coconut.core.AttributeMaps;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <T>
 *            the type of objects contained within the replacement policy
 */
public abstract class AbstractPolicy<T> implements ReplacementPolicy<T> {

    /**
     * Calls {@link ReplacementPolicy#add(Object, org.coconut.core.AttributeMap)} with an
     * empty attributemap.
     * 
     * @param element
     *            the element to add
     * @return a positive index that can be used to reference the element in the
     *         replacement policy. A negative number is returned if the element is not
     *         accepted into the replacement policy
     */
    public int add(T element) {
        return add(element, AttributeMaps.EMPTY_MAP);
    }

    /**
     * Calls {@link ReplacementPolicy#update(int, Object, org.coconut.core.AttributeMap)}
     * with an empty attributemap.
     * 
     * @param index
     *            the index of the previous element
     * @param newElement
     *            the new element that should replace the previous element
     * @return <tt>true</tt> if the policy accepted the new element, otherwise
     *         <tt>false</tt>
     */
    public boolean update(int index, T newElement) {
        return update(index, newElement, AttributeMaps.EMPTY_MAP);
    }

    /**
     * Adds all the elements to the policy. The elements will be added accordingly to the
     * returning order of the iterator.
     * 
     * @param items
     *            the elements to add
     * @return the references of each element that was added. The first element returned
     *         by the lists iterator will have index 0 in the array. The last element
     *         returned by the iterator will have index
     */
    public int[] addAll(List<T> items) {
        int[] result = new int[items.size()];
        int count = 0;
        for (T t : items) {
            result[count++] = add(t);
        }
        return result;
    }

    /**
     * Evict at most the specified entries. If the
     * 
     * @param number
     *            the number of entries to evict
     * @return a list containing the elements that was evicted in the order they where
     *         evicted
     */
    public List<T> evict(final int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("number must be a positive number, was " + number);
        }
        int count = number;
        // do not use number as initial capacity, might be Integer.MAX_VALUE
        ArrayList<T> list = new ArrayList<T>(Math.min(getSize(), number));
        T i = evictNext();
        while (i != null) {
            list.add(i);
            i = --count > 0 ? evictNext() : null;
        }
        return list;
    }

    /**
     * Evicts all the entries in the policy.
     * 
     * @return all the entries in the policy.
     */
    public List<T> evictAll() {
        return evict(Integer.MAX_VALUE);
    }
}
