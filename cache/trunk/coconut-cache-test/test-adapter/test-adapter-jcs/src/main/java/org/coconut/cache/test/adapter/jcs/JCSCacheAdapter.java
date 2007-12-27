/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.jcs;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.stats.behavior.IStatElement;
import org.coconut.cache.test.adapter.CacheTestAdapter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class JCSCacheAdapter implements CacheTestAdapter {

    private final JCS cache;

    /**
     * @param map
     */
    public JCSCacheAdapter() throws CacheException {
        ICompositeCacheAttributes cattr = new CompositeCacheAttributes();
        cattr.setMaxObjects(Integer.MAX_VALUE);
        cache = JCS.getInstance("Cache" + System.nanoTime(), cattr);
    }

    /**
     * @see coconut.cache.test.adapter.CacheTestAdapter#put(java.lang.String,
     *      java.lang.Object)
     */
    public void put(String key, Object value) throws CacheException {
        cache.put(key, value);
    }

    /**
     * @see org.coconut.cache.test.adapter.CacheTestAdapter#get(java.lang.String)
     */
    public Object get(String key) throws Exception {
        return cache.get(key);
    }

    public Object getCache() {
//        System.out.println(cache.getStatistics());
        return cache;
    }

    public int size() {
        //return cache.;
        return -1;
    }
}
