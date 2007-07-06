import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.eviction.CacheEvictionConfiguration;


public class EvictionServiceImpl {
    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#getDefaultIdleTime(java.util.concurrent.TimeUnit)
     */
    public long getDefaultIdleTime(TimeUnit unit) {
        return new CacheEvictionConfiguration<K, V>().setDefaultIdleTime(
                defaultIdleTimeNS, TimeUnit.NANOSECONDS).getDefaultIdleTime(unit);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#setDefaultIdleTime(long,
     *      java.util.concurrent.TimeUnit)
     */
    public void setDefaultIdleTime(long idleTime, TimeUnit unit) {
        defaultIdleTimeNS = new CacheEvictionConfiguration<K, V>().setDefaultIdleTime(
                idleTime, unit).getDefaultIdleTime(TimeUnit.NANOSECONDS);
    }
}
