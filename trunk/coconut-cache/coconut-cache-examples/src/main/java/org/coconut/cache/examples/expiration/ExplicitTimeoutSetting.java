/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.defaults.memory.UnlimitedCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExplicitTimeoutSetting {
    public static void main(String[] args) {
        // START SNIPPET: class
        Cache<String, String> cache = new UnlimitedCache<String, String>();
        cache.put("key", "value", 60 * 60, TimeUnit.SECONDS);
        // END SNIPPET: class
    }
}
