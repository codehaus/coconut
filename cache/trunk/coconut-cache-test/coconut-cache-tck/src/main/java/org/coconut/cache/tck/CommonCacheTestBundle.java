/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck;

import org.coconut.cache.Cache;
import org.junit.Before;

/**
 * This is base class that all test bundle should extend.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Header$
 */
public abstract class CommonCacheTestBundle extends AbstractCacheTCKTestBundle {
    protected Cache<Integer, String> c0;

    protected Cache<Integer, String> c1;

    protected Cache<Integer, String> c2;

    protected Cache<Integer, String> c3;

    protected Cache<Integer, String> c4;

    protected Cache<Integer, String> c5;

    protected Cache<Integer, String> c6;

    @Before
    public void setUp() throws Exception {
        c0 = newCache(0);
        c1 = newCache(1);
        c2 = newCache(2);
        c3 = newCache(3);
        c4 = newCache(4);
        c5 = newCache(5);
        c6 = newCache(6);
    }

}
