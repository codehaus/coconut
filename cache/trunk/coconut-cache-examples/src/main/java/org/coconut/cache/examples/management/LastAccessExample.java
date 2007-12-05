/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.management;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.SynchronizedCache;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.operations.Ops.Predicate;

/**
 * TODO fix example, we have idle time now.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class LastAccessExample {
    public static void main(String[] args) throws InterruptedException {
        CacheConfiguration<String, String> conf = CacheConfiguration.create("WebPage-Cache");
        conf.management().setEnabled(true);
        conf.expiration().setExpirationFilter(new LastAccessFilter<String, String>());
        SynchronizedCache<String, String> cache = conf.newCacheInstance(SynchronizedCache.class);
        cache.prestart();
        Thread.sleep(1000000);
    }

    @ThreadSafe
    public static class LastAccessFilter<K, V> implements Predicate<CacheEntry<K, V>> {
        private volatile int seconds = 60 * 60;// initial 1 hour

        public boolean evaluate(CacheEntry<K, V> entry) {
            long delta = System.currentTimeMillis()
                    - Math.max(entry.getCreationTime(), entry.getLastAccessTime());
            // return true to indicate that the entry should be refreshed
            return delta > seconds * 1000;
        }

        @ManagedAttribute(defaultValue = "TimeoutAccess", description = "Set expiration time based on last access time")
        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public int getSeconds() {
            return seconds;
        }
    }
}
