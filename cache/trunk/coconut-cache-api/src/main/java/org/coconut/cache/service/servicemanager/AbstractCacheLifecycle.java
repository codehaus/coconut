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

    /**
     * Returns the name of this lifecycle.
     * @return the name of this lifecycle
     */
    public final String getName() {
        return getClass().getSimpleName();
    }

    // /CLOVER:OFF
    /** {@inheritDoc} */
    public void initialize(CacheLifecycleInitializer cli) {}

    /** {@inheritDoc} */
    public void start(CacheServiceManagerService serviceManager) {}

    /** {@inheritDoc} */
    public void started(Cache<?, ?> cache) {}

    /** {@inheritDoc} */
    public void shutdown() {}

    /** {@inheritDoc} */
    public void terminated() {}
    // /CLOVER:ON
}
