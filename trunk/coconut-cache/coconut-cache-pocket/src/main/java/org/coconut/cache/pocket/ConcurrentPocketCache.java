/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Currently this class uses LRU as a replacement policy for choosing which
 * entries to evict. However, this 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ConcurrentPocketCache<K, V> {
    // built in lru policy
    public List<Map.Entry<K, V>> evictNext(int count) {
        return null;
    }
    
    static class ValueInfo<V> {
        long updateTime;
        V value;
    }
}
