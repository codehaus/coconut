/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.whirlycache;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheConfiguration;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class WhirlyCacheAdapter implements CacheTestAdapter {

	private final String name = "WhirlyCache" + System.nanoTime();

	private final Cache cache;

	/**
     * @param map
     */
	public WhirlyCacheAdapter() throws CacheException {
		CacheConfiguration conf = new CacheConfiguration();
		conf.setName(name);
		conf.setBackend("com.whirlycott.cache.impl.ConcurrentHashMapImpl");
		conf.setTunerSleepTime(Integer.MAX_VALUE);
		conf.setPolicy("com.whirlycott.cache.policy.FIFOMaintenancePolicy");
		cache = CacheManager.getInstance().createCache(conf);
	}

	/**
     * @see coconut.cache.test.adapter.CacheTestAdapter#put(java.lang.String,
     *      java.lang.Object)
     */
	public void put(String key, Object value) {
		cache.store(key, value);
	}

	/**
	 * @see org.coconut.cache.test.adapter.CacheTestAdapter#get(java.lang.String)
	 */
	public Object get(String key) throws Exception {
		return cache.retrieve(key);
	}

}
