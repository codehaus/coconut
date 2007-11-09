/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.eviction;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.eviction.CacheEvictionService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ScheduableTrimmerExample {
    static class TrimToSize implements Runnable {
        // all coconut cache implementations extend AbstractCache
        private final CacheEvictionService c;

        private final int threshold;

        private final int trimTo;

        public TrimToSize(Cache<?,?> cache, int threshold, int trimTo) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            } else if (threshold < 0) {
                throw new IllegalArgumentException("threshold must be non negative, was "
                        + threshold);
            } else if (trimTo < 0) {
                throw new IllegalArgumentException("trimTo must be non negative, was "
                        + trimTo);
            } else if (trimTo >= threshold) {
                throw new IllegalArgumentException(
                        "trimTo must smaller then threshold, was " + trimTo + " and "
                                + threshold);
            }
            this.threshold = threshold;
            this.trimTo = trimTo;
            c = CacheServices.eviction(cache);
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            c.trimToSize(trimTo);
        }
    }

    public static void main(String[] args) {
        UnsynchronizedCache<String, String> c = new UnsynchronizedCache<String, String>();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        
        ses.scheduleAtFixedRate(new TrimToSize(c, 1100, 1000), 0, 1, TimeUnit.SECONDS);

        // other code

        ses.shutdown();
    }
}
