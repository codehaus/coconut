/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.List;

import org.coconut.core.AttributeMap;

/**
 * A (cache) replacement policy determines which data item(s) should be evicted (deleted)
 * from the cache when the free space is insufficient for accommodating an item to be
 * cached. Normally users should not need to implement this interface, only if they want
 * to implement new replacement polices. This library comes with a number of predefined
 * replacement policies, see {@link org.coconut.cache.policy.Policies} for the most
 * commonly used policies.
 * <p>
 * For performance reasons cache policies are not expected to be thread-safe. Instead, any
 * cache implementation using a replacement policy must maintain thread safety.
 * <p>
 * All general-purpose <tt>ReplacementPolicy</tt> implementation classes should provide
 * a a void (no arguments) constructor. There is no way to enforce this recommendation (as
 * interfaces cannot contain constructors) but all of the general-purpose replacement
 * policy implementations available in this library comply.
 * <p>
 * We use an <tt>int</tt> to represent a pointer to all entries because it has a lower
 * memory footprint then using objects. While this is no problem for a cache with one
 * single policy the overhead can be significant when using an adaptable caching strategy
 * where we monitor tens or hundreds of polices. Furthermore, we observe that (as a
 * general rule) most entries that are evicted from a cache are the entries that have not
 * been accessed for the longest time. Hence these objects are most often in the tenure GC
 * area.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 * @param <T>
 *            the type of data maintained by this policy
 */
public interface ReplacementPolicy<T> {

    /**
     * Adds the specified element to the replacement policy. If the policy accepts the
     * element a non-negative integer is returned. This integer is reference to the
     * element and must be used when referencing the element. A negative return value
     * indicates that the policy has rejected the entry. Any replacement policy is allowed
     * to reject entries. For example, LRU-Size which rejects entries over a certain size.
     * Also takes an {@link AttributeMap} as parameter which can contain various
     * properties for the added element.
     * 
     * @param element
     *            the element to add to the replacement policy
     * @param attributes
     *            map of attributes for the specified element
     * @return a positive index that can be used to reference the element in the
     *         replacement policy. A negative number is returned if the element is not
     *         accepted into the replacement policy
     */
    int add(T element, AttributeMap attributes);

    /**
     * Removes all elements from this policy.
     */
    void clear();

    /**
     * Returns the element that should be evicted next according to the specific
     * replacement policy.
     * 
     * @return the element that should be evicted or <code>null</code> if the policy
     *         does not contain any elements
     */
    T evictNext();

    /**
     * @return the current number of elements contained within this policy.
     */
    int getSize();

    /**
     * Returns the next element that will be evicted or <code>null</code> if no elements
     * are being held by the policy.
     * <p>
     * If the policy makes any guarantees as to what order its elements are evicted, this
     * method <tt>must</tt> return the elements in the same order. However, non
     * deterministic policies may return elements in any order from this method.
     * <p>
     * NOTE: This might be an expensive operation. For complicated policies where we
     * lazyly calculate which element should be evicted next. A complete copy of the
     * policys internal datastructures might need to be created.
     * 
     * @return the next element that should be evicted
     */
    T peek();

    /**
     * Return all elements present in the cache in the order with the first element that
     * will be evicted next.
     * <p>
     * If the policy makes any guarantees as to what order its elements are evicted, this
     * method must return the elements in the same order. For example, non deterministic
     * policies might evict entries in another order then specified by this method.
     * <p>
     * NOTE: This can be a very expensive operation. For implementations where we lazily
     * calculate which element should be evicted next. An implementation of this method
     * might need to create a full copy of all internal data structures.
     * 
     * @return a list containing all the elements in the order they will be evicted.
     */
    List<T> peekAll();

    /**
     * Removes the element with the specified reference index.
     * 
     * @param index
     *            the index of the element
     * @return the element that was removed
     */
    T remove(int index);

    /**
     * Used for notifying the replacement policy that the element with specified index has
     * been referenced.
     * 
     * @param index
     *            the index of the element
     */
    void touch(int index);

    /**
     * The entry with the specified index was updated with a new value. This new element
     * should be referenced instead of the previous element.
     * <p>
     * IMPORTANT: The previous element must be removed by the replacement policy even if
     * the new element cannot be accepted.
     * <p>
     * 
     * @param index
     *            the index of the previous element
     * @param newElement
     *            the new element that should replace the previous element
     * @param attributes
     *            map of attributes for the new element
     * @return <tt>true</tt> if the policy accepted the new element, otherwise
     *         <tt>false</tt>
     */
    boolean update(int index, T newElement, AttributeMap attributes);
}
