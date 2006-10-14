/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.memory;

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheLoader;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

class CustomExpirationFilter2<K, V> implements Filter<CacheEntry<K, V>> {
    private volatile long nextRefresh;

    private volatile long current;

    public boolean accept(CacheEntry<K, V> entry) {
        long now = System.currentTimeMillis();
        if (now > nextRefresh) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 6);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            current = nextRefresh;
            nextRefresh = c.getTimeInMillis();
        }
        return current > entry.getLastUpdateTime();
    }
}

public class HmmTest {
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 23);
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        System.out.println(c.getTimeInMillis());

        System.out.println();
    }

    public static void main2(String[] args) throws InterruptedException {
        CacheConfiguration cc = CacheConfiguration.newConf();

        cc.expiration().setDefaultTimeout(24 * 60 * 60, TimeUnit.SECONDS);
        // cc.backend().setLoader(new ErrorCacheLoader());
        // cc.backend().setStore(new SysoutStore<Object, Object>(null));
        Cache c = cc.create(UnlimitedCache.class);
        for (int i = 0; i < 100000; i++) {
            c.put(i, i);
        }
        Thread.sleep(10000000);
        Cache<String, String> cache;
      //  caches.put("key", "value", 60 * 60, TimeUnit.SECONDS);

        // c.putIfAbsent("foos", 6);
        // // System.out.println(c.get(4));
        // System.out.println("bye");

        // Runnable r = new Runnable() {
        //
        // public void run() {
        // throw new Error();
        // }
        // };
        // ThreadPoolExecutor e = new ThreadPoolExecutor(1, 1, 0L,
        // TimeUnit.MILLISECONDS,
        // new LinkedBlockingQueue<Runnable>());
        // //e.execute(r);
        // new Thread(r).start();
    }

    static class CustomExpirationFilter<K, V> implements Filter<CacheEntry<K, V>> {
        public boolean accept(CacheEntry<K, V> entry) {
            long delta = System.currentTimeMillis() - entry.getLastAccessTime();
            return delta > 60 * 60 * 1000;
        }
    }

    static class CustomExpirationFilter2<K, V> implements Filter<CacheEntry<K, V>> {
        private volatile long nextRefresh;

        private volatile long current;

        public boolean accept(CacheEntry<K, V> entry) {
            long now = System.currentTimeMillis();
            if (now > nextRefresh) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 6);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                current = nextRefresh;
                nextRefresh = c.getTimeInMillis();
            }
            return current > entry.getLastUpdateTime();
        }
    }

    static class ErrorCacheLoader<K, V> implements CacheLoader<K, V> {

        /**
         * @see org.coconut.cache.CacheLoader#load(java.lang.Object)
         */
        public V load(K key) throws Exception {
            throw new Error();
        }

        /**
         * @see org.coconut.cache.CacheLoader#loadAll(java.util.Collection)
         */
        public Map<K, V> loadAll(Collection<? extends K> keys) throws Exception {
            throw new Error();
        }

    }
}
