/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.exceptionhandling;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.debug.InternalDebugService;
import org.coconut.cache.internal.service.spi.Resources;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
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

    volatile Throwable startupException;

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
        logger = configuration.getExceptionLogger();
        if (logger == null) {
            logger = conf.getDefaultLogger();
        }
        debugLogger = conf.getDefaultLogger() == null ? Loggers.NULL_LOGGER : conf
                .getDefaultLogger();
        if (configuration.getExceptionHandler() != null) {
            this.exceptionHandler = configuration.getExceptionHandler();
        } else {
            this.exceptionHandler = new CacheExceptionHandler();
        }
    }

    public void checkStartupException() {
        Throwable re = startupException;
        if (re != null) {
            throw new IllegalStateException("Cache failed to start previously", re);
        }
    }

    public CacheExceptionContext<K, V> createContext(String message) {
        return createContext(null, message);
    }

    /** {@inheritDoc} */
    public CacheExceptionContext<K, V> createContext(final Throwable cause, final String message) {
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

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    public void debug(String str) {
        debugLogger.debug(str);
    }

    /** {@inheritDoc} */
    public void fatalRuntimeException(String msg) {
        getLogger().error(msg);
    }

    /** {@inheritDoc} */
    public void fatalRuntimeException(String msg, RuntimeException cause) {
        getLogger().error(msg, cause);
    }

    /** {@inheritDoc} */
    public void initializationFailed(CacheConfiguration<K, V> configuration,
            CacheLifecycle service, RuntimeException cause) {
        Logger logger = this.logger;
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

    public void initialize(CacheConfiguration<K, V> conf) {
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
                "Could not load value [key = " + key + ", attributes = " + attributes + "]"),
                loader, key, attributes);
    }

    public void serviceManagerShutdownFailed(Throwable cause, CacheLifecycle lifecycle) {
        getLogger();
        logger.fatal("Failed to shutdown service [name = " + cache.getName() + ", type = "
                + cache.getClass() + ", service = " + lifecycle + " ]", cause);
    }

    public void startFailed(Throwable cause, CacheConfiguration<K, V> configuration, Object service) {
        startupException = cause;
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        Logger logger = this.logger;
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
        exceptionHandler.handleWarning(createContext(warning));
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
}
