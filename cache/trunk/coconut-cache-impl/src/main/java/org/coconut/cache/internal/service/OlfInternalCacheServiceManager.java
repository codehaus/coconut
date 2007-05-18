/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface OlfInternalCacheServiceManager<K, V> {
    
    void initializeAll();
    void checkStarted();

    void registerInstance(Class type, Object instance);

    void registerServiceImplementations(Class... services);

    <T> T getAsCacheService(Class<T> type);

    /**
     * @param serviceType
     */
    boolean hasService(Class serviceType);
    
    <T> T getServiceOrThrow(Class<T> type);
}
