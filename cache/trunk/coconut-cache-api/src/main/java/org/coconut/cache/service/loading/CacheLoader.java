/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;

import org.coconut.attribute.AttributeMap;

/**
 * A <code>CacheLoader</code> is used for transparent loading or creation of values by a
 * cache. Instead of the user first checking if a value for a given key exist in the cache
 * and if that is not the case; create the value and add it to the cache. A cache
 * implementation can use a cache loader for lazily creating values for missing entries
 * making this process transparent for the user. A cache loader is also sometimes referred
 * to as a cache backend.
 * <p>
 * Usage example, a loader that for each key (String) loads the file for that given path.
 *
 * <pre>
 * class FileLoader implements CacheLoader&lt;String, byte[]&gt; {
 *     public byte[] load(String s, AttributeMap attributes) throws IOException {
 *         File f = new File(s);
 *         byte[] bytes = new byte[(int) f.length()];
 *         (new RandomAccessFile(f, &quot;r&quot;)).read(bytes);
 *         return bytes;
 *     }
 * }
 * </pre>
 *
 * When a user requests a value for a particular key that is not present in the cache. The
 * cache will automatically call the supplied cache loader to fetch the value. The cache
 * will then insert the key-value pair into the cache and return the value to the user.
 * The cache loader might also be used to fetch an updated value when an entry is no
 * longer valid according to some policy.
 * <p>
 * Another usage of a cache loader is for lazily creating new values. For example, a cache
 * that caches <code>Patterns</code>.
 *
 * <pre>
 * class PatternLoader implements CacheLoader&lt;String, Pattern&gt; {
 *     public Pattern load(String s, AttributeMap attributes) throws IOException {
 *         return Pattern.compile(s);
 *     }
 * }
 * </pre>
 *
 * <p>
 * The load method also provides an attribute map. This map can be used to provide
 * meta-data information to the caller of the {@link #load(Object, AttributeMap)} method.
 * For example, the following cache loader, which retrieves an URL as String. Defines the
 * cost of the element as the number of milliseconds it takes to retrieve the value.
 *
 * <pre>
 * public static class UrlLoader implements CacheLoader&lt;URL, String&gt; {
 *     public String load(URL url, AttributeMap attributes) throws Exception {
 *         long start = System.currentTimeMillis();
 *         BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
 *         StringBuilder sb = new StringBuilder();
 *         int str;
 *         while ((str = in.read()) != -1) {
 *             sb.append((char) str);
 *         }
 *         in.close();
 *         CacheAttributes.setCost(attributes, System.currentTimeMillis() - start);
 *         return sb.toString();
 *     }
 * }
 * </pre>
 *
 * TODO write something about attribute map.
 * <p>
 * Cache loader instances <tt>MUST</tt> be thread-safe. Allowing multiple threads to
 * simultaneous create or load new values.
 * <p>
 * Any <tt>cache</tt> implementation that makes use of cache loaders should, but is not
 * required to, make sure that if two threads are simultaneous requesting a value for the
 * same key. Only one of them do the actual loading.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
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
     * @param attributes
     *            a map of attributes that can the loader can inspect for attributes
     *            needed while loading or it can be updated by the cache loader with
     *            additional attributes
     * @return the value to which this key is mapped, or null if no such mapping exist.
     * @throws Exception
     *             An exception occured while loading or creating the value
     */
    V load(K key, AttributeMap attributes) throws Exception;

    /**
     * Loads multiple values. This might for be usefull for performance reasons, for
     * example, if the values are to be retrieved from a remote host.
     *
     * @param loadCallbacks
     *            a collection of CacheLoaderCallback with details about the elements that
     *            needs loading
     */
    void loadAll(Collection<? extends LoaderCallback<? extends K, ? super V>> loadCallbacks);

    /**
     * A callback that is used to asynchronously load values.
     *
     * @param <K>
     *            the type of keys used for loading values
     * @param <V>
     *            the type of values returned when loading
     */
    public static interface LoaderCallback<K, V> {

        /**
         * This method is invoked if the asynchronous computation completed succesfully.
         *
         * @param result
         *            the result of the computation
         */
        void completed(V result);

        /**
         * This method is invoked if the asynchronous computation completed with failures.
         *
         * @param cause
         *            the cause of the failure
         */
        void failed(Throwable cause);

        /**
         * Returns <tt>true</tt> if the loading has completed. Completion may be due to
         * normal termination or an exception -- in both these cases, this method will
         * return <tt>true</tt>.
         *
         * @return <tt>true</tt> if this task completed
         */
        boolean isDone();

        /**
         * Returns the key for which a corresponding value should be loaded.
         *
         * @return the key for which a corresponding value should be loaded
         */
        K getKey();

        /**
         * Returns a map of attributes which can be used when loading the entry.
         *
         * @return a map of attributes which can be used when loading the entry
         */
        AttributeMap getAttributes();
    }
}
