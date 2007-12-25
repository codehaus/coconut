package org.coconut.cache.test.service.loading;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.AbstractCacheLoader;

public class SingleLoader extends AbstractCacheLoader<Integer, String> {

    private AttributeMap attributes;

    private Throwable cause;

    private final AtomicLong count = new AtomicLong();

    private Integer key;

    private String value;

    public AttributeMap getAttributes() {
        return attributes;
    }

    public Throwable getCause() {
        return cause;
    }

    public long getCount() {
        return count.get();
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

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

    public static SingleLoader from(Integer key, String value) {
        SingleLoader sl = new SingleLoader();
        sl.key = key;
        sl.value = value;
        return sl;
    }

    public static SingleLoader from(Integer key, String value, AttributeMap attributes) {
        SingleLoader sl = from(key, value);
        sl.attributes = attributes;
        return sl;
    }

    public static SingleLoader from(Integer key, Throwable cause) {
        SingleLoader sl = new SingleLoader();
        sl.key = key;
        sl.cause = cause;
        return sl;
    }

    public void setAttributes(AttributeMap attributes) {
        this.attributes = attributes;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
