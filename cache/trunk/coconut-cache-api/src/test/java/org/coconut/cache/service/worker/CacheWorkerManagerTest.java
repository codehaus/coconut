/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.worker;

import static junit.framework.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.core.AttributeMap;
import org.coconut.test.MockTestCase;
import org.junit.Test;

public class CacheWorkerManagerTest {

    private Class s1 = String.class;

    private Class s2 = Integer.class;

    private ExecutorService es = MockTestCase.mockDummy(ExecutorService.class);

    private ScheduledExecutorService ses = MockTestCase
            .mockDummy(ScheduledExecutorService.class);

    @Test
    public void test() {
     //   Manager m = new Manager();
       // assertSame(es, m.getExecutorService(s1));
       // assertSame(ses, m.getScheduledExecutorService(s2));
    }

    class Manager extends CacheWorkerManager {

        
        public ExecutorService getExecutorService(Class<?> service,
                AttributeMap attributes) {
            s1 = service;
            assertEquals(0, attributes.size());
            return es;
        }

        
        public ScheduledExecutorService getScheduledExecutorService(Class<?> service,
                AttributeMap attributes) {
            s2 = service;
            assertEquals(0, attributes.size());
            return ses;
        }

    }
}
