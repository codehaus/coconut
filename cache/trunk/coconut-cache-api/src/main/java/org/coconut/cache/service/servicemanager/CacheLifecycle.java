/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.concurrent.Callable;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheLifecycle {

    /**
     * Initializes the object. This is the first method in the cache lifecycle that is
     * called.
     * <p>
     * This method will be called from within the constructor of the cache. Any runtime
     * exception thrown from this method will be rethrown from the cache's constructor
     * 
     * @param initializer
     *            provides information about the configuration of the cache
     */
    void initialize(CacheLifecycle.Initializer initializer);

    /**
     * Starts the service. The specified serviceManager can be used to retrieve other
     * services.
     * 
     * @param serviceManager
     *            the caches service manager
     * @throws Exception
     *             the service failed to start properly
     */
    void start(CacheServiceManagerService serviceManager) throws Exception;

    /**
     * All services have been initialized correctly, and the cache is ready for use.
     * 
     * @param cache
     *            the cache that was started
     */
    void started(Cache<?, ?> cache);

    /**
     * The cache has been shutdown.
     */
    void shutdown(Shutdown shutdown) throws Exception;

    /**
     * The {@link Cache#shutdownNow()} method has been invoked. This method is always
     * invoked after {@link #shutdown()} if invoked at all.
     */
    void shutdownNow();

    /**
     * Method invoked when the cache has terminated. This method is invoked as the last
     * method in this lifecycle interface and is called when the cache and all of it
     * services has been succesfully shutdown. This method is also called if the cache
     * failed to initialize or start. But only if the service was succesfully initialized ({@link #initialize(Initializer)}
     * was run without failing).
     */
    void terminated();

    interface Shutdown {

        /**
         * @param callable
         * @throws IllegalStateException
         *             if this method has already been called, or if this method is called
         *             outside {@link CacheLifecycle#shutdown()}.
         * @throws UnsupportedOperationException
         *             if shutting down services asynchronously is not supported by the
         *             cache
         */
        void shutdownAsynchronously(Callable<?> callable);
    }

    /**
     * Provides information about the configuration of the cache. Used when initializing
     * services.
     */
    interface Initializer {
        /**
         * Returns the configuration used for creating this cache.
         * 
         * @return the configuration used for creating this cache
         */
        CacheConfiguration<?, ?> getCacheConfiguration();

        /**
         * Registers the specified service in the cache. The service can later be
         * retrieved by calls to {@link Cache#getService(Class)} with the specified class
         * as parameter.
         * 
         * @param <T>
         *            the type of the service
         * @param clazz
         *            the type of the service
         * @param service
         *            the service to register
         */
        <T> void registerService(Class<T> clazz, T service);

        /**
         * Returns the type of cache that is being initialized.
         * 
         * @return the type of cache that is being initialized
         */
        Class<? extends Cache> getCacheType();
    }
}
