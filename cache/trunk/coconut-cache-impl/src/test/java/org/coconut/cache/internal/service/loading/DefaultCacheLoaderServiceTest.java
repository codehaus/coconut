/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.coconut.cache.internal.service.threading.InternalCacheThreadingService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.internal.spi.ExtendedExecutorRunnable;
import org.coconut.cache.internal.spi.ExtendedExecutorRunnable.LoadKey;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.threading.CacheServiceThreadManage;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.jmock.core.Constraint;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheLoaderServiceTest extends MockTestCase {

    private final static AttributeMap attributes = mockDummy(AttributeMap.class);

    private final static Collection<Integer> keys = mockDummy(Collection.class);

    private final static Map<Integer, AttributeMap> keys1 = mockDummy(Map.class);

    private final static Map<Integer, AttributeMap> keys2 = mockDummy(Map.class);

    private final static Map<Integer, AttributeMap> realKeys = new HashMap() {
        {
            put(3, new AttributeMaps.DefaultAttributeMap());
            put(5, new AttributeMaps.DefaultAttributeMap());
        }
    };

    private final static Map<Integer, String> map2 = new HashMap<Integer, String>();

    private final static Map<Integer, String> map3 = new HashMap<Integer, String>();

    private final static Exception e = new Exception();

    private final static RuntimeException re = new RuntimeException();

    MyExecutor myExecutor;

    Mock loaderMock1;

    CacheLoader<Integer, String> loaderProxy1;

    Mock loaderMock2;

    CacheLoader<Integer, String> loaderProxy2;

    Mock errorHandlerMock;

    AbstractCacheExceptionHandler<Integer, String> errorHandlerProxy;

    Mock cacheHelperMock;

    CacheHelper<Integer, String> cacheHelperProxy;

    DefaultCacheLoaderService<Integer, String> service;

    CacheLoadingConfiguration<Integer, String> loadingConf;
    public void setUp() {
        loaderMock1 = mock(CacheLoader.class);
        loaderProxy1 = (CacheLoader) loaderMock1.proxy();
        loaderMock2 = mock(CacheLoader.class);
        loaderProxy2 = (CacheLoader) loaderMock2.proxy();

        errorHandlerMock = mock(AbstractCacheExceptionHandler.class);
        errorHandlerProxy = CacheExceptionHandlers.defaultExceptionHandler();

        cacheHelperMock = mock(CacheHelper.class);
        cacheHelperProxy = (CacheHelper) cacheHelperMock.proxy();
        myExecutor = new MyExecutor();
        loadingConf=new CacheLoadingConfiguration<Integer, String> ();
        loadingConf.setLoader(loaderProxy1);
    }

    public void testIgnore() {
        
    }
    public void atestLoadBlocking() {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, null, loadingConf, null, null, null);

        loaderMock1.expects(once()).method("load").with(eq(3), same(attributes)).will(
                returnValue("boo"));

        assertEquals("boo", dcls.loadBlocking(3, attributes));

        loaderMock2.expects(once()).method("load").with(eq(5), same(attributes)).will(
                returnValue("foo"));
       // assertEquals("foo", dcls.loadBlocking(loaderProxy2, 5, attributes));
    }

    public void atestLoadBlockingError() {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, errorHandlerProxy, loadingConf, null, null, null);

        loaderMock1.expects(once()).method("load").with(eq(4), same(attributes)).will(
                throwException(e));
        errorHandlerMock.expects(once()).method("loadFailed").with(
                new Constraint[] { same(loaderProxy1), eq(4), same(attributes),
                        eq(false), same(e) }).will(returnValue("voo"));
        assertEquals("voo", dcls.loadBlocking(4, attributes));
    }

    public void atestLoadAllBlocking() {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, null, loadingConf, null, null, null);

        loaderMock1.expects(once()).method("loadAll").with(eq(keys1)).will(
                returnValue(map2));
        assertSame(map2, dcls.loadAllBlocking(keys1));

        loaderMock2.expects(once()).method("loadAll").with(eq(keys2)).will(
                returnValue(map2));
        assertEquals(map2, dcls.loadAllBlocking(loaderProxy2, keys2));
    }

    public void atestLoadAllBlockingError() {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, errorHandlerProxy, loadingConf, null, null, null);

        loaderMock1.expects(once()).method("loadAll").with(eq(keys1)).will(
                throwException(e));

        errorHandlerMock.expects(once()).method("loadAllFailed").with(
                new Constraint[] { same(loaderProxy1), same(keys1), eq(false), same(e) })
                .will(returnValue(map2));
        assertEquals(map2, dcls.loadAllBlocking(keys1));
    }

    public void atestLoad() throws InterruptedException, ExecutionException {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, errorHandlerProxy, loadingConf, myExecutor, null,
                cacheHelperProxy);

        loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
                returnValue("boo"));
        cacheHelperMock.expects(once()).method("valueLoaded").with(eq(1), eq("boo"),
                ANYTHING);
        Future f = dcls.load(1);
        assertNotNull(f);
        f.get();

        loaderMock1.expects(once()).method("load").with(eq(2), same(attributes)).will(
                returnValue("foo"));
        cacheHelperMock.expects(once()).method("valueLoaded").with(eq(2), eq("foo"),
                same(attributes));
        Future f1 = dcls.load(2, attributes);
        assertNotNull(f1);
        f1.get();

        System.out.println(myExecutor.r.getClass());
        assertTrue(myExecutor.r instanceof ExtendedExecutorRunnable.LoadKey);
        ExtendedExecutorRunnable.LoadKey loadKey = (LoadKey) myExecutor.r;
        assertEquals(2, loadKey.getKey());
        assertSame(attributes, loadKey.getAttributes());
    }

    public void atestLoadError() throws InterruptedException, ExecutionException {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, errorHandlerProxy, loadingConf, myExecutor, null,
                cacheHelperProxy);

        loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
                throwException(e));
        cacheHelperMock.expects(once()).method("valueLoaded").with(eq(1), eq("boo"),
                ANYTHING);
        errorHandlerMock.expects(once()).method("loadFailed").with(
                new Constraint[] { same(loaderProxy1), eq(1), same(attributes), eq(true),
                        same(e) }).will(returnValue("boo"));

        Future f = dcls.load(1, attributes);
        assertNotNull(f);
        f.get();

        // no add of value if cacheerrorhandler returns null
        loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
                throwException(e));
        errorHandlerMock.expects(once()).method("loadFailed").with(
                new Constraint[] { same(loaderProxy1), eq(1), same(attributes), eq(true),
                        same(e) }).will(returnValue(null));

        Future f1 = dcls.load(1, attributes);
        assertNotNull(f1);
        f1.get();

        // test cache errorhandler throws CacheException
        loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
                throwException(e));
        errorHandlerMock.expects(once()).method("loadFailed").with(
                new Constraint[] { same(loaderProxy1), eq(1), same(attributes), eq(true),
                        same(e) }).will(throwException(re));

        Future f2 = dcls.load(1, attributes);
        assertNotNull(f2);
        try {
            f2.get();
            fail("Should fail with ExecutionException");
        } catch (ExecutionException e) {
            assertSame(re, e.getCause());
        }
    }

    //tests has been removed because CacheLoader does not define a loadAll method anymore
    public void aloadAll() throws InterruptedException, ExecutionException {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, errorHandlerProxy,loadingConf, myExecutor, null,
                cacheHelperProxy);
        // result null
        loaderMock1.expects(once()).method("loadAll").with(same(keys1)).will(
                returnValue(null));
        Future f = dcls.loadAll(keys1);
        assertNotNull(f);
        f.get();

        // result empty
        loaderMock1.expects(once()).method("loadAll").with(same(keys1)).will(
                returnValue(map2));
        Future f1 = dcls.loadAll(keys1);
        assertNotNull(f1);
        f1.get();

        map2.put(1, "foo");
        loaderMock1.expects(once()).method("loadAll").with(same(keys1)).will(
                returnValue(map2));
        cacheHelperMock.expects(once()).method("valuesLoaded").with(same(map2),
                same(keys1));
        Future f2 = dcls.loadAll(keys1);
        assertNotNull(f2);
        f2.get();
    }

    public void atestLoadAllError() throws InterruptedException, ExecutionException {
        DefaultCacheLoaderService<Integer, String> dcls = new DefaultCacheLoaderService<Integer, String>(
                null, null, errorHandlerProxy, loadingConf, myExecutor, null,
                cacheHelperProxy);

        loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
                throwException(e));
        cacheHelperMock.expects(once()).method("valuesLoaded").with(same(map2),
                eq(realKeys));
        errorHandlerMock.expects(once()).method("loadAllFailed").with(
                new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
                .will(returnValue(map2));

        Future f = dcls.loadAll(realKeys);
        assertNotNull(f);
        f.get();

        // no add of value if cacheerrorhandler returns null
        loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
                throwException(e));
        errorHandlerMock.expects(once()).method("loadAllFailed").with(
                new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
                .will(returnValue(null));

        Future f1 = dcls.loadAll(realKeys);
        assertNotNull(f1);
        f1.get();

        // no add of value if map size==0
        loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
                throwException(e));
        errorHandlerMock.expects(once()).method("loadAllFailed").with(
                new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
                .will(returnValue(new HashMap()));

        Future f2 = dcls.loadAll(realKeys);
        assertNotNull(f2);
        f2.get();

        // test cache errorhandler throws CacheException
        loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
                throwException(e));
        errorHandlerMock.expects(once()).method("loadAllFailed").with(
                new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
                .will(throwException(re));
        Future f3 = dcls.loadAll(realKeys);
        assertNotNull(f2);
        try {
            f3.get();
            fail("Should fail with ExecutionException");
        } catch (ExecutionException e) {
            assertSame(re, e.getCause());
        }
    }

    static class MyExecutor implements InternalCacheThreadingService {
        Runnable r;

        /**
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) {
            this.r = command;
            r.run();
        }

        /**
         * @see org.coconut.cache.internal.service.threading.InternalCacheThreadingService#isActive()
         */
        public boolean isActive() {
            throw new UnsupportedOperationException();
        }

        public CacheServiceThreadManage getExecutor(Class<?> service) {
            throw new UnsupportedOperationException();
        }

    }
}
