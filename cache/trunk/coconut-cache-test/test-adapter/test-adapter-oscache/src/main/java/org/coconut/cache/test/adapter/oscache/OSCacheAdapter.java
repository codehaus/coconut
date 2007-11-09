/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.oscache;

import com.opensymphony.oscache.base.Cache;

import java.util.Map;

import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class OSCacheAdapter implements CacheTestAdapter {

	private final Cache cache;
	/**
	 * @param map
	 */
	public OSCacheAdapter() {
		cache = new Cache(true, false, false);
	}
	/**
	 * @see coconut.cache.test.adapter.CacheTestAdapter#put(java.lang.String, java.lang.Object)
	 */
	public void put(String key, Object value) {
		cache.putInCache(key, value);
	}
	/**
	 * @see org.coconut.cache.test.adapter.CacheTestAdapter#get(java.lang.String)
	 */
	public Object get(String key) throws Exception {
		return cache.getFromCache(key);
	}

}
