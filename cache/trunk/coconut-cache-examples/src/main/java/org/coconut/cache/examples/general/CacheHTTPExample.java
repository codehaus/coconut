/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.general;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.loading.AbstractCacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheHTTPExample {
    public static class UrlLoader extends AbstractCacheLoader<String, String> {
        public String load(String key, AttributeMap ignore) throws Exception {
            URL url = new URL(key);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            int str;
            while ((str = in.read()) != -1) {
                sb.append((char) str);
            }
            in.close();
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.loading().setLoader(new UrlLoader());
        UnsynchronizedCache<String, String> c = cc.newCacheInstance(UnsynchronizedCache.class);
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
