package org.coconut.cache.test.service.loading;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoaderCallback;

public class TestLoader implements CacheLoader<Integer, String> {

    private final ConcurrentHashMap<Integer, SingleLoader> map = new ConcurrentHashMap<Integer, SingleLoader>();

    public String load(Integer key, AttributeMap attributes) throws Exception {
        //System.out.println("load " + key);
        SingleLoader sl = map.get(key);
        if (sl != null) {
            return sl.load(key, attributes);
        }
        return null;
    }

    public SingleLoader get(Integer key) {
        return map.get(key);
    }

    public long totalLoads() {
        long count = 0;
        for (SingleLoader sl : map.values()) {
            count += sl.getCount();
        }
        return count;
    }

    public static TestLoader create(int entries) {
        TestLoader tl = new TestLoader();
        for (int i = 1; i <= entries; i++) {
            tl.map.put(i, SingleLoader.from(i, "" + (char) (i + 64)));
        }
        return tl;
    }

    public void clearAndFromBase(int entries, int base) {
        map.clear();
        for (int i = 1; i <= entries; i++) {
            map.put(i, SingleLoader.from(i, "" + (char) (i + base + 64)));
        }
    }

    public static TestLoader createFromBase(int entries, int base) {
        TestLoader tl = new TestLoader();
        for (int i = 1; i <= entries; i++) {
            tl.map.put(i, SingleLoader.from(i, "" + (char) (i + base + 64)));
        }
        return tl;
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

    public void clear() {
        map.clear();
    }

    public TestLoader add(Integer key, String value, AttributeMap attributes) {
        map.put(key, SingleLoader.from(key, value, attributes));
        return this;
    }

    public <T> TestLoader add(Integer key, String value, Attribute<T> attribute, T avalue) {
        map.put(key, SingleLoader.from(key, value, Attributes.singleton(attribute, avalue)));
        return this;
    }
}
