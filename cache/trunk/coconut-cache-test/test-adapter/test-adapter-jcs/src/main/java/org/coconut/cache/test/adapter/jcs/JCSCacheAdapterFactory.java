/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.jcs;

import java.util.Properties;

import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class JCSCacheAdapterFactory implements CacheAdapterFactory {
	static void shutupLogger(String logger) {
		Logger.getLogger(logger).setLevel(Level.OFF);
	}

	static {
		// JCS is mentally retarded, and wins the prize, without much
		// competition for "most difficult to configure cacheing framework"
    	Logger.getRootLogger().setLevel(Level.OFF);
		CompositeCacheManager cc = CompositeCacheManager.getUnconfiguredInstance();
		Properties props = new Properties();
		props.put("jcs.default", "");
		props.put("jcs.default.cacheattributes",
				"org.apache.jcs.engine.CompositeCacheAttributes");
		props.put("jcs.default.cacheattributes.MemoryCacheName",
				"org.apache.jcs.engine.memory.lru.LRUMemoryCache");
		cc.configure(props);
	}

	/**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#createAdapter()
     */
	public CacheTestAdapter createAdapter() throws CacheException {
		return new JCSCacheAdapter();
	}

	/**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#supportsPut()
     */
	public boolean supportsPut() {
		return true;
	}

}
