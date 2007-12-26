package org.coconut.cache.examples.management;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.SynchronizedCache;

public class CountCacheUsage {
    public static void main(String[] args) throws InterruptedException {
      //START SNIPPET: class
        CacheConfiguration<String, String> conf = CacheConfiguration.create("CountCacheUsage");
        conf.loading().setLoader(new CountCacheLoader());
        conf.management().setEnabled(true); //enables JMX management
        Cache<String, String> cache = conf.newCacheInstance(SynchronizedCache.class);
        cache.get("count-1");
        cache.get("count-2");
      //END SNIPPET: class
        Thread.sleep(10 * 60 * 1000); // sleep 10 minutes, to allow management console to startup
    }
}
