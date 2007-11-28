package org.coconut.cache.tck.service.servicemanager;

import java.util.concurrent.TimeUnit;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.test.MockTestCase;
import org.junit.Test;

@RequireService({ NotThreadSafe.class })
public class LifecycleAsynchronousShutdownNoSupport extends AbstractCacheTCKTest {
    @Test
    public void noSupport() {
        setCache(newConf().serviceManager().add(new AbstractCacheLifecycle() {
            private volatile CacheServiceManagerService services;

            @Override
            public void shutdown() {
                try {
                    services.shutdownServiceAsynchronously(MockTestCase
                            .mockDummy(AsynchronousShutdownObject.class));
                    throw new AssertionError("should throw");
                } catch (UnsupportedOperationException ok) {}
            }

            @Override
            public void started(Cache<?, ?> cache) {
                services = CacheServices.servicemanager(cache);
            }
        }));

        prestart();
        shutdownAndAwaitTermination();
    }
}
