/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.Map;

/**
 * Move getAll services to here?
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheServiceManagerService {

    /**
     * Returns whether or not this cache contains a service of the specified type.
     * 
     * @param serviceType
     *            the type of service
     * @return true if this cache has a service of the specified type registered,
     *         otherwise false
     * @see #getInternalService(Class)
     * @see CacheServiceManagerService#getAllServices()
     */
    boolean hasService(Class<?> serviceType);

    /**
     * Returns all registered services within the cache.
     * 
     * @return a map of all registered services
     */
    Map<Class<?>, Object> getAllServices();

    //cannot register services at runtime for now
    //<T extends CacheService> T registerService(T lifecycle);

    /* Needs to connect to some remote server. */
    //Future must return null on completion
    //void asynchronousStartup(Future );
    
    //void preStart();//prestarts the cache
    
    /**
     * If a service has to block while shutting  cannot be completly shutdown
     * Must be called
     * @param service
     * @param service2
     * @throws IllegalStateException
     *             if this method is called before shutdown has been called on the cache
     *             or if it called after it has been terminated
     */
    void shutdownServiceAsynchronously(AsynchronousShutdownService service2);
}
