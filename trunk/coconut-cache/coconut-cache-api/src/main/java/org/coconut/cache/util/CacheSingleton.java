/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.spi.AbstractCache;

/**
 * This class gives access to a cache or cache manager as a singleton.
 * <p>
 * It is the authors belief that the singleton pattern is usually a poor choice
 * for maintaining a single instance of a component. See, for example,
 * http://www-128.ibm.com/developerworks/library/co-single.html for an
 * discussion. However, we also realize that in certain situations the pattern
 * has its merits.
 * <p>
 * $Id$
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@ThreadSafe(true)
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

    /** A map of registered cache instances. */
    private final static ConcurrentHashMap<String, Cache<?, ?>> caches = new ConcurrentHashMap<String, Cache<?, ?>>();

    /** Whether or not this singleton has been initialized. */
    private static boolean isInitialized = false;

    public static void addCache(AbstractCache<?, ?> cache) {
        addCache(cache, cache.getName());
    }

    public static synchronized void addCache(Cache<?, ?> cache, String cacheName) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        } else if (cacheName == null) {
            throw new NullPointerException("cacheName is null");
        }
        caches.put(cacheName, cache);
        isInitialized = true;
    }

    public static <K, V> Cache<K, V> getCache(String name) {
        Cache<K, V> c = (Cache<K, V>) caches.get(name);
        if (c == null) {
            lazyInitializeClasspathConfiguration();
            c = (Cache<K, V>) caches.get(name);
            if (c == null) {
                throw new CacheException(
                        "A cache with the specified name does not exist, name = " + name
                                + " registered caches = " + caches.keySet().toString());
            }
        }
        return c;
    }

    /**
     * @return the cACHE_RESSOURCE_LOCATION
     */
    public static synchronized String getCacheRessourceLocation() {
        return cache_ressource_location;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Cache<K, V> getSingleCache() {
        Cache c = cacheInstance;
        if (c != null) {
            return c;
        }
        lazyInitializeClasspathConfiguration();
        return cacheInstance;
    }

    public static boolean hasCache(String name) {
        boolean contains = caches.containsKey(name);
        if (!contains) {
            lazyInitializeClasspathConfiguration();
            contains = caches.containsKey(name);
        }
        return contains;
    }

    /**
     * @param cache_ressource_location
     *            the cACHE_RESSOURCE_LOCATION to set
     */
    public static synchronized void setCacheRessourceLocation(String location) {
        cache_ressource_location = location;
    }

    /**
     * @param cache
     */
    public synchronized static void setSingleCache(Cache<?, ?> cache) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        }
        cacheInstance = cache;
        isInitialized = true;
        if (cache instanceof AbstractCache) {
            caches.put(((AbstractCache) cache).getName(), cache);
        }
    }

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
                setSingleCache(CacheConfiguration.createInstantiateAndStart(is));
            } catch (Exception e) {
                throw new CacheException("Cache could not be instantiated", e);
            } finally {
                isInitialized = true;
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // ignore allready on error path
                    }
                }
            }
        }
    }
}
