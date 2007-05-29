/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.service;

import java.util.Map;

import org.coconut.cache.spi.CacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheService extends CacheService {

	/**
     * Registers all the public services this service exposes. For example, an
     * expiration service would use:
     * 
     * <pre>
     * Map&lt;Class, Object&gt; serviceMap = null;
     * serviceMap.put(CacheExpirationMXBean.class, null);
     * serviceMap.put(CacheExpirationService.class, null);
     * </pre>
     * 
     * @param serviceMap
     */
	void registerServices(Map<Class, Object> serviceMap);
}
