/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.tck.TCKClassTester;
import org.coconut.cache.tck.TCKRunner;
import org.junit.runner.RunWith;

@RunWith(TCKRunner.class)
@TCKClassTester(UnsynchronizedCache.class)
public class UnlimitedCacheTest {

    public static Test suite() {
        return new JUnit4TestAdapter(UnlimitedCacheTest.class);
    }


}
