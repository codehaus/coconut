package org.coconut.cache.service.servicemanager;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class AbstraktCacheLifecycleTest {

    @Test
    public void defaultName() {
        assertEquals("MyLifecycle", new MyLifecycle().getName());
    }

    @Test(expected = NullPointerException.class)
    public void nameNPE() {
        new MyLifecycle(null);
    }

    @Test
    public void name() {
        assertEquals("foo", new MyLifecycle("foo").getName());
    }

    static class MyLifecycle extends AbstractCacheLifecycle {
        public MyLifecycle() {}

        public MyLifecycle(String name) {
            super(name);
        }
    }
}
