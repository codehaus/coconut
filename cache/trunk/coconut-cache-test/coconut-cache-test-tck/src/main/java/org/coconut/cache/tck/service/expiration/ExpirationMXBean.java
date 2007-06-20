package org.coconut.cache.tck.service.expiration;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.RuntimeMBeanException;

import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.junit.Before;
import org.junit.Test;

public class ExpirationMXBean extends AbstractExpirationTestBundle {
    CacheExpirationMXBean mxBean;

    MBeanServer mbs;

    static CacheExpirationConfiguration<?, ?> DEFAULT = new CacheExpirationConfiguration();

    // TODO
    // We should test for a default objectname
    // a.la. for cache named foo
    // ObjName=org.coconut.cache:name=343434, service=General

    @Before
    public void setup() {
        mbs = MBeanServerFactory.createMBeanServer();
        c = newCache(newConf().management().setEnabled(true).setMBeanServer(mbs).c());
        mxBean = findMXBean(mbs, CacheExpirationMXBean.class);
    }
    
    /**
     * Tests default idle time.
     */
    @Test
    public void defaultIdleTime() {
        assertEquals(DEFAULT.getDefaultTimeToLive(TimeUnit.MILLISECONDS), mxBean
             .getDefaultTimeToLiveMs());
        mxBean.setDefaultTimeToLiveMs(1000);
        assertEquals(1000, mxBean.getDefaultTimeToLiveMs());
        assertEquals(1000 * 1000, expiration().getDefaultTimeToLive(TimeUnit.MICROSECONDS));

        // start value
        c = newCache(newConf().setName("foo").management().setEnabled(true)
                .setMBeanServer(mbs).c().expiration().setDefaultTimeToLive(1800,
                        TimeUnit.SECONDS));
        mxBean = findMXBean(mbs, CacheExpirationMXBean.class);
        assertEquals(1800 * 1000, mxBean.getDefaultTimeToLiveMs());

        // Exception
        try {
            mxBean.setDefaultTimeToLiveMs(-1);
            fail("Did not throw exception");
        } catch (RuntimeMBeanException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }
}
