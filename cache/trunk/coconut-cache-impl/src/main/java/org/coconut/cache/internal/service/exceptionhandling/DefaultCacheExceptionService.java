package org.coconut.cache.internal.service.exceptionhandling;

import java.text.MessageFormat;
import java.util.logging.Level;

import org.coconut.cache.Cache;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.spi.CacheSPI;
import org.coconut.core.Logger;
import org.coconut.core.Loggers;

public class DefaultCacheExceptionService<K, V> implements CacheExceptionService<K, V>{

    private final Cache<K, V> cache;

    private volatile Logger logger;

    private final CacheExceptionHandler<K, V> exceptionHandler;

    public DefaultCacheExceptionService(Cache<K, V> cache,
            CacheExceptionHandlingConfiguration<K, V> configuration) {
        this.cache = cache;
        this.logger = configuration.getExceptionLogger();
        this.exceptionHandler = configuration.getExceptionHandler();
    }

    private Logger getLogger() {
        Logger l = logger;
        if (l != null) {
            return l;
        }
        return initializeLogger();
    }

    public CacheExceptionHandler<K, V> getExceptionHandler() {
        //TODO we really should wrap it in something that catches all runtime exceptions
        //thrown by the handler methods.
        return exceptionHandler;
    }

    private synchronized Logger initializeLogger() {
        if (logger == null) {
            String name = cache.getName();
            String loggerName = Cache.class.getPackage().getName() + "." + name;
            java.util.logging.Logger l = java.util.logging.Logger.getLogger(loggerName);
            String infoMsg = CacheSPI.lookup(CacheExceptionHandler.class, "noLogger");
            Logger logger = Loggers.JDK.from(l);
            l.setLevel(Level.ALL);
            logger.info(MessageFormat.format(infoMsg, name, loggerName));
            l.setLevel(Level.SEVERE);
            return logger;
        }
        return logger;
    }

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
}
