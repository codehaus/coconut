/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.coconut.cache.tck.TCKClassTester;
import org.coconut.cache.tck.TCKRunner;
import org.coconut.test.MavenDummyTest;
import org.junit.runner.RunWith;

@RunWith(TCKRunner.class)
@TCKClassTester(UnlimitedCache.class)
public class UnlimitedCacheTest extends MavenDummyTest {

    public static Test suite() {
        return new JUnit4TestAdapter(UnlimitedCacheTest.class);
    }


}
