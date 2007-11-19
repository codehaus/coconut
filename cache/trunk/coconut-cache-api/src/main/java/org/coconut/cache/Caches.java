/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import org.coconut.cache.service.loading.CacheLoader;

/**
 * Factory and utility methods for for creating different types of
 * {@link Cache Caches} and {@link CacheLoader CacheLoaders}. Furthermore there
 * are a number of small utility functions concerning general cache usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheServices.java 469 2007-11-17 14:32:25Z kasper $
 */
public final class Caches {

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Caches() {
    }
    // /CLOVER:ON

    /**
     * Returns a Runnable that when executed will call the clear method on the
     * specified cache.
     * <p>
     * The following example shows how this can be used to clear the cache every
     * hour.
     * 
     * <pre>
     * Cache c;
     * ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
     * ses.scheduleAtFixedRate(Caches.runClear(c), 0, 60 * 60, TimeUnit.SECONDS);
     * </pre>
     * 
     * @param cache
     *            the cache on which to call evict
     * @return a runnable where invocation of the run method will clear the
     *         specified cache
     * @throws NullPointerException
     *             if the specified cache is <tt>null</tt>.
     */
    public static Runnable runClear(Cache<?, ?> cache) {
        return new ClearRunnable(cache);
    }


    /**
     * A runnable used for calling clear on a cache.
     */
    static class ClearRunnable implements Runnable  {

        /** The cache to call clear on. */
        private final Cache<?, ?> cache;

        /**
         * Creates a new ClearRunnable.
         * 
         * @param cache
         *            the cache to call clear on
         */
        ClearRunnable(Cache<?, ?> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /** {@inheritDoc} */
        public void run() {
            cache.clear();
        }
    }
}
