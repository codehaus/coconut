/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import org.coconut.cache.tck.CacheTCKClassSpecifier;
import org.coconut.cache.tck.CacheTCKRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CacheTCKRunner.class)
@CacheTCKClassSpecifier(UnsynchronizedCache.class)
public class UnsynchronizedCacheTest {

    @Test
    public void none() {
        
    }
    
}
