/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.loading;

// START SNIPPET: class
import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;
import org.coconut.cache.service.loading.CacheLoadingService;

public class VariousLoading {
    public static void main(String[] args) {
        Cache<String, String> cache = null;// replace with real cache instance
        CacheLoadingService<String, String> cls = CacheServices.loading(cache);

        //Forced loading will always load the specified elements
        cls.forceLoad("http://www.google.com"); //will load the specified url even if it already present in the cache
        cls.forceLoadAll(); // will reload all elements in the cache
        
        //(Ordinary) loading will only load the specified elements if they are either not present in the cache or expired or needs refreshing
        cls.load("http://www.google.com"); //will load the specified url only if it is not already present in the cache or if it is expired or needs refreshing 
        cls.loadAll(); // will reload all elements in the cache that is expired or needs refreshing
    }
}
// END SNIPPET: class
