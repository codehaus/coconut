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

        @Override
        public ExecutorService getExecutorService(Class<?> service,
                AttributeMap attributes) {
            s1 = service;
            assertEquals(0, attributes.size());
            return es;
        }

        @Override
        public ScheduledExecutorService getScheduledExecutorService(Class<?> service,
                AttributeMap attributes) {
            s2 = service;
            assertEquals(0, attributes.size());
            return ses;
        }

    }
}
