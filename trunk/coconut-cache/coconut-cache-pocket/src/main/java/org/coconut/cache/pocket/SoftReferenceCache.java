/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.pocket;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * We do not keep softreferences to keys.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access a SoftReferenceCache concurrently, and at least one
 * of the threads modifies the map structurally, it <i>must</i> be synchronized
 * externally. (A structural modification is any operation that adds or deletes
 * one or more mappings; merely changing the value associated with a key that an
 * instance already contains is not a structural modification.) This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the map.
 * <p>
 * If no such object exists, the map should be "wrapped" using the
 * {@link PocketCaches#synchronizedCache(PocketCache)} method. This is best done
 * at creation time, to prevent accidental unsynchronized access to the map:
 * 
 * <pre>
 *                     PocketCache m = PocketCaches.synchronizedCache(new SoftReferenceCache(...));
 * </pre>
 * 
 * <p>
 * The iterators returned by all of this class's "collection view methods" are
 * <i>fail-fast</i>: if the map is structurally modified at any time after the
 * iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}. Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed as it
 * is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification. Fail-fast iterators throw
 * <tt>ConcurrentModificationException</tt> on a best-effort basis. Therefore,
 * it would be wrong to write a program that depended on this exception for its
 * correctness: <i>the fail-fast behavior of iterators should be used only to
 * detect bugs.</i>
 * 
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @see Object#hashCode()
 * @see Collection
 * @see Map
 * @see PocketCache
 */
public class SoftReferenceCache<K, V> extends UnsafePocketCache<K, V> {

    private final SoftReferenceMap softReferences = new SoftReferenceMap();

    private final int capacity;

    /**
     * Constructs an empty <tt>SoftReferenceCache</tt> with the specified
     * loader, the default capacity (1000), and the default load factor (0.75).
     * 
     * @param loader
     *            the loader used for lazily construction missing values.
     * @throws NullPointerException
     *             if the specified loader is null
     */
    public SoftReferenceCache(ValueLoader<K, V> loader) {
        this(loader, DEFAULT_CAPACITY);
    }

    /**
     * Constructs an empty <tt>SoftReferenceCache</tt> with the specified
     * loader, capacity, and the default load factor (0.75).
     * 
     * @param loader
     *            the loader used for lazily construction missing values.
     * @param capacity
     *            the maximum number of entries allowed in the cache
     * @throws NullPointerException
     *             if the specified loader is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the capacity is nonpositive
     */
    public SoftReferenceCache(ValueLoader<K, V> loader, int capacity) {
        this(loader, capacity, capacity);
    }

    /**
     * Constructs an empty <tt>SoftReferenceCache</tt> with the specified
     * loader, capacity, and load factor.
     * 
     * @param loader
     *            the loader used for lazily construction missing values.
     * @param capacity
     *            the maximum number of entries allowed in the cache
     * @param softReferenceCapacity
     *            the number of softreferences we should keep.
     * @throws IllegalArgumentException
     *             if the capacity or the load factor is nonpositive
     * @throws NullPointerException
     *             if the specified loader is <tt>null</tt>
     */
    public SoftReferenceCache(ValueLoader<K, V> loader, int capacity,
            int softReferenceCapacity) {
        this(loader, capacity, softReferenceCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>SoftReferenceCache</tt> with the specified
     * loader, capacity, and load factor.
     * 
     * @param loader
     *            the loader used for lazily construction missing values.
     * @param capacity
     *            the maximum number of entries allowed in the cache
     * @param loadFactor
     *            the load factor threshold, used to control resizing. Resizing
     *            may be performed when the average number of elements per bin
     *            exceeds this threshold.
     * @throws IllegalArgumentException
     *             if the capacity or the load factor is nonpositive
     * @throws NullPointerException
     *             if the specified loader is <tt>null</tt>
     */
    public SoftReferenceCache(ValueLoader<K, V> loader, int capacity,
            int softReferenceCapacity, float loadFactor) {
        super(loader, capacity, DEFAULT_LOAD_FACTOR);
        this.capacity = softReferenceCapacity;
    }

    /**
     * @see org.coconut.cache.pocket.UnsafePocketCache#evicted(java.util.Map.Entry)
     */
    @Override
    void evicted(java.util.Map.Entry<K, V> entry) {
        super.evicted(entry);
        softReferences.put(entry.getKey(), new SoftReference<V>(entry.getValue()));
    }

    /**
     * @see org.coconut.cache.pocket.UnsafePocketCache#clear()
     */
    @Override
    public void clear() {
        softReferences.clear();
        super.clear();
    }

    /**
     * @see org.coconut.cache.pocket.UnsafePocketCache#loadValue(java.lang.Object)
     */
    @Override
    protected V loadValue(K k) {
        final SoftReference<V> ref = softReferences.get(k);
        if (ref == null) {
            softReferences.remove(k);
            return super.loadValue(k);
        } else {
            return ref.get();
        }
    }

    class SoftReferenceMap extends LinkedHashMap<K, SoftReference<V>> {
        /** serialVersionUID */
        private static final long serialVersionUID = 8408194972744164863L;

        /**
         * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
         */
        @Override
        protected boolean removeEldestEntry(
                java.util.Map.Entry<K, SoftReference<V>> eldest) {
            return size() > capacity;
        }

    }

    /**
     * @see org.coconut.cache.pocket.UnsafePocketCache#evict()
     */
    @Override
    public void evict() {
        for (Iterator<Map.Entry<K, SoftReference<V>>> iter = softReferences.entrySet()
                .iterator(); iter.hasNext();) {
            if (iter.next().getValue().get() == null) {
                iter.remove();
            }
        }
        super.evict();
    }
}
