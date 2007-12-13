/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.exceptionhandling;

import java.util.logging.Level;
import java.util.logging.LogManager;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.debug.InternalDebugService;
import org.coconut.cache.internal.service.spi.Resources;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;

/**
 * The default implementation of the {@link InternalCacheExceptionService}.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class DefaultCacheExceptionService<K, V> implements InternalCacheExceptionService<K, V>,
        InternalDebugService {

    /** The cache for which exceptions should be handled. */
    private final Cache<K, V> cache;

    private final Logger debugLogger;

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
    public DefaultCacheExceptionService(Cache<K, V> cache, CacheConfiguration<K, V> conf,
            CacheExceptionHandlingConfiguration<K, V> configuration) {
        this.cache = cache;
        // TODO resort to default logger if no exceptionLogger is defined?
        if (configuration.getExceptionHandler() == null) {
            this.logger = conf.getDefaultLogger();
        } else {
            this.logger = configuration.getExceptionLogger();
        }
        debugLogger = conf.getDefaultLogger();
        if (configuration.getExceptionHandler() != null) {
            this.exceptionHandler = configuration.getExceptionHandler();
        } else {
            this.exceptionHandler = CacheExceptionHandlers.defaultLoggingExceptionHandler();
        }
    }

    /** {@inheritDoc} */
    public CacheExceptionContext<K, V> createContext(final Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause is null");
        }
        return new CacheExceptionContext<K, V>() {

            @Override
            public Logger defaultLogger() {
                return getLogger();
            }

            @Override
            public Cache<K, V> getCache() {
                return cache;
            }

            @Override
            public Throwable getCause() {
                return cause;
            }
        };
    }

    public void debug(String str) {
        if (debugLogger != null) {
            debugLogger.debug(str);
        }
    }

    /** {@inheritDoc} */
    public CacheExceptionHandler<K, V> getHandler() {
        // TODO we really should wrap it in something that catches all runtime exceptions
        // thrown by the handler methods.
        return exceptionHandler;
    }

    public boolean isDebugEnabled() {
        return debugLogger != null && debugLogger.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return debugLogger != null && debugLogger.isTraceEnabled();
    }

    public String toString() {
        return "ExceptionHandling Service";
    }

    public void trace(String str) {
        if (debugLogger != null) {
            debugLogger.trace(str);
        }
    }

    /**
     * Returns the exception logger configured for this cache. Or initializes the default
     * logger if no logger has been defined and the default logger has not already been
     * initialized
     * 
     * @return the exception logger for the cache
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
                java.util.logging.Logger jucLogger = LogManager.getLogManager().getLogger(
                        loggerName);
                if (jucLogger == null) {
                    jucLogger = java.util.logging.Logger.getLogger(loggerName);
                    jucLogger.setLevel(Level.ALL);

                    String infoMsg = Resources.lookup(DefaultCacheExceptionService.class,
                            "noLogger", name, loggerName);
                    jucLogger.info(infoMsg);
                    jucLogger.setLevel(Level.WARNING);
                }
                logger = Loggers.JDK.from(jucLogger);
            }
        }
        return logger;
    }

    public Logger getExceptionLogger() {
        Logger l=logger;
        return l==null ? Loggers.NULL_LOGGER : l;
    }

    public CacheExceptionContext<K, V> createContext() {
        return createContext(new Exception());
    }
}
