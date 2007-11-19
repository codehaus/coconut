/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import java.util.concurrent.TimeUnit;
import static org.coconut.test.CollectionUtils.M1;
import org.coconut.cache.Cache;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
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
        LifeMo lm = new LifeMo();
        setCache(newConf().serviceManager().add(l));
        setCache(newConf().serviceManager().add(mo));
        setCache(newConf().serviceManager().add(lm));
        assertFalse(services().hasService(l.getClass()));
        assertFalse(services().hasService(mo.getClass()));
        assertFalse(services().hasService(lm.getClass()));
    }

    @Test
    public void lifecycle() {
        Life l = new Life();
        setCache(newConf().serviceManager().add(l));
        l.assertNotStarted();
        assertFalse(services().hasService(l.getClass()));
        prestart();
        l.shutdownAndAssert(c);
    }

    @Test
    public void managedObject() throws InterruptedException {
        Mo l = new Mo();
        setCache(newConf().serviceManager().add(l).c().management().setEnabled(true));
        assertNull(l.g);
        prestart();
        c.shutdown();
        c.awaitTermination(5, TimeUnit.SECONDS);
        assertNotNull(l.g);
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
    public void lifecycleAndManagedObject() {
        LifeMo l = new LifeMo();
        setCache(newConf().serviceManager().add(l).c().management().setEnabled(true));
        assertNull(l.g);
        l.assertNotStarted();
        assertFalse(services().hasService(l.getClass()));
        prestart();
        l.shutdownAndAssert(c);
        assertEquals(3, l.state);
        assertNotNull(l.g);
    }

    @Test
    public void recursiveStart() {
        setCache(newConf().serviceManager().add(new Put()));
        get(M1);
    }
    static class Mo implements ManagedLifecycle {
        ManagedGroup g;

        public void manage(ManagedGroup parent) {
            g = parent;
        }
    }

    static class Life extends AbstractLifecycleVerifier {}

    static class LifeMo extends AbstractLifecycleVerifier implements ManagedLifecycle {
        ManagedGroup g;

        int state;

        public void manage(ManagedGroup parent) {
            g = parent;
            state = getState();
        }
    }

    static class Put extends AbstractCacheLifecycle {

        @Override
        public void started(Cache<?, ?> cache) {
            ((Cache) cache).put(M1.getKey(), M1.getValue());
        }
    }
}
