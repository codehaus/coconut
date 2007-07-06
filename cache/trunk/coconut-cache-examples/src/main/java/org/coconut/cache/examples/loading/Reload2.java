package org.coconut.cache.examples.loading;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;

public class Reload2 {

    public static <K, V> ScheduledFuture<?> scheduleLoad(final Cache<K, V> cache,
            final K key, ScheduledExecutorService ses) {
        return ses.schedule(new Runnable() {
            public void run() {
                CacheServices.loading(cache).load(key);
            }
        }, 100, TimeUnit.DAYS);
    }
}
