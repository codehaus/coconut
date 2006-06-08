/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.tck.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.util.AbstractCacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
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
