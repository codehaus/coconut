/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.test.MockTestCase;
import org.junit.Test;

public class LifecycleStart extends AbstractCacheTCKTest {

    @Test
    public void serviceManagerAvailable() {
        setCache(newConf().serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                assertTrue(serviceManager.hasService(CacheServiceManagerService.class));
                assertNotNull(serviceManager.getService(CacheServiceManagerService.class));
                assertTrue(serviceManager.getAllServices().containsKey(
                        CacheServiceManagerService.class));
                assertNotNull(serviceManager.getAllServices().get(CacheServiceManagerService.class));
            }
        }));
        prestart();
    }

    @Test
    public void cannotCallshutdownServiceAsynchronously() {
        setCache(newConf().serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService serviceManager) {
                if (getCacheType().getAnnotation(ThreadSafe.class) != null) {
                    try {
                        serviceManager.shutdownServiceAsynchronously(MockTestCase
                                .mockDummy(AsynchronousShutdownObject.class));
                        throw new AssertionError(
                                "serviceManager should throw IllegalStateException");
                    } catch (IllegalStateException ok) {/* ok */}
                } else {
                    try {
                        serviceManager.shutdownServiceAsynchronously(MockTestCase
                                .mockDummy(AsynchronousShutdownObject.class));
                        throw new AssertionError(
                                "serviceManager should throw UnsupportedOperationException");
                    } catch (UnsupportedOperationException ok) {/* ok */}
                }
            }
        }));
        prestart();
    }
}
