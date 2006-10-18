/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import java.util.Calendar;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
//START SNIPPET: class
public class Refresh2200FilterExample<K, V> implements Filter<CacheEntry<K, V>> {
    private volatile long nextRefresh;

    private volatile long current;

    public boolean accept(CacheEntry<K, V> entry) {
        long now = System.currentTimeMillis();
        if (now > nextRefresh) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 22);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            current = nextRefresh;
            nextRefresh = c.getTimeInMillis();
        }
        return current > entry.getLastUpdateTime();
    }
}
// END SNIPPET: class