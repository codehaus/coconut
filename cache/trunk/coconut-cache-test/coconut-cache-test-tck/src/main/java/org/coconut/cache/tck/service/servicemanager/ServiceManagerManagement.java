package org.coconut.cache.tck.service.servicemanager;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.cache.tck.service.servicemanager.ServiceManagerObjects.LifeMo;
import org.coconut.cache.tck.service.servicemanager.ServiceManagerObjects.Mo;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.junit.Test;

@RequireService( { CacheManagementService.class })
public class ServiceManagerManagement extends AbstractCacheTCKTest {

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
    public void lifecycleAndManagedObject() {
        LifeMo l = new LifeMo();
        setCache(newConf().serviceManager().add(l).c().management().setEnabled(true));
        assertNull(l.g);
        l.assertInitializedButNotStarted();
        assertFalse(services().hasService(l.getClass()));
        prestart();
        l.shutdownAndAssert(c);
        assertEquals(AbstractLifecycleVerifier.Step.START, l.state);
        assertNotNull(l.g);
    }

}
