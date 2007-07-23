/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.attributes;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;

public class TimeDecoratedLoader<K, V> implements CacheLoader<K, V> {
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
        CacheAttributes.setCost(attributes, System.nanoTime() - start);
        return v;
    }

}
