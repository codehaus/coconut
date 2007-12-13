/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import org.coconut.cache.Cache;

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
    public void initialize(Initializer cli) {}

    /** {@inheritDoc} */
    public void start(CacheServiceManagerService serviceManager) throws Exception {}

    /** {@inheritDoc} */
    public void started(Cache<?, ?> cache) {}

    /** {@inheritDoc} */
    public void shutdown(Shutdown shutdown) throws Exception {} 
    
    ///** {@inheritDoc} */
    //public void shutdownNow() {}

    /** {@inheritDoc} */
    public void terminated() {}
    // /CLOVER:ON
}
