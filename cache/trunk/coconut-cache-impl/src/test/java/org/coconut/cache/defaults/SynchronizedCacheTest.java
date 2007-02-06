/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.coconut.cache.tck.TCKClassTester;
import org.coconut.cache.tck.TCKRunner;
import org.junit.runner.RunWith;

@RunWith(TCKRunner.class)
@TCKClassTester(SynchronizedCache.class)
public class SynchronizedCacheTest {

    public static Test suite() {
        return new JUnit4TestAdapter(SynchronizedCacheTest.class);
    }
}
