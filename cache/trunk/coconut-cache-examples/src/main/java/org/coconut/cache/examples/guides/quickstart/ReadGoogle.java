package org.coconut.cache.examples.guides.quickstart;

//START SNIPPET: CacheGoogle
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.examples.general.CacheHTTPExample.UrlLoader;

public class ReadGoogle {
    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.loading().setLoader(new UrlLoader());
        UnsynchronizedCache<String, String> c = cc.newInstance(UnsynchronizedCache.class);
        readGoogle(c, "Not Cached : ");
        readGoogle(c, "Cached     : ");
    }

    public static void readGoogle(Cache<?, ?> c, String prefix) {
        long start = System.nanoTime();
        c.get("http://www.google.com");
        System.out.println(prefix + " Time to read www.google.com: "
                + ((System.nanoTime() - start) / 1000000.0) + " ms");
    }
}
// END SNIPPET: CacheGoogle
