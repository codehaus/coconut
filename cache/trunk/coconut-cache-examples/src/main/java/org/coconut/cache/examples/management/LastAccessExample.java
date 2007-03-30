/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.management;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class LastAccessExample {
    public static void main(String[] args) throws InterruptedException {
        CacheConfiguration<String, String> conf = CacheConfiguration
                .create("WebPage-Cache");
        conf.addService(CacheManagementConfiguration.class);
        conf.serviceExpiration().setExpirationFilter(new LastAccessFilter<String, String>());
        AbstractCache<String, String> cache = conf.newInstance(UnsynchronizedCache.class);
        cache.preStart();
        Thread.sleep(1000000);
    }

    @ThreadSafe
    public static class LastAccessFilter<K, V> implements Filter<CacheEntry<K, V>> {
        private volatile int seconds = 60 * 60;// initial 1 hour

        public boolean accept(CacheEntry<K, V> entry) {
            long delta = System.currentTimeMillis()
                    - Math.max(entry.getCreationTime(), entry.getLastAccessTime());
            // return true to indicate that the entry should be refreshed
            return delta > seconds * 1000;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        @ManagedAttribute(defaultValue = "TimeoutAccess", description = "Set expiration time based on last access time")
        public int getSeconds() {
            return seconds;
        }
    }
}
