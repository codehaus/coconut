import java.util.Collection;
import java.util.HashMap;

import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;


public class CacheLoadingServiceImpl {

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter)
     */
    public final void filteredLoad(Filter<? super CacheEntry<K, V>> filter) {
        if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        Collection<? extends K> keys = helper.filterKeys(filter);
        forceLoadAll(keys);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter,
     *      org.coconut.core.AttributeMap)
     */
    public final void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            AttributeMap defaultAttributes) {
        if (filter == null) {
            throw new NullPointerException("filter is null");
        } else if (defaultAttributes == null) {
            throw new NullPointerException("defaultAttributes is null");
        }
        Collection<? extends K> keys = helper.filterKeys(filter);
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (K key : keys) {
            map.put(key, defaultAttributes);
        }
        forceLoadAll(map);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter,
     *      org.coconut.core.Transformer)
     */
    public final void filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer) {
        if (filter == null) {
            throw new NullPointerException("filter is null");
        } else if (attributeTransformer == null) {
            throw new NullPointerException("defaultAttributes is null");
        }
        Collection<? extends CacheEntry<K, V>> keys = helper.filter(filter);
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (CacheEntry<K, V> entry : keys) {
            AttributeMap atr = attributeTransformer.transform(entry);
            map.put(entry.getKey(), atr);
        }
        forceLoadAll(map);
    }

}
