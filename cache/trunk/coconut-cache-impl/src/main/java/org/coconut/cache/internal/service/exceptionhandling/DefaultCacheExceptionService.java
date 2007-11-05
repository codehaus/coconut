/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.exceptionhandling;

import java.text.MessageFormat;
import java.util.logging.Level;

import org.coconut.cache.Cache;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.spi.CacheSPI;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;

/**
 * The default implementation of the {@link CacheExceptionService}.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
public class DefaultCacheExceptionService<K, V> implements CacheExceptionService<K, V> {

    /** The cache for which this DefaultCacheExceptionService is registered. */
    private final Cache<K, V> cache;

    /** The CacheExceptionHandler configured for this cache. */
    private final CacheExceptionHandler<K, V> exceptionHandler;

    /** The logger to log exceptions to. */
    private volatile Logger logger;

    /**
     * Creates a new DefaultCacheExceptionService.
     * 
     * @param cache
     *            the cache that is using this service
     * @param configuration
     *            the configuration of CacheExceptionService
     */
    public DefaultCacheExceptionService(Cache<K, V> cache,
            CacheExceptionHandlingConfiguration<K, V> configuration) {
        this.cache = cache;
        this.logger = configuration.getExceptionLogger();
        if (configuration.getExceptionHandler() != null) {
            this.exceptionHandler = configuration.getExceptionHandler();
        } else {
            this.exceptionHandler = new CacheExceptionHandlers.DefaultLoggingExceptionHandler<K, V>();
        }
    }

    /** {@inheritDoc} */
    public CacheExceptionContext<K, V> createContext() {
        return new CacheExceptionContext<K, V>() {

            @Override
            public Logger defaultLogger() {
                return getLogger();
            }

            @Override
            public Cache<K, V> getCache() {
                return cache;
            }
        };
    }

    /** {@inheritDoc} */
    public CacheExceptionHandler<K, V> getExceptionHandler() {
        // TODO we really should wrap it in something that catches all runtime exceptions
        // thrown by the handler methods.
        return exceptionHandler;
    }

    /**
     * Returns the exception logger configured for this cache. Or initializes the default
     * logger if no logger has been defined and the default logger has not already been
     * initialized
     * 
     * @return the exception logger
     */
    private Logger getLogger() {
        Logger l = logger;
        if (l != null) {
            return l;
        }
        synchronized (this) {
            if (logger == null) {
                String name = cache.getName();
                String loggerName = Cache.class.getPackage().getName() + "." + name;
                java.util.logging.Logger jucLogger = java.util.logging.Logger.getLogger(loggerName);
                String infoMsg = CacheSPI.lookup(CacheExceptionHandler.class, "noLogger");
                jucLogger.setLevel(Level.ALL);
                jucLogger.info(MessageFormat.format(infoMsg, name, loggerName));
                jucLogger.setLevel(Level.SEVERE);
                logger = Loggers.JDK.from(jucLogger);
            }
        }
        return logger;
    }
}
