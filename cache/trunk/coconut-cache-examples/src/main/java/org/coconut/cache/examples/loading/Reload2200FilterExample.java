/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.loading;

import java.util.Calendar;

import org.coconut.cache.CacheEntry;
import org.coconut.predicate.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
// START SNIPPET: class
public class Reload2200FilterExample<K, V> implements Predicate<CacheEntry<K, V>> {
    private volatile long nextRefreshTime = getNextUpdateTime();

    private volatile long refreshTime = getNextUpdateTime();

    // TODO Right now the first invocation will force a reload of all items

    public boolean evaluate(CacheEntry<K, V> entry) {
        long now = System.currentTimeMillis();
        if (now > nextRefreshTime) {
            refreshTime = nextRefreshTime;
            nextRefreshTime = getNextUpdateTime();
        }
        return refreshTime > entry.getLastUpdateTime();
    }

    protected long getNextUpdateTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 22);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if (c.getTimeInMillis() > System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        return c.getTimeInMillis();
    }
}
// END SNIPPET: class
