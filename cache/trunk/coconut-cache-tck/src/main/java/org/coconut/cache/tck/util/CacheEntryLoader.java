/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.util.AbstractCacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEntryLoader extends AbstractCacheLoader<Integer, CacheEntry<Integer, String>> {

    public Map<Integer, CacheEntry<Integer, String>> entries = new ConcurrentHashMap<Integer, CacheEntry<Integer, String>>();

    /**
     * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
     */
    public CacheEntry<Integer, String> load(Integer key) throws Exception {
        return entries.get(key);
    }

}
