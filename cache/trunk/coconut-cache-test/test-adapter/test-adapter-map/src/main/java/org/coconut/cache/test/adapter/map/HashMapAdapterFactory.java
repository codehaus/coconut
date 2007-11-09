/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.map;

import java.util.HashMap;

import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class HashMapAdapterFactory implements CacheAdapterFactory {

	/**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#createAdapter()
     */
	public CacheTestAdapter createAdapter() {
		return new MapAdapter(new HashMap<String, Object>());
	}

	/**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#supportsPut()
     */
	public boolean supportsPut() {
		return true;
	}

}
