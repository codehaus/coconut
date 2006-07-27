/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.util;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheException;
import org.coconut.test.MavenDummyTest;
import org.coconut.test.MockTestCase;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheSingletonTest extends MavenDummyTest {
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CacheSingletonTest.class);
    }

    @Test(expected = CacheException.class)
    public void testNoConfiguration() {
        CacheSingleton.getCacheInstance();
    }

    @Test
    public void testSetCacheInstance() {
        Cache c = MockTestCase.mockDummy(Cache.class);
        CacheSingleton.setCacheInstance(c);
        Assert.assertSame(c, CacheSingleton.getCacheInstance());
    }
}
