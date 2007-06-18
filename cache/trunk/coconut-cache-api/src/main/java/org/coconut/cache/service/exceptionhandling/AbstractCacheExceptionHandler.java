/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import java.text.MessageFormat;
import java.util.logging.Level;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.spi.Resources;
import org.coconut.core.AttributeMap;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.coconut.event.EventSubscription;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheExceptionHandler<K, V> {
    private volatile Logger logger;

    private String name;

    public boolean eventDeliveryFailed(Cache<K, V> cache, CacheEvent<K, V> event,
            EventSubscription<CacheEvent<K, V>> destination, Throwable cause) {
        return false;
    }

    public final synchronized String getCacheName() {
        return name;
    }

    public final Logger getLogger() {
        Logger l = logger;
        if (l != null) {
            return l;
        }
        return initializeLogger();
    }

    public final boolean hasLogger() {
        return logger != null;
    }

    public V loadFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader, K key,
            AttributeMap map, boolean isGet, Throwable cause) {
        return null;
    }

    public final synchronized void setCacheName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (this.name != null) {
            throw new IllegalStateException("Cache name has already been set");
        }
        this.name = name;
    }

    public final synchronized void setLogger(Logger logger) {
        if (logger == null) {
            throw new NullPointerException("logger is null");
        }
        this.logger = logger;
    }

    public void unhandledRuntimeException(RuntimeException t) {}

    /**
     * @param warning
     */
    public void warning(String warning) {};

    private synchronized Logger initializeLogger() {
        if (logger == null) {
            String loggerName = Cache.class.getPackage().getName() + "." + name;
            java.util.logging.Logger l = java.util.logging.Logger.getLogger(loggerName);
            String infoMsg = Resources.lookup(AbstractCacheExceptionHandler.class,
                    "noLogger");
            Logger logger = Loggers.JDK.from(l);
            l.setLevel(Level.ALL);
            logger.info(MessageFormat.format(infoMsg, name, loggerName));
            l.setLevel(Level.SEVERE);
            return logger;
        }
        return logger;
    }
}
