/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

/**
 * A <code>ValueLoader</code> is used for transparant loading or creation of
 * values by a cache. Instead of the user first checking if a value for a given
 * key exist in the cache and if that is not the case; create the value and add
 * it to the cache. A cache implementation can use a value loader for lazily
 * creating values for missing entries making this process transparent for the
 * user.
 * <p>
 * Usage example, a loader that for each key (String) loads the file for that
 * given path.
 * 
 * <pre>
 * class FileLoader implements ValueLoader {
 *     public byte[] load(String s) {
 *         File f = new File(s);
 *         byte[] bytes = new byte[(int) f.length()];
 *         (new RandomAccessFile(f, &quot;r&quot;)).read(bytes);
 *         return bytes;
 *     }
 * }
 * </pre>
 * 
 * When a user requests a value for a particular key that is not present in the
 * cache. The cache will automatically call the supplied value loader to fetch
 * the value. The cache will then insert the key-value pair into the cache and
 * return the value.
 * <p>
 * Another usage of a value loader is for lazily creating new values. For
 * example, a cache that caches <code>Patterns</code>.
 * 
 * <pre>
 * class PatternLoader implements ValueLoader&lt;String, Pattern&gt; {
 *     public Pattern load(String s) {
 *         return Pattern.compile(s);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * ValueLoader instances <tt>MUST</tt> be thread-safe. Allowing multiple
 * threads to simultaneous create or load new values.
 * <p>
 * Any <tt>cache</tt> implementation that makes use of value loaders should,
 * for performance reasons, make sure that if two threads are simultaneous
 * requesting a value for the same key. Only one of them do the actual loading.
 * <p>
 * Only the two following methods on a Cache instance will trigger the cache
 * loader: {@link PocketCache#get(Object)}, and
 * {@link PocketCache#getAll(Collection)}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys used for loading values
 * @param <V>
 *            the type of values that are loaded
 */
public interface ValueLoader<K, V> {

    /**
     * Loads a single value.
     * 
     * @param key
     *            the key whose associated value is to be returned.
     * @return the value to which the specified should be mapped, or <tt>null</tt> if the
     *         corresponding value could not be found.
     */
    V load(K key); /* Throws Exception??????????? */
}
