/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import java.util.Map;

import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;
import org.coconut.cache.test.adapter.map.MapAdapterFactory;
import org.coconut.cache.test.keys.KeyValues;
import org.coconut.cache.test.memory.util.MemoryCounter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class MapMemoryOverheadTest {

    CacheAdapterFactory factory;

    /**
     * @param c
     */
    public MapMemoryOverheadTest(final Class<? extends Map> c) {
        this(new MapAdapterFactory(c));
    }

    /**
     * @param c
     */
    public MapMemoryOverheadTest(CacheAdapterFactory factory) {
        this.factory = factory;
    }
    String name;

    public long test2(final int iterations) throws Exception {
        long count = 0;
        MemoryCounter mc = new MemoryCounter();
        CacheTestAdapter m = factory.createAdapter();
        for (int j = 0; j < iterations; j++) {
            String key = KeyValues.getString(j);
            Integer value = KeyValues.getInt(j);
            count += mc.map(key);
            count += mc.map(value);
            m.put(key, value);
        }
        name=m.getCache().getClass().getCanonicalName();
        return mc.map(m.getCache()) - count;
    }

    @Override
    public String toString() {
        return name;
    }
}
