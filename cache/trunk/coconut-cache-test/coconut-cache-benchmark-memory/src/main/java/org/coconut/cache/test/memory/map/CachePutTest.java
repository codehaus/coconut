/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.memory.map;

import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;
import org.coconut.cache.test.KeyValues;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CachePutTest extends MapMemoryTest {

	/**
	 * @param factory
	 */
	public CachePutTest(CacheAdapterFactory factory) {
		super(factory);
	}

	/**
     * @see org.coconut.cache.test.memory.map.MapMemoryTest#doRun(org.coconut.cache.test.adapter.CacheTestAdapter,
     *      int)
     */
	@Override
	protected void doRun(CacheTestAdapter adapter, int iterations) throws Exception {
		for (int j = 0; j < iterations; j++) {
			adapter.put(KeyValues.getString(j), KeyValues.getInt(j));
		}
	}

}
