/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_KEY_SET;
import static org.coconut.test.CollectionUtils.M1_TO_M5_MAP;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.tck.TCKClassTester;
import org.junit.Before;
import org.junit.Test;

// @RunWith(TCKRunner.class)
@TCKClassTester(SynchronizedCache.class)
public class SynchronizedCacheTest {
    SynchronizedCache sc;

    @Before
    public void setup() throws InterruptedException {
        sc = new SynchronizedCache(M1_TO_M5_MAP);
    }

    @Test
    public void testClear() throws Throwable {
        testSync(new Runnable() {
            public void run() {
                sc.clear();
                assertEquals(0, sc.size());
            }
        });
    }

    @Test
    public void testContainsKey() throws Throwable {
        testSync(new Runnable() {
            public void run() {
                assertTrue(sc.containsKey(M1.getKey()));
            }
        });
        testSync(new Runnable() {
            public void run() {
                assertFalse(sc.containsKey(M6.getKey()));
            }
        });
    }

    @Test
    public void testContainsValue() throws Throwable {
        testSync(new Runnable() {
            public void run() {
                assertTrue(sc.containsValue(M1.getValue()));
            }
        });
        testSync(new Runnable() {
            public void run() {
                assertFalse(sc.containsKey(M6.getValue()));
            }
        });
    }

    @Test
    public void testEntrySet() throws Throwable {
        testNoSync(new Runnable() {
            public void run() {
                assertNotNull(sc.entrySet());
            }
        });
        testNoSync(new Runnable() {
            public void run() {
                Set s = sc.entrySet();
                int count = 0;
                for (Object o : s) {
                    count++;
                }
                assertEquals(5, count);
            }
        });

    }

    @Test(expected = ConcurrentModificationException.class)
    public void testEntrySetIterator() throws Throwable {
        Iterator i = sc.entrySet().iterator();
        i.next();
        testSync(new Runnable() {
            public void run() {
                Iterator i = sc.entrySet().iterator();
                i.next();
                i.remove();
            }
        });
        i.next();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testEntrySetIterator2() throws Throwable {
        Iterator i = sc.entrySet().iterator();
        i.next();
        testSync(new Runnable() {
            public void run() {
                sc.remove(M5.getKey());
            }
        });
        i.next();
    }

    @Test
    public void testEquals() throws Throwable {
        testSync(new Runnable() {
            public void run() {
                assertFalse(sc.equals(M6.getValue()));
            }
        });
        testSync(new Runnable() {
            public void run() {
                assertTrue(sc.equals(sc));
            }
        });
    }

    @Test
    public void testEvict() throws Throwable {
        testSync(new Runnable() {
            public void run() {
                sc.evict();
            }
        });
    }

    @Test
    public void testGet() throws Throwable {
        testSync(new Runnable() {
            public void run() {
                assertEquals(M1.getValue(), sc.get(M1.getKey()));
            }
        });
        testSync(new Runnable() {
            public void run() {
                assertNull(sc.get(M6.getKey()));
            }
        });
        testSync(new Runnable() {
            public void run() {
                assertNotNull(sc.getAll(M1_TO_M5_KEY_SET));
            }
        });
    }

    @Test
    public void testToString() throws Throwable {
        testSync(new Runnable() {
            public void run() {
                assertNotNull(sc.toString());
            }
        });
    }

    private Throwable cause;

    void testNoSync(final Runnable r) throws Throwable {
        final AtomicInteger ab = new AtomicInteger();
        synchronized (sc) {
            Thread t = new Thread() {
                public void run() {
                    ab.set(1);
                    try {
                        r.run();
                    } catch (Throwable t) {
                        cause = t;
                    } finally {
                        ab.set(2);
                    }
                }
            };
            t.start();
            do {
                Thread.sleep(15);
            } while (ab.get() == 0);
            assertEquals("Should not be synchronized", 2, ab.get());
        }
        if (cause != null) {
            throw cause;
        }
    }

    void testSync(final Runnable r) throws Throwable {
        final AtomicInteger ab = new AtomicInteger();
        synchronized (sc) {
            Thread t = new Thread() {
                public void run() {
                    ab.set(1);
                    try {
                        r.run();
                    } catch (Throwable t) {
                        cause = t;
                    } finally {
                        ab.set(2);
                    }
                }
            };
            t.start();
            do {
                Thread.sleep(15);
            } while (ab.get() == 0);
            assertEquals("Not properly synchronized", 1, ab.get());
        }
        do {
            Thread.sleep(15);
        } while (ab.get() != 2);
        if (cause != null) {
            throw cause;
        }
    }
}
