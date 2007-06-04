/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.service;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheServiceManager {

	/**
     * Prestarts all services.
     */
	void prestart();

	void lazyStart(boolean failIfShutdown);

	void registerServices(Class<? extends AbstractInternalCacheService>... service);

	void registerService(Class type, Class<? extends AbstractInternalCacheService> service);

	ServiceStatus getCurrentState();

	Map<Class<?>, Object> getAllPublicServices();
	/**
     * Returns a list of all the public exposed services.
     * 
     * @return
     */
	List getPublicServices();

	<T> T getService(Class<T> type);
	<T> T getPublicService(Class<T> type);

	boolean hasPublicService(Class type);
}
