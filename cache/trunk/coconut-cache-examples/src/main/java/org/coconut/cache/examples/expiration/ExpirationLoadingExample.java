/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.loading.AbstractCacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExpirationLoadingExample {
    // START SNIPPET: class
    static class ExpirationLoader extends AbstractCacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            TimeToLiveAttribute.INSTANCE.setAttribute(attributes, 60 * 60, TimeUnit.SECONDS);
            return "some value";
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.loading().setLoader(new ExpirationLoader());
        Cache<Integer, String> cache = cc.newCacheInstance(UnsynchronizedCache.class);
        cache.get(4); // item will expire after 1 hour (60 * 60 seconds)
    }
    // END SNIPPET: class
}
