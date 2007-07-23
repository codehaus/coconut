/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.examples.loading.SimpleUrlLoader;
import org.coconut.core.AttributeMap;

public class ExpirationAndRefreshing {
    public static class StockQuoteLoader extends SimpleUrlLoader {
        @Override
        public String load(String key, AttributeMap ignore) throws Exception {
            return super.load("http://mystockservice.com/?quote=" + key, ignore);
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.loading().setLoader(new StockQuoteLoader());
        cc.loading().setDefaultTimeToRefresh(60, TimeUnit.SECONDS);
        cc.expiration().setDefaultTimeToLive(5 * 60, TimeUnit.SECONDS);
        Cache<String, String> cache = cc.newInstance(UnsynchronizedCache.class);
        System.out.println(cache.get("SUNW")); //will return the quote for Sun
    }
}
