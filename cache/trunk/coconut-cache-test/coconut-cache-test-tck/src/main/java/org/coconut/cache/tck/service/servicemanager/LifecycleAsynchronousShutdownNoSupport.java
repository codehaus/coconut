/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.concurrent.Callable;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.test.TestUtil;
import org.junit.Test;

@RequireService({ NotThreadSafe.class })
public class LifecycleAsynchronousShutdownNoSupport extends AbstractCacheTCKTest {
    @Test
    public void noSupport() {
        init(newConf().serviceManager().add(new AbstractCacheLifecycle() {
            private volatile CacheServiceManagerService services;

            @Override
            public void shutdown(Shutdown shutdown) {
                try {
                    shutdown.shutdownAsynchronously(TestUtil
                            .dummy(Callable.class));
                    throw new AssertionError("should throw");
                } catch (UnsupportedOperationException ok) {}
            }

            @Override
            public void started(Cache<?, ?> cache) {
                services = cache.services().servicemanager();
            }
        }));

        prestart();
        shutdownAndAwaitTermination();
    }
}
