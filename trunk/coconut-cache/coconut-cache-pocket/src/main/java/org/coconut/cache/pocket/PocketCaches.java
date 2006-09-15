/**
 * 
 */
package org.coconut.cache.pocket;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PocketCaches {

    public final static String JMX_PREFIX = "org.coconut.cache.pocket:type=PocketCache,name=";

    final static class PocketCacheMXBeanImpl extends StandardMBean implements
            PocketCacheMXBean {

        private PocketCache<?, ?> pc;

        /**
         * @param pc
         */
        public PocketCacheMXBeanImpl(PocketCache<?, ?> pc)
                throws NotCompliantMBeanException {
            super(PocketCacheMXBean.class);
            if (pc == null) {
                throw new NullPointerException("pc is null");
            }
            this.pc = pc;
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#clear()
         */
        public void clear() {
            pc.clear();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#evict()
         */
        public void evict() {
            pc.evict();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#ratio()
         */
        public double getHitRatio() {
            return pc.getHitRatio();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#hit()
         */
        public long getNumberOfHits() {
            return pc.getNumberOfHits();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#misses()
         */
        public long getNumberOfMisses() {
            return pc.getNumberOfMisses();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#size()
         */
        public int getSize() {
            return pc.size();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#trimToSize(int)
         */
        public void trimToSize(int newSize) {
            pc.trimToSize(newSize);
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#getDefaultTrimSize()
         */
        public int getDefaultTrimSize() {
            return pc.getEvictWatermark();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#setDefaultTrimSize(int)
         */
        public void setEvictWatermark(int trimSize) {
            pc.setEvictWatermark(trimSize);
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#getCapacity()
         */
        public int getCapacity() {
            return pc.getCapacity();
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#setCapacity(int)
         */
        public void setCapacity(int newCapacity) {
            pc.setCapacity(newCapacity);
        }

        /**
         * @see org.coconut.cache.pocket.PocketCacheMXBean#resetStatistics()
         */
        public void resetStatistics() {
            pc.resetStatistics();
        }
    }

    public static PocketCacheMXBean jmxCreateProxy(MBeanServer server, ObjectName name) {
        return (PocketCacheMXBean) MBeanServerInvocationHandler.newProxyInstance(server,
                name, PocketCacheMXBean.class, false);
    }

    public static PocketCacheMXBean jmxCreateProxy(MBeanServer server, String name) {
        return jmxCreateProxy(server, jmxToObjectName(name));
    }

    public static void jmxRegisterCache(PocketCache cache, ObjectName name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        jmxRegisterCache(cache, name, ManagementFactory.getPlatformMBeanServer());
    }

    public static void jmxRegisterCache(PocketCache cache, ObjectName name,
            MBeanServer mbs) throws InstanceAlreadyExistsException,
            MBeanRegistrationException {
        try {
            mbs.registerMBean(jmxToMXBean(cache), name);
        } catch (NotCompliantMBeanException nce) {
            throw new Error("Internal error, this should not happen", nce);
        }
    }

    public static ObjectName jmxRegisterCache(PocketCache c, String name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        ObjectName oName = jmxToObjectName(name);
        jmxRegisterCache(c, oName);
        return oName;
    }

    /**
     * Returns a {@link PocketCacheMXBean} for the specified cache.
     * <p>
     * 
     * @return a {@link PocketCacheMXBean} for the specified cache.
     */
    public static PocketCacheMXBean jmxToMXBean(PocketCache<?, ?> cache) {
        try {
            return new PocketCacheMXBeanImpl(cache);
        } catch (NotCompliantMBeanException nce) {
            throw new Error("Internal error, this should not happen", nce);
        }
    }

    public static void jxmUnregisterCache(String name) throws InstanceNotFoundException,
            MBeanRegistrationException {
        ManagementFactory.getPlatformMBeanServer().unregisterMBean(jmxToObjectName(name));
    }

    public static ObjectName jmxToObjectName(String name) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        try {
            return new ObjectName(JMX_PREFIX + name);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException("Specified name (" + name
                    + ") results in invalid object name", e);
        }
    }

    public static <K, V> PocketCache<K, V> synchronizedPocketCache(PocketCache<K, V> cache) {
        return CollectionUtils.synchronizedPocketCache(cache);
    }

}
