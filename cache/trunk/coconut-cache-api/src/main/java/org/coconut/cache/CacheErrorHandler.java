/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.spi.Resources;
import org.coconut.core.AttributeMap;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.event.EventSubscription;

/**
 * A CacheErrorHandler takes of all error handling within a Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheErrorHandler<K, V> {

    private boolean isInitialized;

    private Log logger;

    private volatile String name;

    /**
     * Creates a new CacheErrorHandler.
     */
    public CacheErrorHandler() {
    }

    /**
     * Creates a new CacheErrorHandler using the specified logger.
     */
    public CacheErrorHandler(Log logger) {
        setLogger(logger);
    }

    public synchronized Log getLogger() {
        checkInitialized();
        return logger;
    }

    public synchronized boolean hasLogger() {
        return logger != null;
    }

    public synchronized Map<K, V> loadAllFailed(
            final CacheLoader<? super K, ?> loader,
            Map<? extends K, AttributeMap> keysWithAttributes, boolean isAsynchronous,
            Throwable cause) {
        String msg = Resources.lookup(CacheErrorHandler.class, "loadAllFailed",
                keysWithAttributes.keySet().toString());
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public synchronized V loadFailed(CacheLoader<? super K, ?> loader,
            K key, AttributeMap map, boolean isAsync, Throwable cause) {
        String msg = Resources.lookup(CacheErrorHandler.class, "loadFailed", key
                .toString());
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public synchronized void eventDeliveryFailed(Cache<K, V> cache,
            CacheEvent<K, V> event, EventSubscription<CacheEvent<K, V>> destination,
            Throwable cause) {
        String msg = Resources.lookup(CacheErrorHandler.class, "eventFailed", event
                .toString());
        getLogger().error(msg, cause);
        throw new CacheException(msg, cause);
    }

    public synchronized void setCacheName(String name) {
        this.name = name;
    }

    public synchronized String getCacheName() {
        return name;
    }

    public synchronized void setLogger(Log logger) {
        if (logger == null) {
            throw new NullPointerException("logger is null");
        }
        this.logger = logger;
        isInitialized = true;
    }

    public synchronized final void unhandledRuntimeException(RuntimeException t) {
        getLogger().error("Unhandled RuntimeException", t);
    }

    public synchronized void warning(String warning) {
        getLogger().warn(warning);
    }

    protected void checkInitialized() {
        if (!isInitialized) {
            isInitialized = true;
            String loggerName = Cache.class.getPackage().getName() + "." + name;
            Logger l = Logger.getLogger(loggerName);
            String infoMsg = Resources.lookup(CacheErrorHandler.class, "noLogger");
            logger = Logs.JDK.from(l);
            l.setLevel(Level.ALL);
            logger.info(MessageFormat.format(infoMsg, name, loggerName));
            l.setLevel(Level.SEVERE);
            isInitialized = true;
        }
    }
}
