/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheConfiguration.ExpirationStrategy;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ConfiguringExpirationStrategyExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        CacheConfiguration<String, String> cc = CacheConfiguration.newConf();
        cc.expiration().setStrategy(ExpirationStrategy.STRICT);
        // END SNIPPET: class
    }
}
