/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.loading;

import org.coconut.core.AttributeMap;

/**
 * A <code>CacheLoader</code> is used for transparant loading or creation of
 * values by a cache. Instead of the user first checking if a value for a given
 * key exist in the cache and if that is not the case; create the value and add
 * it to the cache. A cache implementation can use a cache loader for lazily
 * creating values for missing entries making this process transparant for the
 * user. A cache loader is also sometimes referred to as a cache backend.
 * Another type of a cache backend is the cache store
 * {@link org.coconut.cache.store.CacheStore} which allows persisting values in
 * addition to retrieving them.
 * <p>
 * Usage example, a loader that for each key (String) loads the file for that
 * given path.
 * 
 * <pre>
 * class FileLoader implements CacheLoader {
 *     public byte[] load(String s) throws IOException {
 *         File f = new File(s);
 *         byte[] bytes = new byte[(int) f.length()];
 *         (new RandomAccessFile(f, &quot;r&quot;)).read(bytes);
 *         return bytes;
 *     }
 * }
 * </pre>
 * 
 * When a user requests a value for a particular key that is not present in the
 * cache. The cache will automatically call the supplied cache loader to fetch
 * the value. The cache will then insert the key-value pair into the cache and
 * return the value. The cache loader might also be used to fetch an updated
 * value when an entry has expired.
 * <p>
 * Another usage of a cache loader is for lazily creating new values. For
 * example, a cache that caches <code>Patterns</code>.
 * 
 * <pre>
 * class PatternLoader implements CacheLoader&lt;String, Pattern&gt; {
 *     public Pattern load(String s) throws IOException {
 *         return Pattern.compile(s);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * Cache loader instances <tt>MUST</tt> be thread-safe. Allowing multiple
 * threads to simultaneous create or load new values.
 * <p>
 * Any <tt>cache</tt> implementation that makes use of cache loaders should,
 * for performance reasons, make sure that if two threads are simultaneous
 * requesting a value for the same key. Only one of them do the actual loading.
 * <p>
 * Only the following methods on a Cache instance will trigger the cache loader:
 * {@link Cache#get(Object)}, {@link Cache#getAll(Collection)},
 * {@link Cache#load(Object)}, {@link Cache#loadAll(Collection)}.
 * <p>
 * By overriding {@link org.coconut.cache.util.AbstractCacheLoader} only the
 * {@link CacheLoader#load(Object) load} method needs to be implemented. Some
 * cache implementations might use this information for optimization.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 * @see org.coconut.cache.util.AbstractCacheLoader
 * @see org.coconut.cache.store.CacheStore
 * @param <K>
 *            the type of keys used for loading values
 * @param <V>
 *            the type of values that are loaded
 */
public interface CacheLoader<K, V> {

    /**
     * Loads a single value.
     * 
     * @param key
     *            the key whose associated value is to be returned.
     * @param attributes a map of attributes that can be updated by the cache
     *        loader with additional attributes
     * @return the value to which this key is mapped, or null if no such mapping
     *         exist.
     * @throws Exception
     *             An exception occured while loading or creating the value
     */
    V load(K key, AttributeMap attributes) throws Exception; 
}
