import org.coconut.cache.service.eviction.CacheEvictionService;


public interface CacheEvictionMXBean {

    /**
     * Returns the default idle time in milliseconds.
     * 
     * @return the default idle time in milliseconds
     * @see CacheEvictionService#getDefaultIdleTime(java.util.concurrent.TimeUnit)
     */
    long getDefaultIdleTimeMs();

    /**
     * Sets the default idle time in milliseconds.
     * 
     * @param idleTimeMs
     *            the default idle time in milliseconds
     * @throws IllegalArgumentException
     *             if the specified idle time is not a positive number
     * @see CacheEvictionService#setDefaultIdleTime(long, java.util.concurrent.TimeUnit)
     */
    void setDefaultIdleTimeMs(long idleTimeMs);
    
    /**
     * Checks all elements in the cache for whether or not they are idle and should be
     * evicted.
     */
    void evictIdleElements();
}
