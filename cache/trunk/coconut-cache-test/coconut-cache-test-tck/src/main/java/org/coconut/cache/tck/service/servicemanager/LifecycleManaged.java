package org.coconut.cache.tck.service.servicemanager;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle.Initializer;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.cache.tck.service.servicemanager.Lifecycle.ExceptionHandler;
import org.coconut.cache.tck.service.servicemanager.ServiceManagerObjects.Mo;
import org.coconut.cache.test.util.lifecycle.AbstractLifecycleManagedVerifier;
import org.junit.Before;
import org.junit.Test;

@RequireService( { CacheManagementService.class })
public class LifecycleManaged extends AbstractCacheTCKTest {
    CacheConfiguration conf;

    ExceptionHandler handler;

    @Before
    public void setup() {
        conf = newConf();
        conf.management().setEnabled(true);
        handler = new Lifecycle.ExceptionHandler();
        conf.exceptionHandling().setExceptionHandler(handler);
    }
    /**
     * Tests that {@link CacheLifecycle#initialize(Initializer)} is called
     * on a simple service.
     * 
     * @throws InterruptedException
     */
    @Test
    public void simpleManagedLifecycle() throws InterruptedException {
        AbstractLifecycleManagedVerifier alv = new AbstractLifecycleManagedVerifier();
        conf.serviceManager().add(alv);
        alv.setConfigurationToVerify(conf);
        setCache(conf);
        alv.assertInitializedButNotStarted();
        prestart();
        alv.assertInStartedPhase();
        c.shutdown();
        alv.assertShutdownOrTerminatedPhase();
        assertTrue(c.awaitTermination(10, TimeUnit.SECONDS));
        alv.assertTerminatedPhase();
    }
    
    @Test
    public void managedObject() throws InterruptedException {
        Mo l = new Mo();
        setCache(newConf().serviceManager().add(l).c().management().setEnabled(true));
        assertNull(l.g);
        prestart();
        assertNotNull(l.g);
        c.shutdown();
        c.awaitTermination(5, TimeUnit.SECONDS);
    }
}
