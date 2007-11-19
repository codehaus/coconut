/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static junit.framework.Assert.assertNull;

import java.util.Arrays;

import org.coconut.cache.Cache;
import org.coconut.cache.DummyCache;
import org.coconut.core.AttributeMaps;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link CacheLoaders} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheServicesTest.java 427 2007-11-10 13:15:25Z kasper $
 */
@RunWith(JMock.class)
public class CacheLoadersTest {
    /** The junit mockery. */
    private final Mockery context = new JUnit4Mockery();

    private DummyCache<Integer, String> dc;

    private CacheLoadingService<Integer, String> service;

    @Before
    public void setupDummy() {
        dc = new DummyCache();
        service = context.mock(CacheLoadingService.class);
        dc.addService(CacheLoadingService.class, service);
    }

    /** Tests {@link CacheLoaders#runLoad(Cache, Object)}. */
    @Test
    public void runLoad() {
        context.checking(new Expectations() {
            {
                one(service).load(1);
            }
        });
        CacheLoaders.runLoad(dc, 1).run();
    }

    /**
     * Tests {@link CacheLoaders#runLoad(Cache, Object)} throws
     * {@link NullPointerException} for <code>null</code> key.
     */
    @Test(expected = NullPointerException.class)
    public void runLoadNPE() {
        CacheLoaders.runLoad(dc, null);
    }

    /** Tests {@link CacheLoaders#runForceLoadAll(Cache)}. */
    @Test
    public void runLoad1() {
        context.checking(new Expectations() {
            {
                one(service).forceLoadAll();
            }
        });
        CacheLoaders.runForceLoadAll(dc).run();
    }

    /** Tests {@link CacheLoaders#runLoadAll(Cache)}. */
    @Test
    public void runLoad2() {
        context.checking(new Expectations() {
            {
                one(service).loadAll();
            }
        });
        CacheLoaders.runLoadAll(dc).run();
    }

    /** Tests {@link CacheLoaders#nullLoader()}. */
    @Test
    public void nullLoader() throws Exception {
        CacheLoader<Integer, String> cl = CacheLoaders.nullLoader();
        assertNull(cl.load(1, AttributeMaps.EMPTY_MAP));
        final CacheLoaderCallback<Integer, String> callback = context
                .mock(CacheLoaderCallback.class);
//        context.checking(new Expectations() {
//            {
//                
//                one(callback).getKey();
//                will(returnValue("1"));
//                one(callback).completed(null);
//            }
//        });
//        cl.loadAll(Arrays.asList(callback));
    }
}
