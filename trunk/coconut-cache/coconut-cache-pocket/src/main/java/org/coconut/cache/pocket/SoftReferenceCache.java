/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.pocket;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SoftReferenceCache<K, V> extends UnsafePocketCache<K, V> {

    private final Map<K, SoftReference<V>> softReferences = new HashMap<K, SoftReference<V>>();

    /**
     * Returns the table entry that should be used for key with given hash
     * 
     * @param hash
     *            the hash code for the key
     * @return the table entry
     */
    static int indexFor(int hash, int tableLength) {
        return hash & (tableLength - 1);
    }

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
        super(loader);
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
        super(loader, capacity);
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
    public SoftReferenceCache(ValueLoader<K, V> loader, int capacity, float loadFactor) {
        super(loader, capacity, loadFactor);
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
        return ref == null ? super.loadValue(k) : ref.get();
    }
}
