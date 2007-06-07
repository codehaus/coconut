/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy;

import java.util.List;

import org.coconut.core.AttributeMap;

/**
 * A (cache) replacement policy determines which data item(s) should be evicted (deleted)
 * from the cache when the free space is insufficient for accommodating an item to be
 * cached. Normally users should not need to implement this interface, only if they want
 * to implement new replacement polices. This library comes with a number of predefined
 * replacement policies, see {@link Polices} for the most commonly used policies.
 * <p>
 * For performance reasons cache policies are not expected to be thread-safe. Instead, any
 * cache implementation must maintain thread safety. A thread-safe replacement policy can
 * be obtained from an existing non thread-safe replacement policy by using
 * {@link Policies#synchronizedReplacementPolicy(ReplacementPolicy)}.
 * <p>
 * {@link org.coconut.cache.policy.concurrent} contains a number of specialized concurrent
 * replacement policies. Note: because of their highly concurrent nature they do not
 * implement this interface.
 * <p>
 * The <code>ReplacementPolicy</code> interface does not support policies which dependes
 * on arbitrary cost or arbitrary size. That is, only paging is supported by this
 * particular interface. Instead use the {@link CostSizePolicy} interface, which uses the
 * size and cost of refetching to determine which objects to evict.
 * {@link org.coconut.cache.policy.costsize} contains a number of policies that makes
 * effective decisions in selecting cache objects for eviction based on their size and the
 * cost of retrieving them again.
 * <p>
 * All general-purpose <tt>ReplacementPolicy</tt> implementation classes should provide
 * a a void (no arguments) constructor. There is no way to enforce this recommendation (as
 * interfaces cannot contain constructors) but all of the general-purpose replacement
 * policy implementations available in this library comply.
 * <p>
 * We use an <tt>int</tt> to represent pointer to entries because it has a lower memory
 * footprint then using objects. While this is no problem for a cache with one single
 * policy the overhead can be significant when using an adaptable caching strategy where
 * we monitor tens or hundreds of polices. Furthermore we observe that (as a general rule)
 * most entries that are evicted from a cache are the entries that have not been accessed
 * for the longest time. Hence these objects are most often in the tenure GC area.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface ReplacementPolicy<T> {

    /** The default cost of fetching an element if no cost is specified. */
    public static final double DEFAULT_COST = 1.0;

    public static final long DEFAULT_SIZE = 1;

    public static final long DEFAULT_HITS = 0;

    /**
     * Removes all elements from this policy.
     */
    void clear();

    /**
     * The entry with the specified index was updated with a new value. This new element
     * should be referenced instead of the previous element.
     * <p>
     * IMPORTANT: The previous element must be removed by the replacement policy no matter
     * what.
     * 
     * @param index
     *            the index of the previous element
     * @param newElement
     *            the new element that should replace the previous element
     * @return <tt>true</tt> if the policy accepted the new element, otherwise
     *         <tt>false</tt>
     */
    boolean update(int index, T newElement);

    boolean update(int index, T newElement, AttributeMap attributes);

    /**
     * Adds the specified element to the replacement policy. If the policy accepts the
     * element a positive integer is returned. This integer is reference to the element
     * and must be used when referencing the element. A negative return value indicates
     * that the policy has rejected the entry. Any replacement policy is allowed to reject
     * entries. For example, LRU-Size which rejects entries over a certain size.
     * <p>
     * TODO can return 0????
     * 
     * @param element
     *            the element to add to the replacement policy
     * @return a positive index that can be used to reference the element in the
     *         replacement policy. A negative number is returned if the element is not
     *         accepted into the replacement policy
     */
    int add(T element);

    int add(T element, AttributeMap attributes);

    /**
     * Used for notifying the replacement policy that the element with specified index has
     * been referenced.
     * 
     * @param index
     *            the index of the element
     */
    void touch(int index);

    /**
     * Returns the element that should be evicted next according to the specific
     * replacement policy.
     * 
     * @return the element that should be evicted or <code>null</code> if the policy
     *         does not contain any elements
     */
    T evictNext();

    /**
     * Removes the element with the specified reference index.
     * 
     * @param index
     *            the index of the element
     * @return the element that was removed
     */
    T remove(int index);

    /**
     * Return all elements present in the cache in the order with the first element that
     * will be evicted next.
     * <p>
     * If the policy makes any guarantees as to what order its elements are evicted, this
     * method must return the elements in the same order. For example, non deterministic
     * policies might evict entries in another order then specified by this method.
     * <p>
     * Be aware that this can be a very expensive operation. For implementations where we
     * lazily calculate which element should be evicted next. An implementation of this
     * method might need to create a full copy of all internal data structures.
     * 
     * @return a list containing all the elements in the order they will be evicted.
     */
    List<T> peekAll();

    /**
     * Returns the next element that will be evicted or <code>null</code> if no elements
     * are being held by the policy.
     * <p>
     * If the policy makes any guarantees as to what order its elements are evicted, this
     * method must return the elements in the same order. For example, non deterministic
     * policies might evict entries in another order then specified by this method.
     * <p>
     * Be aware that this can be a very expensive operation. For complicated policies
     * where we lazyly calculate which element should be evicted next. A complete copy of
     * the policys internal datastructures might need to be created.
     * 
     * @return the next element that should be evicted
     */
    T peek();

    /**
     * Returns the number of elements in the policy.
     */
    int getSize();
}
