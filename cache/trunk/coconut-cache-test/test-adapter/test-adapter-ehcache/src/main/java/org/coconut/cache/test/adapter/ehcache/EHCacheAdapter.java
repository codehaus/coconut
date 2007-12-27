/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class EHCacheAdapter implements CacheTestAdapter {
	private final String name = "EHCache" + System.nanoTime();

	private final Cache cache;

	/**
     * @param map
     */
	public EHCacheAdapter() {
		CacheManager.getInstance().addCache(name);
		cache = CacheManager.getInstance().getCache(name);
	}

	/**
     * @see coconut.cache.test.adapter.CacheTestAdapter#put(java.lang.String,
     *      java.lang.Object)
     */
	public void put(String key, Object value) {
		cache.put(new Element(key, value));
	}

	/**
     * @see org.coconut.cache.test.adapter.CacheTestAdapter#get(java.lang.String)
     */
	public Object get(String key) throws Exception {
		return cache.get(key).getValue();
	}

    public Object getCache() {
        return cache;
    }

    public int size() {
        return cache.getSize();
    }

}
