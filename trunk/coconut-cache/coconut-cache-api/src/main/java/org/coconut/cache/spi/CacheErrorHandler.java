/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
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
import org.coconut.cache.CacheLoader;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheErrorHandler<K, V> {

    private boolean isInitialized;

    private Log logger;

    private volatile String name;

    public CacheErrorHandler() {

    }

    /**
     * @param default_logger2
     */
    public CacheErrorHandler(Log logger) {
        synchronized (this) {
            isInitialized = true;
        }
        this.logger = logger;
    }

    public synchronized boolean hasLogger() {
        return logger != null;
    }

    public Log getLogger() {
        checkInitialized();
        return logger;
    }

    public Map<K, CacheEntry<K, V>> loadAllEntrisFailed(
            final CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            Collection<? extends K> keys, boolean isAsync, Throwable cause) {
        String msg = "Failed to load values [keys = " + keys.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public Map<K, V> loadAllFailed(CacheLoader<? super K, ? extends V> loader,
            Collection<? extends K> keys, boolean isAsync, Throwable cause) {
        String msg = "Failed to load values [keys = " + keys.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public CacheEntry<K, V> loadEntryFailed(
            CacheLoader<? super K, ? extends CacheEntry<? super K, ? extends V>> loader,
            K key, boolean isAsync, Throwable cause) {
        String msg = "Failed to load value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public V loadFailed(CacheLoader<? super K, ? extends V> loader, K key,
            boolean isAsync, Throwable cause) {
        String msg = "Failed to load value [key = " + key.toString() + "]";
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public synchronized void setCacheName(String name) {
        this.name = name;
    }

    public final void warning(String warning) {
        getLogger().warn(warning);
    }

    public final void unhandledError(Throwable t) {
        getLogger().error("Unhandled Error", t);
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
