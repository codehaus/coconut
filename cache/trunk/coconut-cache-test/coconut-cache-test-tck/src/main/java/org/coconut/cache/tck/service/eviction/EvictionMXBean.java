/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.eviction;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.RuntimeMBeanException;

import org.coconut.attribute.common.SizeAttribute;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.memorystore.MemoryStoreConfiguration;
import org.coconut.cache.service.memorystore.MemoryStoreMXBean;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.cache.test.TestCacheLoader;
import org.junit.Before;
import org.junit.Test;

@RequireService( { CacheManagementService.class })
public class EvictionMXBean extends AbstractCacheTCKTest {

    static MemoryStoreConfiguration<?, ?> DEFAULT = new MemoryStoreConfiguration();

    MemoryStoreMXBean mxBean;

    MBeanServer mbs;

    // TODO
    // We should test for a default objectname
    // a.la. for cache named foo
    // ObjName=org.coconut.cache:name=343434, service=General

    @Before
    public void setup() {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().loading().setLoader(new TestCacheLoader(SizeAttribute.INSTANCE)).c()
                .management().setEnabled(true).setMBeanServer(mbs).c());
        mxBean = findMXBean(mbs, MemoryStoreMXBean.class);
    }

    /**
     * Tests maximum capacity.
     */
    @Test
    public void maximumCapacity() {
        assertEquals(Long.MAX_VALUE, mxBean.getMaximumVolume());
        mxBean.setMaximumVolume(1000);
        assertEquals(1000, mxBean.getMaximumVolume());
        assertEquals(1000, eviction().getMaximumVolume());

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true).setMBeanServer(mbs).c()
                .eviction().setMaximumVolume(5000));
        mxBean = findMXBean(mbs, MemoryStoreMXBean.class);
        assertEquals(5000, mxBean.getMaximumVolume());

        // Exception
        try {
            mxBean.setMaximumVolume(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {} catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /**
     * Tests maximum size.
     */
    @Test
    public void maximumSize() {
        assertEquals(Integer.MAX_VALUE, mxBean.getMaximumSize());
        mxBean.setMaximumSize(1000);
        assertEquals(1000, mxBean.getMaximumSize());
        assertEquals(1000, eviction().getMaximumSize());

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true).setMBeanServer(mbs).c()
                .eviction().setMaximumSize(5000));
        mxBean = findMXBean(mbs, MemoryStoreMXBean.class);
        assertEquals(5000, mxBean.getMaximumSize());

        // Exception
        try {
            mxBean.setMaximumSize(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {/* ok */} catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /**
     * Tests trimToSize.
     */
    @Test
    public void trimToSize() {
        put(5);
        assertSize(5);
        mxBean.trimToSize(3);
        assertSize(3);
        put(10, 15);
        assertSize(9);
        mxBean.trimToSize(1);
        assertSize(1);

        // Exception
        try {
            mxBean.trimToSize(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {} catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void trimToVolume() {
        assertGet(M1, M2, M3, M4);
        assertSize(4);
        assertEquals(1 + 2 + 3 + 4, c.volume());
        mxBean.trimToVolume(5);

        long v = c.volume();
        assertTrue(c.volume() <= 5);
        assertGet(M5);
        assertEquals(v + 5, c.volume());
        mxBean.trimToVolume(3);
        assertTrue(c.volume() <= 3);
        // Exception
        try {
            mxBean.trimToVolume(-1);
            fail("Did not throw exception");
        } catch (IllegalArgumentException e) {} catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }
}
