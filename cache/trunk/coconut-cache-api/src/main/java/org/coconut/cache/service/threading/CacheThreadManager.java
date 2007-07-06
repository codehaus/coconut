/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.threading;

public interface CacheThreadManager {
    /**
     * Returns a CacheServiceThreadManager for the specified service.
     * 
     * @param service
     * @return a CacheServiceThreadManager for the specified service
     */ 
    CacheServiceThreadManager createCacheExecutor(Class<?> service);
}
