package org.coconut.cache.test.service.loading;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.service.loading.CacheLoader;

public class CacheLoaderBuilder<K, V> {

    public static <K, V> CacheLoaderBuilder<K, V> create() {
        return null;
    }

    public CacheLoaderBuilder<K, V> asAbstract() {
        return null;
    }

    public CacheLoaderBuilder<K, V> as() {
        return null;
    }

    static class MyLoader<K, V> implements CacheLoader<K, V> {

        public V load(K key, AttributeMap attributes) throws Exception {
            return null;
        }

        public void loadAll(
                Collection<? extends LoaderCallback<? extends K, ? super V>> loadCallbacks) {}

    }

    static class ValueLoader extends AbstractCacheLoader<Integer, String> {

        private final AtomicLong count = new AtomicLong();

        private Integer key;

        private AttributeMap attributes;

        private String value;

        private Throwable cause;

        public String load(Integer key, AttributeMap attributes) throws Exception {
            if (key.equals(this.key)) {
                count.incrementAndGet();
                if (cause instanceof Error) {
                    throw (Error) cause;
                } else if (cause instanceof Exception) {
                    throw (Exception) cause;
                }
                for (Map.Entry<Attribute, Object> me : this.attributes.entrySet()) {
                    attributes.put(me.getKey(), me.getValue());
                }
                return value;
            }
            return null;
        }
    }
}