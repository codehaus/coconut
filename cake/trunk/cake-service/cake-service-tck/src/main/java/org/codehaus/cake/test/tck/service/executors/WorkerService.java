/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.codehaus.cake.test.tck.service.executors;

import org.codehaus.cake.service.executor.ExecutorsService;
import org.codehaus.cake.test.tck.RequireService;
import org.junit.Test;

@RequireService( { ExecutorsService.class })
public class WorkerService extends AbstractWorkerTckTest {
    
    @Test
    public void testServiceAvailable() {
       // System.out.println("run test");
        newConfigurationClean();
        newContainer();
       // assertTrue(c.hasService(ExecutorsService.class));
       // assertNotNull(c.getService(ExecutorsService.class));
    }
    
//    @Test
//    public void testServiceAvailable() {
//        c = newCache(newConf().worker().setWorkerManager(null));
//        assertNotNull(c.getService(MemoryStoreService.class));
//    }
//    @Test
//    public void shutdown() {
//        c = newCache(newConf().worker().setWorkerManager(null));
//        assertNotNull(c.getService(MemoryStoreService.class));
//        c.shutdown();
//    }
}
