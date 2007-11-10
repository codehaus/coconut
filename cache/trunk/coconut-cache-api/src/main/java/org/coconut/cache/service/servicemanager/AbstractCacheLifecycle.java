/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * An abstract base class for implementing a {@link CacheLifecycle}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractCacheLifecycle implements CacheLifecycle {

    /** The name of the service. */
    private final String name;

    /**
     * Creates a new AbstractCacheService with the same service name as the name of the
     * parent Class extending this class.
     */
    public AbstractCacheLifecycle() {
        name = getClass().getSimpleName();
    }

    /**
     * Creates a new AbstractCacheService with the specified name.
     * 
     * @param name
     *            the name of the cache service
     */
    public AbstractCacheLifecycle(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        this.name = name;
    }

    /**
     * Returns the name of this lifecycle.
     * @return the name of this lifecycle
     */
    public final String getName() {
        return name;
    }

    // /CLOVER:OFF
    /** {@inheritDoc} */
    public void initialize(CacheConfiguration<?, ?> configuration) {}

    /** {@inheritDoc} */
    public void registerServices(Map<Class<?>, Object> serviceMap) {}

    /** {@inheritDoc} */
    public void start(Map<Class<?>, Object> allServices) {}

    /** {@inheritDoc} */
    public void started(Cache<?, ?> cache) {}

    /** {@inheritDoc} */
    public void shutdown() {}

    /** {@inheritDoc} */
    public void terminated() {}
    // /CLOVER:ON
}
