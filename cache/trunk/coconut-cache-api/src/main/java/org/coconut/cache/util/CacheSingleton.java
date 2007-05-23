/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;

/**
 * This class gives access to a cache or cache manager as a singleton.
 * <p>
 * It is the authors belief that the singleton pattern is usually a poor choice
 * for maintaining a single instance of a component. See, for example,
 * http://www-128.ibm.com/developerworks/library/co-single.html for an
 * discussion. However, we also realize that in certain situations the pattern
 * has its merits.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@ThreadSafe
public final class CacheSingleton {
	/**
     * The <tt>default</tt> location of the cache configuration file which is
     * used if no cache has been set programmatically using
     * {@link #setSingleCache(Cache)}, {@link #addCache(AbstractCache)} or
     * {@link #addCache(Cache, String)}.
     */
	public final static String DEFAULT_CACHE_RESSOURCE = "coconut-cache.xml";

	/**
     * The location of the cache configuration file which is used if the caches
     * has not been set programmatically using {@link #setSingleCache(Cache)}.
     */
	private static String cache_ressource_location = DEFAULT_CACHE_RESSOURCE;

	/** The single cache instance. */
	private static volatile Cache cacheInstance;

	/** Whether or not this singleton has been initialized. */
	private static boolean isInitialized;

	/** Cannot instantiate. */
	private CacheSingleton() {}

	/**
     * @return the cACHE_RESSOURCE_LOCATION
     */
	public static synchronized String getCacheRessourceLocation() {
		return cache_ressource_location;
	}

	/**
     * Returns the cache maintained by this singleton. If no cache has been set
     * using {@link #setCache(Cache)} this method will attempt to load one from
     * the classpath location configured in
     * {@link #setCacheRessourceLocation(String)}. If no location has been set
     * this method will attempt to load '{@value #DEFAULT_CACHE_RESSOURCE}'
     * from the classpath.
     * 
     * @return the singleton cache
     * @throws CacheException
     *             if the cache could not be properly instantiated.
     * @param <K>
     *            the type of keys maintained by this cache
     * @param <V>
     *            the type of mapped values
     */
	@SuppressWarnings("unchecked")
	public static <K, V> Cache<K, V> getCache() {
		Cache c = cacheInstance;
		if (c != null) {
			return c;
		}
		lazyInitializeClasspathConfiguration();
		return cacheInstance;
	}

	/**
     * Sets the location of the configuration that should be used to configure
     * the cache.
     * 
     * @param location
     *            the location of the configuration
     */
	public static synchronized void setCacheRessourceLocation(String location) {
		cache_ressource_location = location;
	}

	/**
     * Sets the single cache used. The cache instance can be retrieved, possible
     * by another thread, by callong {@link #getSingleCache()}.
     * 
     * @param cache
     *            the cache to keep a singleton reference for
     */
	public synchronized static void setCache(Cache<?, ?> cache) {
		if (cache == null) {
			throw new NullPointerException("cache is null");
		}
		cacheInstance = cache;
		isInitialized = true;
	}

	/**
     * Tries to load the configuration from the classpath.
     */
	static synchronized void lazyInitializeClasspathConfiguration() {
		if (!isInitialized) {
			InputStream is = null;
			try {
				URL url = Thread.currentThread().getContextClassLoader().getResource(
						cache_ressource_location);
				if (url == null) {
					throw new CacheException("Could not find configuration '"
							+ cache_ressource_location + "' on the classpath.");
				}
				is = url.openStream();
				Cache cache = CacheConfiguration.createCache(is);
				setCache(cache);
				isInitialized = true;
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						throw new CacheException("InputStream could not be closed", e);
					}
				}

			} catch (Exception e) {
				throw new CacheException("Cache could not be instantiated", e);
			}
		}
	}
}
