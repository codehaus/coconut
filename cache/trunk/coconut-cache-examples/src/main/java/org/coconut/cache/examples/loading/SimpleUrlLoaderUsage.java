package org.coconut.cache.examples.loading;

// START SNIPPET: class
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.SynchronizedCache;

public class SimpleUrlLoaderUsage {
    public static void main(String[] args) {
        CacheConfiguration<String, String> conf = CacheConfiguration.create();
        conf.loading().setLoader(new SimpleUrlLoader());
        Cache<String, String> cache = conf.newCacheInstance(SynchronizedCache.class);
        System.out.println(cache.get("http://www.google.com"));// uses CacheLoader
        System.out.println(cache.get("http://www.google.com"));// uses cached version
    }
}
// END SNIPPET: class
