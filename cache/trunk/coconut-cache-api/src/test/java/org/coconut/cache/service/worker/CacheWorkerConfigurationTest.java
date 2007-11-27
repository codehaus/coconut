/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.worker;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.attribute.AttributeMap;
import org.junit.Before;
import org.junit.Test;

public class CacheWorkerConfigurationTest {

    static CacheWorkerConfiguration DEFAULT = new CacheWorkerConfiguration();

    private CacheWorkerConfiguration conf;

    private static final CacheWorkerManager M = new LoadableCacheWorkerManager();

    @Before
    public void setUp() {
        conf = new CacheWorkerConfiguration();
    }

    @Test
    public void testInitial() {
        assertEquals(CacheWorkerConfiguration.SERVICE_NAME, conf.getServiceName());
    }

    /**
     * Test CacheWorkerManager.
     */
    @Test
    public void testCacheWorkerManager() {
        // initial values
        assertNull(conf.getWorkerManager());
        assertEquals(conf, conf.setWorkerManager(M));
        assertSame(M, conf.getWorkerManager());

    }

    @Test
    public void testCacheWorkerManagerXML() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getWorkerManager());
        assertEquals(conf, conf.setWorkerManager(M));
        conf = reloadService(conf);
        assertTrue(conf.getWorkerManager() instanceof LoadableCacheWorkerManager);
        
        conf.setWorkerManager(new NonLoadableCacheWorkerManager());
        conf = reloadService(conf);
        assertNull(conf.getWorkerManager());
    }

    public static class LoadableCacheWorkerManager extends CacheWorkerManager {
        
        public ExecutorService getExecutorService(Object service,
                AttributeMap attributes) {
            return null;
        }

        
        public ScheduledExecutorService getScheduledExecutorService(Object service,
                AttributeMap attributes) {
            return null;
        }
    }

    public class NonLoadableCacheWorkerManager extends CacheWorkerManager {
        
        public ExecutorService getExecutorService(Object service,
                AttributeMap attributes) {
            return null;
        }

        
        public ScheduledExecutorService getScheduledExecutorService(Object service,
                AttributeMap attributes) {
            return null;
        }
    }
}
