/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link CacheServices} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheServicesTest.java 427 2007-11-10 13:15:25Z kasper $
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CachesTest {

    /** The junit mockery. */
    private final Mockery context = new JUnit4Mockery();

    /** The default cache to test upon. */
    private Cache<Integer, String> cache;

    /**
     * Setup the cache mock.
     */
    @Before
    public void setupCache() {
        cache = context.mock(Cache.class);
    }

    /**
     * Tests {@link Caches#runClear(Cache)}.
     */
    @Test
    public void runClear() {
        context.checking(new Expectations() {
            {
                one(cache).clear();
            }
        });
        Caches.runClear(cache).run();
    }

    /**
     * Tests that {@link Caches#runClear(Cache)} throws a {@link NullPointerException}
     * when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void runClearNPE() {
        Caches.runClear(null);
    }
}
