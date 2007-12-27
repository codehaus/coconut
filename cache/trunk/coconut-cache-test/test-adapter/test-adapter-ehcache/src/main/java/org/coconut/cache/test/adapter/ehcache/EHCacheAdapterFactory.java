/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.ehcache;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EHCacheAdapterFactory implements CacheAdapterFactory {

	static {
		Logger.getRootLogger().setLevel(Level.OFF);
	}
	/**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#createAdapter()
     */
	public CacheTestAdapter createAdapter() {
		return new EHCacheAdapter();
	}

	/**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#supportsPut()
     */
	public boolean supportsPut() {
		return true;
	}

//	public String toString() {
//	    return 
//	}
}
