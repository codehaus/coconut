package org.coconut.cache.test.harness;

import java.net.URL;

import org.coconut.cache.defaults.SynchronizedCache;
import org.coconut.cache.test.CacheTestRunner;

public class TestStuff2 {
    public static void main(String[] args) throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource(
                "org/coconut/cache/test/harness/test.xml");
        CacheTestRunner htr = new CacheTestRunner(url.openStream(),SynchronizedCache.class);
        htr.start();
    }
}
