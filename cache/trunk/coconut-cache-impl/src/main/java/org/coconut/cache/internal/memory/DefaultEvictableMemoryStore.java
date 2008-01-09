package org.coconut.cache.internal.memory;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.Cache;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.service.memorystore.CacheEvictionConfiguration;

public class DefaultEvictableMemoryStore<K, V> extends UnlimitedSequentialMemoryStore<K, V>
        implements CompositeService {

    /** The maximum volume of this cache. */
    private long maxVolume;

    /** The maximum size of this cache. */
    private int maxSize;

    /** The ReplacementPolicy used for deciding which elements to evict. */
    private final ReplacementPolicy cp;

    public DefaultEvictableMemoryStore(Cache<K, V> cache, AbstractCacheEntryFactoryService e,
            CacheEvictionConfiguration<K, V> conf) {
        super(cache, e);
        cp = conf.getPolicy() == null ? new LRUPolicy(1) : (ReplacementPolicy) conf.getPolicy();
        maxSize = getMaximumSizeFromConfiguration(conf);
        // System.out.println("maxSize " + maxSize);
        maxVolume = getMaximumVolumeFromConfiguration(conf);
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(cp);
    }

    /**
     * Returns the maximum size configured in the specified configuration.
     * 
     * @param conf
     *            the configuration to read the maximum size from
     * @return the maximum size configured in the specified configuration
     */
    static int getMaximumSizeFromConfiguration(CacheEvictionConfiguration<?, ?> conf) {
        int tmp = conf.getMaximumSize();
        return tmp == 0 ? Integer.MAX_VALUE : tmp;
    }

    /**
     * Returns the maximum volume configured in the specified configuration.
     * 
     * @param conf
     *            the configuration to read the maximum volume from
     * @return the maximum volume configured in the specified configuration
     */
    static long getMaximumVolumeFromConfiguration(CacheEvictionConfiguration<?, ?> conf) {
        long tmp = conf.getMaximumVolume();
        return tmp == 0 ? Long.MAX_VALUE : tmp;
    }

    @Override
    public int getMaximumSize() {
        return maxSize;
    }

    @Override
    public long getMaximumVolume() {
        return maxVolume;
    }

    @Override
    public void setMaximumSize(int size) {
        size = new CacheEvictionConfiguration<K, V>().setMaximumSize(size).getMaximumSize();
        super.setMaximumSize(size);
    }

    @Override
    public void setMaximumVolume(long volume) {
        volume = new CacheEvictionConfiguration<K, V>().setMaximumVolume(volume).getMaximumVolume();
        super.setMaximumVolume(volume);
    }
}
