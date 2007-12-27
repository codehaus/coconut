/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.map;

import java.util.Map;

import org.coconut.cache.test.adapter.CacheAdapterFactory;
import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MapAdapterFactory implements CacheAdapterFactory {

    private final Class<? extends Map> c;

    public MapAdapterFactory(Class<? extends Map> c) {
        this.c = c;
    }

    /**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#createAdapter()
     */
    public CacheTestAdapter createAdapter() throws Exception {
        return new MapAdapter(c.newInstance());
    }

    /**
     * @see coconut.cache.test.adapter.CacheAdapterProvider#supportsPut()
     */
    public boolean supportsPut() {
        return true;
    }

    public String toString() {
        return c.getCanonicalName();
    }
}
