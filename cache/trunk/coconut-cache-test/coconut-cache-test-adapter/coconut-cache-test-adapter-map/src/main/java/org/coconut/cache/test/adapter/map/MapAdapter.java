/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.map;

import java.util.Map;

import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class MapAdapter implements CacheTestAdapter {

	private final Map<String, Object> map;
	/**
	 * @param map
	 */
	public MapAdapter(final Map<String, Object> map) {
		this.map = map;
	}
	/**
	 * @see coconut.cache.test.adapter.CacheTestAdapter#put(java.lang.String, java.lang.Object)
	 */
	public void put(String key, Object value) {
		map.put(key, value);
	}
	/**
	 * @see org.coconut.cache.test.adapter.CacheTestAdapter#get(java.lang.String)
	 */
	public Object get(String key) throws Exception {
		return map.get(key);
	}

}
