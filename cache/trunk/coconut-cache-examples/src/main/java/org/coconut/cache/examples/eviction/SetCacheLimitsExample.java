/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.eviction;

import org.coconut.cache.CacheConfiguration;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SetCacheLimitsExample {
    // START SNIPPET: class
    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.eviction().setMaximumSize(1000);
        cc.eviction().setMaximumVolume(50000);
    }
    // END SNIPPET: class
}
