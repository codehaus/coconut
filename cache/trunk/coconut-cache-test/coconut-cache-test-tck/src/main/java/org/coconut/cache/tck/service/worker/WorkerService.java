/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.worker;

import org.coconut.cache.service.eviction.CacheEvictionService;
import org.coconut.cache.service.worker.CacheWorkerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.junit.Test;

@RequireService( { CacheWorkerService.class })
public class WorkerService extends AbstractCacheTCKTest {
    @Test
    public void testServiceAvailable() {
        c = newCache(newConf().worker().setWorkerManager(null));
        assertNotNull(c.getService(CacheEvictionService.class));
    }
    @Test
    public void shutdown() {
        c = newCache(newConf().worker().setWorkerManager(null));
        assertNotNull(c.getService(CacheEvictionService.class));
        c.shutdown();
    }
}
