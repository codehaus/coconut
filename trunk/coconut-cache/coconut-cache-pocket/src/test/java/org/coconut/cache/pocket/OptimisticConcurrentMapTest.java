/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import org.coconut.cache.pocket.OptimisticConcurrentMap;
import org.coconut.cache.pocket.ValueLoader;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OptimisticConcurrentMapTest extends MockTestCase {

    public void testNullConstructorArguments1() {
        try {
            new OptimisticConcurrentMap(null);
            fail("Did not throw NullPointerException");
        } catch (NullPointerException ignore) {
        }
    }

    public void testNullConstructorArguments2() {
        try {
            new OptimisticConcurrentMap(mockDummy(ValueLoader.class), null);
            fail("Did not throw NullPointerException");
        } catch (NullPointerException ignore) {
        }
    }

    public void testGet() {
        DummyLoader dl = new DummyLoader();
        OptimisticConcurrentMap ocm = new OptimisticConcurrentMap(dl);
        ocm.put("1", 2);
        assertEquals(Integer.valueOf(2), ocm.get("1"));
        assertEquals(Integer.valueOf(3), ocm.get("3"));
    }

    public void testGetLooseRace() throws InterruptedException {
        final ValueLoaderBlocker dl = new ValueLoaderBlocker();
        final OptimisticConcurrentMapStub ocm = new OptimisticConcurrentMapStub(dl);
        Runnable r = new Runnable() {
            public void run() {
                assertEquals(Integer.valueOf(5), ocm.get("1"));
            }
        };
        Thread t=new Thread(r);
        t.start();
        dl.isWaiting.acquire();
        ocm.put("1", 5);
        dl.waitOnMe.release();
        assertEquals(Integer.valueOf(5), ocm.get("1"));
        t.join();
        assertEquals(dl, ocm.loader);
        assertEquals("1", ocm.key);
        assertEquals(Integer.valueOf(5), ocm.value);
        assertEquals(Integer.valueOf(1), ocm.discardedValue);
    }

    public void testDelegateMethods() throws Exception {
        Mock m = mock(ConcurrentMap.class);
        OptimisticConcurrentMap cm = new OptimisticConcurrentMap(
                mockDummy(ValueLoader.class), (ConcurrentMap) m.proxy());
        delegateTest(cm, m, "clear", "containsKey", "containsValue", "entrySet",
                "hashCode", "isEmpty", "keySet", "putAll", "size", "values", "replace",
                "equals", "toString", "putIfAbsent", "remove", "replace", "put",
                "putIfAbsent");
    }

    static class ValueLoaderBlocker extends DummyLoader {

        final Semaphore waitOnMe = new Semaphore(0);

        final Semaphore isWaiting = new Semaphore(0);

        @Override
        public Integer load(String key) {
            isWaiting.release();
            try {
                waitOnMe.acquire();
            } catch (InterruptedException e) {
                throw new Error(e);
            }
            return super.load(key);
        }

    }

    static class OptimisticConcurrentMapStub extends
            OptimisticConcurrentMap<String, Integer> {
        ValueLoader<String, Integer> loader;

        String key;

        Integer value;

        Integer discardedValue;

        @Override
        protected void undoNewValue(ValueLoader<String, Integer> loader,
                String key, Integer value, Integer discardedValue) {
            this.loader = loader;
            this.key = key;
            this.value = value;
            this.discardedValue = discardedValue;
            super.undoNewValue(loader, key, value, discardedValue);
        }

        /**
         * @param loader
         */
        public OptimisticConcurrentMapStub(ValueLoader<String, Integer> loader) {
            super(loader);
        }

    }

}
