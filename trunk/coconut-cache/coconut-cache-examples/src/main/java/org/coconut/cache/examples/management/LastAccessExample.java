/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
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
        CacheConfiguration<String, String> conf = CacheConfiguration.newConf();
        conf.setName("WebPage-Cache").jmx().setRegister(true);
        conf.expiration().setFilter(new LastAccessFilter());
        conf.createAndStart(UnsynchronizedCache.class);
        Thread.sleep(1000000);
    }

    public static class LastAccessFilter<K, V> implements Filter<CacheEntry<K, V>> {
        private volatile int seconds = 60 * 60;// initial 1 hour

        public boolean accept(CacheEntry<K, V> entry) {
            long delta = System.currentTimeMillis() - entry.getLastAccessTime();
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
