package org.coconut.cache.tck.service.servicemanager;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;
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

    static class Mo implements ManagedObject {
        ManagedGroup g;
        public void manage(ManagedGroup parent) {
            g = parent;
        }
    }

    static class Life extends AbstractLifecycleVerifier {}

    static class LifeMo extends AbstractLifecycleVerifier implements ManagedObject {
        ManagedGroup g;
        int state;
        public void manage(ManagedGroup parent) {
            g = parent;
            state = getState();
        }
    }
}
