package org.coconut.cache.internal.service.statistics;

import org.coconut.cache.service.statistics.CacheHitStat;
import org.coconut.cache.service.statistics.CacheStatisticsMXBean;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * Various utility classes for statistics service implementation.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class StatisticsUtils {

    /**
     * Wraps a CacheLoadingService in a CacheLoadingMXBean.
     * 
     * @param service
     *            the CacheLoadingService to wrap
     * @return the wrapped CacheLoadingMXBean
     */
    public static CacheStatisticsMXBean wrapMXBean(CacheStatisticsService service) {
        return new DelegatedCacheStatisticsMXBean(service);
    }

    /**
     * A wrapper class that exposes an ExecutorService as a CacheExpirationMXBean.
     */
    public static class DelegatedCacheStatisticsMXBean implements CacheStatisticsMXBean {
        /** The CacheStatisticsService we are wrapping. */
        private final CacheStatisticsService service;

        /**
         * Creates a new DelegatedCacheStatisticsMXBean from the specified statistics
         * service.
         * 
         * @param service
         *            the statistics service to wrap
         */
        public DelegatedCacheStatisticsMXBean(CacheStatisticsService service) {
            if (service == null) {
                throw new NullPointerException("service is null");
            }
            this.service = service;
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The hit ratio")
        public double getHitRatio() {
            return service.getHitStat().getHitRatio();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The number of hits")
        public long getNumberOfHits() {
            return service.getHitStat().getNumberOfHits();
        }

        /** {@inheritDoc} */
        @ManagedAttribute(description = "The number of misses")
        public long getNumberOfMisses() {
            return service.getHitStat().getNumberOfMisses();
        }

        /** {@inheritDoc} */
        @ManagedOperation(description = "Resets the cache statistics")
        public void resetStatistics() {
            service.resetStatistics();
        }
    }

    /**
     * A wrapper class that exposes only the CacheStatisticsService methods of an
     * CacheStatisticsService implementation.
     */
    public static class DelegatedCacheStatisticsService<K, V> implements
            CacheStatisticsService {
        /** The expiration service we are wrapping. */
        private final CacheStatisticsService service;

        /**
         * Creates a new DelegatedCacheStatisticsService from the specified statistics
         * service.
         * 
         * @param service
         *            the expiration service to wrap
         */
        public DelegatedCacheStatisticsService(CacheStatisticsService service) {
            this.service = service;
        }

        /** {@inheritDoc} */
        public CacheHitStat getHitStat() {
            return service.getHitStat();
        }

        /** {@inheritDoc} */
        public void resetStatistics() {
            service.resetStatistics();
        }
    }
}
