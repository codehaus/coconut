/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheService {
	/**
     * Returns the readable name of the service.
     * 
     * @return the readable name of the service
     */
	String getName();

	void start(CacheConfiguration<?, ?> configuration);
	void started(Cache<?,?> cache);
	void shutdown(Cache<?, ?> c);

	// void shutdownNow(Cache<?, ?> c);
	void terminated();
}
