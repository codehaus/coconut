package org.coconut.cache.examples.loading;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;

public class SimpleUrlLoaderUsage {
    // START SNIPPET: class
    public static void main(String[] args) {
        CacheConfiguration<String, String> conf = CacheConfiguration.create();
        conf.loading().setLoader(new SimpleUrlLoader());
        Cache<String, String> cache = conf.newCacheInstance(UnsynchronizedCache.class);
        System.out.println(cache.get("http://www.google.com"));
    }
    // END SNIPPET: class
}
