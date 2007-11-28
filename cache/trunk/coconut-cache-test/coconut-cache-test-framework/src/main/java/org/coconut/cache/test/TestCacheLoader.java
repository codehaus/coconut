package org.coconut.cache.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.cache.service.loading.AbstractCacheLoader;

public class TestCacheLoader extends AbstractCacheLoader<Integer, String> {

    List<RequestInfo> list = new CopyOnWriteArrayList<RequestInfo>();

    private final List<Attribute> l = new ArrayList<Attribute>();

    public TestCacheLoader(Attribute... attributes) {
        if (attributes.length > 0) {
            for (Attribute a : attributes) {
                if (a != SizeAttribute.INSTANCE) {
                    throw new IllegalArgumentException("Unknown attribute " + a);
                }
                l.add(a);
            }
        }
    }

    public int getTotalLoads() {
        return list.size();
    }

    public String load(Integer key, AttributeMap attributes) throws Exception {
        String result = doLoad(key, attributes);
        list.add(new RequestInfo(key, attributes, result));
        if (l.contains(SizeAttribute.INSTANCE)) {
            SizeAttribute.set(attributes, key.longValue());
        }
        return result;
    }

    protected String doLoad(Integer key, AttributeMap attributes) {
        if (1 <= key && key <= 5) {
            return "" + (char) (key + 64);
        } else {
            return null;
        }
    }

    public static class RequestInfo {
        private final Thread loadingThread;

        private final Integer key;

        private final AttributeMap attributes;

        private final String result;

        RequestInfo(Integer key, AttributeMap attributes, String result) {
            loadingThread = Thread.currentThread();
            this.key = key;
            this.attributes = attributes;
            this.result = result;
        }
    }
}
