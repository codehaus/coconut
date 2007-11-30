package org.coconut.cache.internal.service.loading;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.CacheLoaderCallback;

class UnsynchronizedCacheLoaderCallback<K, V> implements CacheLoaderCallback<K, V> {
    private final AttributeMap attributes;

    private final K key;

    private V result;

    private boolean isDone;

    private Throwable cause;

    /**
     * @param loader
     * @param key
     * @param callback
     */
    UnsynchronizedCacheLoaderCallback(final K key, AttributeMap attributes) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        this.key = key;
        this.attributes = attributes;
    }

    public AttributeMap getAttributes() {
        return attributes;
    }

    public K getKey() {
        return key;
    }
    public V getResult() {
        return result;
    }
    public Throwable getCause() {
        return cause;
    }
    public boolean isDone() {
        return isDone;
    }

    public void completed(V result) {
        if (isDone) {
            throw new IllegalStateException("Result already set");
        }
        this.result = result;
        isDone = true;
    }

    public void failed(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause is null");
        }
        if (isDone) {
            throw new IllegalStateException("Result already set");
        }
        this.cause = cause;
        isDone = true;
    }

}
