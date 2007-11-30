/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.attributes;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.service.loading.CacheLoader;

public class TimeDecoratedLoader<K, V> extends AbstractCacheLoader<K, V> {
    private final CacheLoader<K, V> delegator;

    public TimeDecoratedLoader(CacheLoader<K, V> delegator) {
        if (delegator == null) {
            throw new NullPointerException("delegator is null");
        }
        this.delegator = delegator;
    }

    public V load(K key, AttributeMap attributes) throws Exception {
        long start = System.nanoTime();
        V v = delegator.load(key, attributes);
        CostAttribute.set(attributes, System.nanoTime() - start);
        return v;
    }

}
