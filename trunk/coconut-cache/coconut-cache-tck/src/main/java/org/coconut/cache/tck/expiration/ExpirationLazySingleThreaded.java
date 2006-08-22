/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.expiration;

import org.coconut.cache.CacheConfiguration.ExpirationStrategy;

/**
 * These test bundle tests the expiration strategy <tt>Lazy</tt> in a single
 * threaded environment. The problem is that we don't have asynchronus loading
 * so this policy should act as the <tt>Strict</tt> strategy in this
 * environment.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ExpirationLazySingleThreaded extends ExpirationStrict {

    @Override
    protected ExpirationStrategy getStrategy() {
        return ExpirationStrategy.LAZY;
    }
    
}
