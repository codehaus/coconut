/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.internal.service.worker.InternalCacheWorkerService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.test.MockTestCase;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings( { "unchecked", "serial" })
public class DefaultCacheLoaderServiceTest {

    private final static AttributeMap ATTRIBUTES = MockTestCase.mockDummy(AttributeMap.class);

    private final static Map<Integer, AttributeMap> KEYS1 = MockTestCase.mockDummy(Map.class);

    private final static Map<Integer, AttributeMap> KEYS2 = MockTestCase.mockDummy(Map.class);

    private final static Map<Integer, AttributeMap> REAL_KEYS = new HashMap() {
        {
            put(3, new AttributeMaps.DefaultAttributeMap());
            put(5, new AttributeMaps.DefaultAttributeMap());
        }
    };

    private final static Map<Integer, String> MAP2 = new HashMap<Integer, String>();

    private final static Map<Integer, String> MAP3 = new HashMap<Integer, String>();

    private final static Exception E = new Exception();

    private final static RuntimeException RE = new RuntimeException();

    MyExecutor myExecutor;

// Mock loaderMock1;
//
// CacheLoader<Integer, String> loaderProxy1;
//
// Mock loaderMock2;
//
// CacheLoader<Integer, String> loaderProxy2;
//
// Mock errorHandlerMock;
//
// InternalCacheExceptionService<Integer, String> errorHandlerProxy;
//
// Mock cacheHelperMock;

    InternalCacheSupport<Integer, String> cacheHelperProxy;

    CacheLoadingService<Integer, String> service;

    CacheLoadingConfiguration<Integer, String> loadingConf;

    public void setUp() {
// loaderMock1 = mock(CacheLoader.class);
// loaderProxy1 = (CacheLoader) loaderMock1.proxy();
// loaderMock2 = mock(CacheLoader.class);
// loaderProxy2 = (CacheLoader) loaderMock2.proxy();
//
// errorHandlerMock = mock(InternalCacheExceptionService.class);
// errorHandlerProxy = (InternalCacheExceptionService) errorHandlerMock.proxy();
//
// cacheHelperMock = mock(InternalCacheSupport.class);
// cacheHelperProxy = (InternalCacheSupport) cacheHelperMock.proxy();
// myExecutor = new MyExecutor();
// loadingConf=new CacheLoadingConfiguration<Integer, String> ();
// loadingConf.setLoader(loaderProxy1);
    }

    @Test
    public void testIgnore() {

    }

// public void atestLoadBlocking() {
// DefaultCacheLoaderService<Integer, String> dcls = new
// DefaultCacheLoaderService<Integer, String>(
// null, null, null, loadingConf, null, null);
//
// loaderMock1.expects(once()).method("load").with(eq(3), same(ATTRIBUTES)).will(
// returnValue("boo"));
//
// assertEquals("boo", dcls.loadBlocking(3, ATTRIBUTES));
//
// loaderMock2.expects(once()).method("load").with(eq(5), same(ATTRIBUTES)).will(
// returnValue("foo"));
// // assertEquals("foo", dcls.loadBlocking(loaderProxy2, 5, attributes));
// }
//
// public void atestLoadBlockingError() {
// DefaultCacheLoaderService<Integer, String> dcls = new
// DefaultCacheLoaderService<Integer, String>(
// null, null, errorHandlerProxy, loadingConf, null, null);
//
// loaderMock1.expects(once()).method("load").with(eq(4), same(ATTRIBUTES)).will(
// throwException(E));
// errorHandlerMock.expects(once()).method("loadFailed").with(
// new Constraint[] { same(loaderProxy1), eq(4), same(ATTRIBUTES),
// eq(false), same(E) }).will(returnValue("voo"));
// assertEquals("voo", dcls.loadBlocking(4, ATTRIBUTES));
// }

// public void atestLoadAllBlocking() {
// DefaultCacheLoaderService<Integer, String> dcls = new
// DefaultCacheLoaderService<Integer, String>(
// null, null, null, loadingConf, null, null);
//
// loaderMock1.expects(once()).method("loadAll").with(eq(KEYS1)).will(
// returnValue(MAP2));
// assertSame(MAP2, dcls.loadAllBlocking(KEYS1));
//
// loaderMock2.expects(once()).method("loadAll").with(eq(KEYS2)).will(
// returnValue(MAP2));
// assertEquals(MAP2, dcls.loadAllBlocking(loaderProxy2, KEYS2));
// }
//
// public void atestLoadAllBlockingError() {
// DefaultCacheLoaderService<Integer, String> dcls = new
// DefaultCacheLoaderService<Integer, String>(
// null, null, errorHandlerProxy, loadingConf, null, null);
//
// loaderMock1.expects(once()).method("loadAll").with(eq(KEYS1)).will(
// throwException(E));
//
// errorHandlerMock.expects(once()).method("loadAllFailed").with(
// new Constraint[] { same(loaderProxy1), same(KEYS1), eq(false), same(E) })
// .will(returnValue(MAP2));
// assertEquals(MAP2, dcls.loadAllBlocking(KEYS1));
// }

