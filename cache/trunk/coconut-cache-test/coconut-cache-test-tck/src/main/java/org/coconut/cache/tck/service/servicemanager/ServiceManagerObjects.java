/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import static org.coconut.test.CollectionUtils.M1;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleManagedVerifier;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleVerifier;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.junit.Test;

public class ServiceManagerObjects extends AbstractCacheTCKTest {

    @Test
    public void testUnknownService() {
        c = newCache();
        assertFalse(services().hasService(Object.class));
    }

    @Test
    public void lifecycleNoService() {
        Life l = new Life();
        Mo mo = new Mo();
        setCache(newConf().serviceManager().add(l));
        setCache(newConf().serviceManager().add(mo));
        assertFalse(services().hasService(l.getClass()));
        assertFalse(services().hasService(mo.getClass()));
    }

    @Test
    public void lifecycle() {
        Life l = new Life();
        conf.serviceManager().add(l);
        l.setConfigurationToVerify(conf);
        setCache();
        l.assertInitializedButNotStarted();
        assertFalse(services().hasService(l.getClass()));
        prestart();
        l.shutdownAndAssert(c);
    }

    @Test
    public void managedObjectManagementNotEnabled() throws InterruptedException {
        Mo l = new Mo();
        setCache(newConf().serviceManager().add(l));
        assertNull(l.g);
        prestart();
        c.shutdown();
        c.awaitTermination(5, TimeUnit.SECONDS);
        assertNull(l.g);
    }

    @Test
    public void recursiveStart() {
        setCache(conf.serviceManager().add(new Put()));
        get(M1);
    }

    static class Mo implements ManagedLifecycle {
        ManagedGroup g;

        public void manage(ManagedGroup parent) {
            g = parent;
        }
    }

    static class Life extends AbstractLifecycleVerifier {}

    static class LifeMo extends AbstractLifecycleManagedVerifier {
        ManagedGroup g;

        public void manage(ManagedGroup parent) {
            super.manage(parent);
            g = parent;
        }
    }

    static class Put extends AbstractCacheLifecycle {

        @Override
        public void started(Cache<?, ?> cache) {
            ((Cache) cache).put(M1.getKey(), M1.getValue());
        }
    }
}
