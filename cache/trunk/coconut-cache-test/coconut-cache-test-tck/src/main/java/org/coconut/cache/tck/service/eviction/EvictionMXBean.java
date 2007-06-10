package org.coconut.cache.tck.service.eviction;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.RuntimeMBeanException;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Before;
import org.junit.Test;

public class EvictionMXBean extends AbstractCacheTCKTestBundle {

    CacheEvictionMXBean mxBean;

    MBeanServer mbs;

    static CacheEvictionConfiguration<?, ?> DEFAULT = new CacheEvictionConfiguration();

    // TODO
    // We should test for a default objectname
    // a.la. for cache named foo
    // ObjName=org.coconut.cache:name=343434, service=General

    @Before
    public void setup() {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().management().setEnabled(true).setMBeanServer(mbs).c());
        mxBean = findMXBean(mbs, CacheEvictionMXBean.class);
    }

    /**
     * Tests maximum capacity.
     */
    @Test
    public void maximumCapacity() {
        assertEquals(DEFAULT.getMaximumCapacity(), mxBean.getMaximumCapacity());
        mxBean.setMaximumCapacity(1000);
        assertEquals(1000, mxBean.getMaximumCapacity());
        assertEquals(1000, eviction().getMaximumCapacity());

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs).c().eviction().setMaximumCapacity(5000));
        mxBean = findMXBean(mbs, CacheEvictionMXBean.class);
        assertEquals(5000, mxBean.getMaximumCapacity());

        // Exception
        try {
            mxBean.setMaximumCapacity(-1);
            fail("Did not throw exception");
        } catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /**
     * Tests maximum size.
     */
    @Test
    public void maximumSize() {
        assertEquals(DEFAULT.getMaximumSize(), mxBean.getMaximumSize());
        mxBean.setMaximumSize(1000);
        assertEquals(1000, mxBean.getMaximumSize());
        assertEquals(1000, eviction().getMaximumSize());

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs).c().eviction().setMaximumSize(5000));
        mxBean = findMXBean(mbs, CacheEvictionMXBean.class);
        assertEquals(5000, mxBean.getMaximumSize());

        // Exception
        try {
            mxBean.setMaximumSize(-1);
            fail("Did not throw exception");
        } catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    /**
     * Tests default idle time.
     */
    @Test
    public void defaultIdleTime() {
        assertEquals(DEFAULT.getDefaultIdleTime(TimeUnit.MILLISECONDS), mxBean
                .getDefaultIdleTimeMs());
        mxBean.setDefaultIdleTimeMs(1000);
        assertEquals(1000, mxBean.getDefaultIdleTimeMs());
        assertEquals(1000 * 1000, eviction().getDefaultIdleTime(TimeUnit.MICROSECONDS));

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs).c().eviction().setDefaultIdleTime(1800,
                        TimeUnit.SECONDS));
        mxBean = findMXBean(mbs, CacheEvictionMXBean.class);
        assertEquals(1800 * 1000, mxBean.getDefaultIdleTimeMs());

        // Exception
        try {
            mxBean.setDefaultIdleTimeMs(-1);
            fail("Did not throw exception");
        } catch (RuntimeMBeanException e) {
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
        put(10,15);
        assertSize(9);
        mxBean.trimToSize(1);
        assertSize(1);
        
        // Exception
        try {
            mxBean.trimToSize(-1);
            fail("Did not throw exception");
        } catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    public void trimToCapacity() {
        //TODO implement
    }
    
    @Test
    public void evictIdleElements() {
        //TODO implement
    }
}