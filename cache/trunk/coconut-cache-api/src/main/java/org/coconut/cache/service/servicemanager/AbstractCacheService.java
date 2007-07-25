/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Map;
import java.util.concurrent.Executor;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * An abstract base class for implementing a {@link CacheService}. By ex
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheService implements CacheService {

    /** The name of the service. */
    private final String name;

    /**
     * Creates a new AbstractCacheService with the same service name as the name of the
     * parent Class extending this class.
     */
    public AbstractCacheService() {
        name = getClass().getSimpleName();
    }

    /**
     * Creates a new AbstractCacheService with the specified name.
     * 
     * @param name
     *            the name of the cache service
     */
    public AbstractCacheService(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
    }

    /** {@inheritDoc} */
    public final String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void initialize(CacheConfiguration<?, ?> configuration) {}

    /** {@inheritDoc} */
    public void initialize(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {}

    /** {@inheritDoc} */
    public void start(Map<Class<?>, Object> allServices) {}

    /** {@inheritDoc} */
    public void started(Cache<?, ?> cache) {}

    /** {@inheritDoc} */
    public void shutdown(Executor e) {}

    /** {@inheritDoc} */
    public void terminated() {}

}
