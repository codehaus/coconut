/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.memory.map;

import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;
import org.coconut.cache.test.memory.MemorySession;
import org.coconut.cache.test.memory.MemoryTest;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class MapMemoryTest implements MemoryTest {
	private final CacheAdapterFactory factory;

	public MapMemoryTest(final CacheAdapterFactory factory) {
		if (factory == null) {
			throw new NullPointerException("factory is null");
		}
		this.factory = factory;
	}

	class MapMemorySession implements MemorySession {
		private final CacheTestAdapter adapter;

        public MapMemorySession(final CacheTestAdapter adapter) {
            this.adapter = adapter;
        }
		public void setupSession() {}

		public void teardownSession() {}


		public void run(int iterations) throws Exception {
			doRun(adapter, iterations);
		}
	}

	/**
     * @see org.coconut.cache.test.memory.MemoryTest#newSession()
     */
	@SuppressWarnings("unchecked")
	public MemorySession newSession() throws Exception {
		CacheTestAdapter adapter = factory.createAdapter();
		return new MapMemorySession(adapter);
	}

	protected abstract void doRun(CacheTestAdapter adapter, int iterations)
			throws Exception;
}
