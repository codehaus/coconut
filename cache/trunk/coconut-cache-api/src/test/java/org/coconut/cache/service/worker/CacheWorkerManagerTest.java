/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.worker;

import static junit.framework.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.attribute.AttributeMap;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class CacheWorkerManagerTest {

    private Object s1 = String.class;

    private Object s2 = Integer.class;

    private ExecutorService es = TestUtil.dummy(ExecutorService.class);

    private ScheduledExecutorService ses = TestUtil.dummy(ScheduledExecutorService.class);

    @Test
    public void name() {
        // Manager m = new Manager();
        // assertSame(es, m.getExecutorService(s1));
        // assertSame(ses, m.getScheduledExecutorService(s2));
    }

    class Manager extends CacheWorkerManager {

        public ExecutorService getExecutorService(Object service, AttributeMap attributes) {
            s1 = service;
            assertEquals(0, attributes.size());
            return es;
        }

        public ScheduledExecutorService getScheduledExecutorService(Object service,
                AttributeMap attributes) {
            s2 = service;
            assertEquals(0, attributes.size());
            return ses;
        }

    }
}
