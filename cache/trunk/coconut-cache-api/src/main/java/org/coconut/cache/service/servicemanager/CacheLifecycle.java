/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import org.coconut.cache.Cache;

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
     * exception thrown by this method will not be handled.
     * 
     * @param cli
     *            provides information about the configuration of the cache
     */
    void initialize(CacheLifecycleInitializer cli);

    /**
     * Starts the service. The specified serviceManager can be used to retrieve other
     * services.
     * 
     * @param serviceManager
     *            the caches service manager
     */
    void start(CacheServiceManagerService serviceManager);

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
    void shutdown();

    /**
     * The {@link Cache#shutdownNow()} method has been invoked. This method is always
     * invoked after {@link #shutdown()} if invoked at all.
     */
    void shutdownNow();

    /**
     * Method invoked when the cache has terminated. This method is invoked as the last
     * method in this lifecycle interface and is called when the cache and all of it
     * services has been succesfully shutdown. This method is also called if the cache
     * failed to initialize or start. But only if the service was succesfully initialized ({@link #initialize(CacheLifecycleInitializer)}
     * was run without failing).
     */
    void terminated();
}
