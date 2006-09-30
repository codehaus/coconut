/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheException;
import org.coconut.cache.util.DefaultCacheEntry;
import org.coconut.core.Log;
import org.coconut.core.Logs;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheErrorHandler<K, V> {
    private Log logger;

    private boolean isInitialized;

    private volatile String name;

    public CacheErrorHandler() {

    }

    /**
     * @param default_logger2
     */
    public CacheErrorHandler(Log logger) {
        isInitialized = true;
        this.logger = logger;
    }

    public static final CacheErrorHandler DEFAULT = new CacheErrorHandler();

    public void setCacheName(String name) {
        this.name = name;
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    protected CacheEntry<K, V> loadingEntryOfValueFailed(K key, Throwable cause) {
        throw new CacheException("Failed to load value [key = " + key.toString() + "]",
                cause);
    }

    public V asyncLoadingFailed(K key, Throwable cause) {
        checkInitialized();
        String msg = "Failed to asynchronously load value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }
    public CacheEntry<K,V> asyncLoadingEntryFailed(K key, Throwable cause) {
        checkInitialized();
        String msg = "Failed to asynchronously load value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }
    public Map<K,V> asyncLoadingAllFailed(Collection<? extends K> keys, Throwable cause) {
        checkInitialized();
        String msg = "Failed to asynchronously load value [key = " + keys.size() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }
    
    protected V loadingOfValueFailed(K key, Throwable cause) {
        checkInitialized();
        String msg = "Failed to load value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    protected Map<K, V> backendLoadFailed(Collection<? extends K> keys, Throwable cause) {
        checkInitialized();
        String msg = "Failed to load values for collection of keys [keys.size = "
                + keys.size() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public CacheEntry<K, V> loadingOfCacheEntryFailed(K key, Throwable cause) {
        return new DefaultCacheEntry<K, V>(key, loadingOfValueFailed(key, cause));
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    public Map<K, V> loadingOfValuesFailed(Collection<? extends K> keys, Throwable cause) {
        throw new CacheException(
                "Failed to load values for collection of keys [keys.size = "
                        + keys.size() + "]", cause);
    }

    public Map<K, CacheEntry<K, V>> loadingOfCacheEntryFailed(
            Collection<? extends K> keys, Throwable cause) {
        throw new CacheException(
                "Failed to load values for collection of keys [keys.size = "
                        + keys.size() + "]", cause);
    }

    public void backendDeleteFailed(Collection<? extends K> keys, Throwable cause) {
        checkInitialized();
        String msg = "Failed to delete values for collection of keys [keys.size = "
                + keys.size() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    /**
     * @param key
     *            the key that failed to load
     * @param cause
     */
    public void backendStoreFailed(Map<K, V> map, Throwable cause) {
        checkInitialized();
        String msg = "Failed to store values for collection entries [size = "
                + map.size() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public Log getLogger() {
        checkInitialized();
        return logger;
    }

    protected synchronized void checkInitialized() {
        if (!isInitialized) {
            isInitialized = true;
            String loggerName = Cache.class.getPackage().getName() + "." + name;
            Logger l = Logger.getLogger(loggerName);
            String infoMsg = Ressources.getString("AbstractCache.default_logger");
            logger = Logs.JDK.from(l);
            logger.info(MessageFormat.format(infoMsg, name, loggerName));
            l.setLevel(Level.SEVERE);
            isInitialized = true;
        }
    }

}
