/**
 * 
 */
package org.coconut.cache.pocket;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PocketCachesTest extends MockTestCase {

    PocketCaches pc;

    public void testJmxToObjectNameNPE() {
        try {
            PocketCaches.jmxToObjectName(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {

        }
    }

    public void testJmxToObjectNameIAE() {
        try {
            PocketCaches.jmxToObjectName("-.,");
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException npe) {

        }
    }

    public void testJmxToObjectName() throws MalformedObjectNameException {
        ObjectName expected = new ObjectName(PocketCaches.JMX_PREFIX + "asd");
        assertEquals(expected, PocketCaches.jmxToObjectName("asd"));
    }

    public void testMXBeanNPE() {
        try {
            PocketCaches.jmxToMXBean(null);
            fail("Should throw NullPointerException");
        } catch (NullPointerException npe) {

        }
    }
    public void testJmxRegisterCache() throws InstanceAlreadyExistsException,
            MBeanRegistrationException, InstanceNotFoundException {
        try {
        Mock mock = mock(PocketCache.class);
        mock.expects(once()).method("clear");
        mock.expects(once()).method("evict");
        mock.expects(once()).method("getHitRatio").will(returnValue(0.5d));
        mock.expects(once()).method("getNumberOfHits").will(returnValue(1l));
        mock.expects(once()).method("getNumberOfMisses").will(returnValue(2l));
        mock.expects(once()).method("size").will(returnValue(3));
        PocketCaches.jmxRegisterCache((PocketCache) mock.proxy(), "asd");
        PocketCacheMXBean bean = PocketCaches.jmxCreateProxy(ManagementFactory
                .getPlatformMBeanServer(), "asd");
        bean.clear();
        bean.evict();
        assertEquals(0.5d, bean.getHitRatio());
        assertEquals(1l, bean.getNumberOfHits());
        assertEquals(2l, bean.getNumberOfMisses());
        assertEquals(3, bean.getSize());
        } finally {
            PocketCaches.jxmUnregisterCache("asd");
        }
    }
}
