/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import org.coconut.cache.Cache;

/**
 * An abstract implementation of InternalCacheServiceManager.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractCacheServiceManager implements InternalCacheServiceManager {

    /** The cache we are managing. */
    private final Cache<?, ?> cache;

    /**
     * Creates a new AbstractCacheServiceManager.
     * 
     * @param cache
     *            the cache we are managing
     * @throws NullPointerException
     *             if the specified cache is null
     */
    AbstractCacheServiceManager(Cache<?, ?> cache) {
        if (cache == null) {
            throw new NullPointerException("cache is null");
        }
        this.cache = cache;
    }

    /** {@inheritDoc} */
    public final <T> T getService(Class<T> serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("serviceType is null");
        }
        T t = (T) getAllServices().get(serviceType);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + serviceType);
        }
        return t;
    }

    /** {@inheritDoc} */
    public boolean isShutdown() {
        return getRunState().isShutdown();
    }

    /** {@inheritDoc} */
    public boolean isStarted() {
        return getRunState().isStarted();
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return getRunState().isTerminated();
    }

    /**
     * Returns the service manager's cache.
     * 
     * @return the service manager's cache
     */
    Cache<?, ?> getCache() {
        return cache;
    }

    /** {@inheritDoc} */
    public final boolean hasService(Class<?> type) {
        return getAllServices().containsKey(type);
    }

    /**
     * Returns the state of the cache.
     * 
     * @return the state of the cache
     */
    abstract RunState getRunState();

    static enum RunState {
        COULD_NOT_START, NOTRUNNING, STARTING, RUNNING, SHUTDOWN, STOP, TERMINATED, TIDYING;

        public boolean isShutdown() {
            return this != RUNNING && this != NOTRUNNING;
        }

        public boolean isStarted() {
            return this != NOTRUNNING && this != COULD_NOT_START;
        }

        public boolean isTerminated() {
            return this == TERMINATED || this == COULD_NOT_START;
        }

        public boolean isTerminating() {
            return this == SHUTDOWN || this == STOP;
        }
    }

    public void shutdown(Throwable cause) {
        // First thing we must do is set the exception so later invocations
        // of methods on the cache will throw it.
        // after that we might want to try and shutdown the cache
        // perhaps we can have a terminateCache(Throwable cause)
        // what about terminatation of services??
        // lots to think about
        shutdown();

        throw new UnsupportedOperationException();
    }
}
