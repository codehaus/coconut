package org.coconut.cache.test.service.loading;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoaderCallback;

public class TestLoader implements CacheLoader<Integer, String> {
    private final ConcurrentHashMap<Integer, SingleLoader> map = new ConcurrentHashMap<Integer, SingleLoader>();

    public String load(Integer key, AttributeMap attributes) throws Exception {
        return map.get(key).load(key, attributes);
    }

    public SingleLoader get(Integer key) {
        return map.get(key);
    }

    public void loadAll(
            Collection<? extends CacheLoaderCallback<? extends Integer, ? super String>> loadCallbacks) {
        for (CacheLoaderCallback<? extends Integer, ? super String> req : loadCallbacks) {
            try {
                String result = load(req.getKey(), req.getAttributes());
                req.completed(result);
            } catch (Throwable t) {
                req.failed(t);
            }
        }
    }

}
