package org.coconut.cache.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.coconut.annotation.ThreadSafe;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheException;

/**
 * This class gives access to a cache or cache manager as a singleton.
 * <p>
 * It is the authors belief that the singleton pattern is usually a poor choice
 * for maintaining a single instance of a component. See, for example,
 * http://www.cabochon.com/~stevey/blog-rants/singleton-stupid.html for an
 * discussion. However, we also realize that in certain situations the pattern
 * has its merits.
 * <p>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@ThreadSafe(true)
public final class CacheSingleton {
    public final static String DEFAULT_CACHE_RESSOURCE = "coconut-cache.xml";

    private final static Lock lock = new ReentrantLock();

    /** The single cache instance. */
    private static volatile Cache cacheInstance;

    // public static void initializeCache(String file) {
    // try {
    // initializeCache(new FileInputStream(file));
    // } catch (FileNotFoundException ffe) {
    // throw new CacheException("Configuration file " + file
    // + " could not be located", ffe);
    // }
    // }
    //
    // public static void initializeCache(InputStream is) {
    // try {
    // cacheInstance = CacheConfiguration.loadCache(is);
    // } catch (Exception e) {
    // throw new CacheException("Cache could not be instanciated", e);
    // }
    // }

    /**
     * @param cache
     */
    public static void setCacheInstance(Cache<?, ?> cache) {
        lock.lock(); // lock not strictly needed
        try {
            cacheInstance = cache;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Cache<K, V> getCacheInstance() {
        Cache c = cacheInstance;
        if (c != null) {
            return c;
        }
        lock.lock();
        try {
            InputStream is = null;
            try {
                is = Thread.currentThread().getContextClassLoader().getResource(
                        DEFAULT_CACHE_RESSOURCE).openStream();
            } catch (IOException ioe) {
                throw new CacheException("Cache could not be instantiated", ioe);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // ignore allready on error path
                    }
                }
            }
            return cacheInstance;
        } finally {
            lock.unlock();
        }
    }

    // public static <K, V> CacheManager<K, V> singletonCacheManager() {
    //        
    // }
    //
    // public static <K, V> CacheTree<K, V> singletonCacheTree() {
    //        
    // }
}
