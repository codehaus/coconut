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

}