    public void atestLoad() throws InterruptedException, ExecutionException {
// CacheLoadingService<Integer, String> dcls = new SynchronizedCacheLoaderService<Integer,
// String>(
// null, errorHandlerProxy, loadingConf, myExecutor,
// cacheHelperProxy);
//
// loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
// returnValue("boo"));
// cacheHelperMock.expects(once()).method("valueLoaded").with(eq(1), eq("boo"),
// ANYTHING);
// // Future f = dcls.load(1);
// // assertNotNull(f);
// // f.get();
//
// loaderMock1.expects(once()).method("load").with(eq(2), same(ATTRIBUTES)).will(
// returnValue("foo"));
// cacheHelperMock.expects(once()).method("valueLoaded").with(eq(2), eq("foo"),
// same(ATTRIBUTES));
// Future f1 = dcls.load(2, attributes);
// assertNotNull(f1);
// f1.get();

// assertTrue(myExecutor.r instanceof ExtendedExecutorRunnable.LoadKey);
// ExtendedExecutorRunnable.LoadKey loadKey = (LoadKey) myExecutor.r;
// assertEquals(2, loadKey.getKey());
// assertSame(attributes, loadKey.getAttributes());
    }

    public void atestLoadError() throws InterruptedException, ExecutionException {
// CacheLoadingService<Integer, String> dcls = new SynchronizedCacheLoaderService<Integer,
// String>(
// null, errorHandlerProxy, loadingConf, myExecutor,
// cacheHelperProxy);
//
// loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
// throwException(E));
// cacheHelperMock.expects(once()).method("valueLoaded").with(eq(1), eq("boo"),
// ANYTHING);
// errorHandlerMock.expects(once()).method("loadFailed").with(
// new Constraint[] { same(loaderProxy1), eq(1), same(ATTRIBUTES), eq(true),
// same(E) }).will(returnValue("boo"));
//
// // Future f = dcls.load(1, attributes);
// // assertNotNull(f);
// // f.get();
//
// // no add of value if cacheerrorhandler returns null
// loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
// throwException(E));
// errorHandlerMock.expects(once()).method("loadFailed").with(
// new Constraint[] { same(loaderProxy1), eq(1), same(ATTRIBUTES), eq(true),
// same(E) }).will(returnValue(null));
//
// // Future f1 = dcls.load(1, attributes);
// // assertNotNull(f1);
// // f1.get();
//
// // test cache errorhandler throws CacheException
// loaderMock1.expects(once()).method("load").with(eq(1), ANYTHING).will(
// throwException(E));
// errorHandlerMock.expects(once()).method("loadFailed").with(
// new Constraint[] { same(loaderProxy1), eq(1), same(ATTRIBUTES), eq(true),
// same(E) }).will(throwException(RE));

// Future f2 = dcls.load(1, attributes);
// assertNotNull(f2);
// try {
// f2.get();
// fail("Should fail with ExecutionException");
// } catch (ExecutionException e) {
// assertSame(re, e.getCause());
// }
    }

// //tests has been removed because CacheLoader does not define a loadAll method anymore
// public void aloadAll() throws InterruptedException, ExecutionException {
// DefaultCacheLoaderService<Integer, String> dcls = new
// DefaultCacheLoaderService<Integer, String>(
// null, null, errorHandlerProxy,loadingConf, myExecutor, null,
// cacheHelperProxy);
// // result null
// loaderMock1.expects(once()).method("loadAll").with(same(keys1)).will(
// returnValue(null));
// Future f = dcls.loadAll(keys1);
// assertNotNull(f);
// f.get();
//
// // result empty
// loaderMock1.expects(once()).method("loadAll").with(same(keys1)).will(
// returnValue(map2));
// Future f1 = dcls.loadAll(keys1);
// assertNotNull(f1);
// f1.get();
//
// map2.put(1, "foo");
// loaderMock1.expects(once()).method("loadAll").with(same(keys1)).will(
// returnValue(map2));
// cacheHelperMock.expects(once()).method("valuesLoaded").with(same(map2),
// same(keys1));
// Future f2 = dcls.loadAll(keys1);
// assertNotNull(f2);
// f2.get();
// }
//
// public void atestLoadAllError() throws InterruptedException, ExecutionException {
// DefaultCacheLoaderService<Integer, String> dcls = new
// DefaultCacheLoaderService<Integer, String>(
// null, null, errorHandlerProxy, loadingConf, myExecutor, null,
// cacheHelperProxy);
//
// loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
// throwException(e));
// cacheHelperMock.expects(once()).method("valuesLoaded").with(same(map2),
// eq(realKeys));
// errorHandlerMock.expects(once()).method("loadAllFailed").with(
// new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
// .will(returnValue(map2));
//
// Future f = dcls.loadAll(realKeys);
// assertNotNull(f);
// f.get();
//
// // no add of value if cacheerrorhandler returns null
// loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
// throwException(e));
// errorHandlerMock.expects(once()).method("loadAllFailed").with(
// new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
// .will(returnValue(null));
//
// Future f1 = dcls.loadAll(realKeys);
// assertNotNull(f1);
// f1.get();
//
// // no add of value if map size==0
// loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
// throwException(e));
// errorHandlerMock.expects(once()).method("loadAllFailed").with(
// new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
// .will(returnValue(new HashMap()));
//
// Future f2 = dcls.loadAll(realKeys);
// assertNotNull(f2);
// f2.get();
//
// // test cache errorhandler throws CacheException
// loaderMock1.expects(once()).method("loadAll").with(eq(realKeys)).will(
// throwException(e));
// errorHandlerMock.expects(once()).method("loadAllFailed").with(
// new Constraint[] { same(loaderProxy1), eq(realKeys), eq(true), same(e) })
// .will(throwException(re));
// Future f3 = dcls.loadAll(realKeys);
// assertNotNull(f2);
// try {
// f3.get();
// fail("Should fail with ExecutionException");
// } catch (ExecutionException e) {
// assertSame(re, e.getCause());
// }
// }

    static class MyExecutor implements InternalCacheWorkerService {
        Runnable r;

        /**
         * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
         */
        public void execute(Runnable command) {
            this.r = command;
            r.run();
        }

        /**
         * @see org.coconut.cache.internal.service.threading.InternalCacheThreadingService#isValid()
         */
        public boolean isActive() {
            throw new UnsupportedOperationException();
        }

        public CacheWorkerManager getManager() {
            return new CacheWorkerManager(){
                
                public ExecutorService getExecutorService(Object service,
                        AttributeMap attributes) {
                    return MockTestCase.mockDummy(ExecutorService.class);
                }
                
                public ScheduledExecutorService getScheduledExecutorService(
                        Object service, AttributeMap attributes) {
                    return MockTestCase.mockDummy(ScheduledExecutorService.class);
                }};
        }

        public ExecutorService getExecutorService(Class<?> service) {
            return MockTestCase.mockDummy(ExecutorService.class);
        }

        public ScheduledExecutorService getScheduledExecutorService(Class<?> service) {
            return MockTestCase.mockDummy(ScheduledExecutorService.class);
        }

    }
}
