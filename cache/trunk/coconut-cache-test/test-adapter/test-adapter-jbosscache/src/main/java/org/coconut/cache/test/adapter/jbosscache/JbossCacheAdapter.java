/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.jbosscache;

import org.coconut.cache.test.adapter.CacheTestAdapter;
import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.Configuration.CacheMode;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class JbossCacheAdapter implements CacheTestAdapter {

	private final String name = "JbossCache" + System.nanoTime();

	private final Cache<String, Object> cache;
	private final Fqn fqn = Fqn.fromString("/foo");
	/**
     * @param map
     */
	public JbossCacheAdapter() throws Exception {
		Configuration conf = new Configuration();
		conf.setCacheMode(CacheMode.LOCAL);
		CacheFactory factory = DefaultCacheFactory.getInstance();
		cache = factory.createCache(conf);
	}

	/**
     * @see coconut.cache.test.adapter.CacheTestAdapter#put(java.lang.String,
     *      java.lang.Object)
     */
	public void put(String key, Object value) throws Exception {
		cache.put(fqn, key, value);
	}

	/**
     * @see org.coconut.cache.test.adapter.CacheTestAdapter#get(java.lang.String)
     */
	public Object get(String key) throws Exception {
		return cache.get(fqn, key);
	}
}
