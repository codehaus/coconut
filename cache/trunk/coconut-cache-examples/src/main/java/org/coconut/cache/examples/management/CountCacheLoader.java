/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.management;

// START SNIPPET: class
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.SynchronizedCache;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.management.annotation.ManagedAttribute;

public class CountCacheLoader extends AbstractCacheLoader<String, String> implements
        ManagedLifecycle {

    private final AtomicLong numberOfLoads = new AtomicLong();

    public void manage(ManagedGroup parent) {
        parent.addChild("MyCacheLoader", "Cache Loading statistics").add(this);
    }

    @ManagedAttribute(defaultValue = "numberOfLoads", description = "The number of loads by the cache loader")
    public long getNumberOfLoads() {
        return numberOfLoads.get();
    }

    public String load(String key, AttributeMap attributes) throws Exception {
        numberOfLoads.incrementAndGet();
        return key.toLowerCase();
    }

    public static void main(String[] args) throws InterruptedException {
        CacheConfiguration<String, String> conf = CacheConfiguration.create("CountCacheUsage");
        conf.loading().setLoader(new CountCacheLoader());
        conf.management().setEnabled(true); // enables JMX management
        Cache<String, String> cache = conf.newCacheInstance(SynchronizedCache.class);
        for (int i = 0; i < 600; i++) {
            cache.get("count" + i);
            Thread.sleep(1000); // sleep some time, to allow management console to startup
        }
    }
}
// END SNIPPET: class
