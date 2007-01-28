/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.management;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class LastAccessExample {
    public static void main(String[] args) throws InterruptedException {
        CacheConfiguration<String, String> conf = CacheConfiguration.create();
        conf.setName("WebPage-Cache").jmx().setAutoRegister(true);
        conf.expiration().setFilter(new LastAccessFilter());
        UnsynchronizedCache cache = conf.newInstance(UnsynchronizedCache.class);
        cache.start();
        Thread.sleep(1000000);
    }

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
