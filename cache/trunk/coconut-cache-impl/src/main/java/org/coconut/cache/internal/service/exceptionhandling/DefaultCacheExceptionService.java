/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.exceptionhandling;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.CacheInternals;
import org.coconut.cache.internal.service.debug.InternalDebugService;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.coconut.core.Logger.Level;
import org.coconut.internal.util.LazyLogger;

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

    private final Logger startupLogger;

    /** The logger to log exceptions to. */
    private final Logger logger;

    volatile Throwable startupException;

    /**
     * Creates a new DefaultCacheExceptionService.
     *
     * @param configuration
     *            the configuration of CacheExceptionService
     */
    public DefaultCacheExceptionService(Cache cache, String name, CacheConfiguration conf,
            CacheExceptionHandlingConfiguration<K, V> configuration) {
        this.cache = cache;
        Logger logger = configuration.getExceptionLogger();
        Logger defaultLogger = conf.getDefaultLogger();
        if (logger == null) {
            logger = defaultLogger;
        }
        startupLogger = logger;
        if (logger == null) {
            String loggerName = Cache.class.getPackage().getName() + "." + name;
            String infoMsg = CacheInternals.lookup(DefaultCacheExceptionService.class, "noLogger", name,
                    loggerName);
            logger = new LazyLogger(loggerName, infoMsg);
        }
        this.logger = logger;
        // set debug logger
        debugLogger = defaultLogger == null ? Loggers.NULL_LOGGER : defaultLogger;
        // Set cache exception handler
        CacheExceptionHandler<K, V> exceptionHandler = configuration.getExceptionHandler();
        this.exceptionHandler = exceptionHandler == null ? new CacheExceptionHandler()
                : exceptionHandler;
    }

    public void checkExceptions(boolean failIfShutdown) {
        Throwable re = startupException;
        if (re != null) {
            throw new IllegalStateException("Cache failed to start previously", re);
        }
    }

    public void debug(String str) {
        debugLogger.debug(str);
    }

    /** {@inheritDoc} */
    public void fatal(String msg) {
        exceptionHandler.apply(createContext(null, msg, Level.Fatal));
    }

    /** {@inheritDoc} */
    public void fatal(String msg, Throwable cause) {
        exceptionHandler.apply(createContext(cause, msg, Level.Fatal));
    }

    public void info(String str) {
        debugLogger.info(str);
    }

    /** {@inheritDoc} */
    public void initializationFailed(CacheConfiguration<K, V> configuration,
            CacheLifecycle service, RuntimeException cause) {
        Logger logger = this.startupLogger;
        if (logger != null) {
            logger.fatal("Failed to initialize cache [name = " + cache.getName() + ", type = "
                    + cache.getClass() + ", service = " + service + " ]", cause);
            logger
                    .debug("---------------------------------CacheConfiguration Start---------------------------------");
            logger.debug(configuration.toString());
            logger
                    .debug("---------------------------------CacheConfiguration Finish--------------------------------");
        }
    }

    public void initialize(Cache<K, V> cache, CacheConfiguration<K, V> conf) {
        exceptionHandler.initialize(conf);
    }

    /** {@inheritDoc} */
    public boolean isDebugEnabled() {
        return debugLogger.isDebugEnabled();
    }

    /** {@inheritDoc} */
    public boolean isTraceEnabled() {
        return debugLogger.isTraceEnabled();
    }

    public V loadFailed(Throwable cause, CacheLoader<? super K, ?> loader, K key,
            AttributeMap attributes) {
        return exceptionHandler.loadingLoadValueFailed(createContext(cause,
                "Could not load value [key = " + key + ", attributes = " + attributes + "]",
                fromException(cause)), loader, key, attributes);
    }

    private Level fromException(Throwable cause) {
        if (cause instanceof Exception && !(cause instanceof RuntimeException)) {
            return Level.Error;
        }
        return Level.Fatal;
    }

    public void serviceManagerShutdownFailed(Throwable cause, CacheLifecycle lifecycle) {
        logger.fatal("Failed to shutdown service [name = " + cache.getName() + ", type = "
                + cache.getClass() + ", service = " + lifecycle + " ]", cause);
    }

    public void startFailed(Throwable cause, CacheConfiguration<K, V> configuration, Object service) {
        startupException = cause;
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        Logger logger = this.startupLogger;
        if (logger != null) {
            logger.fatal("Failed to start cache [name = " + cache.getName() + ", type = "
                    + cache.getClass() + ", service = " + service + " ]", cause);
            logger
                    .debug("---------------------------------CacheConfiguration Start---------------------------------");
            logger.debug(configuration.toString());
            logger
                    .debug("---------------------------------CacheConfiguration Finish--------------------------------");

        }
        cache.shutdown();
        throw (RuntimeException) cause;
    }

    public boolean startupFailed() {
        return startupException != null;
    }

    public void terminated(Map<? extends CacheLifecycle, RuntimeException> terminationFailures) {
        exceptionHandler.terminated(terminationFailures);
    }

    /** {@inheritDoc} */
    public void trace(String str) {
        debugLogger.trace(str);
    }

    public void warning(String warning) {
        exceptionHandler.apply(createContext(null, warning, Level.Warn));
    }

    private CacheExceptionContext<K, V> createContext(final Throwable cause, final String message,
            final Level level) {
        return new CacheExceptionContext<K, V>() {

            @Override
            public Logger defaultLogger() {
                return logger;
            }

            @Override
            public Cache<K, V> getCache() {
                return cache;
            }

            @Override
            public Throwable getCause() {
                return cause;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public Level getLevel() {
                return level;
            }
        };
    }
}
