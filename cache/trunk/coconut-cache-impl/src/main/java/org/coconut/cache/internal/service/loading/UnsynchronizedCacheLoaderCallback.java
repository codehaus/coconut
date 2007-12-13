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
        } else if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        this.key = key;
        this.attributes = attributes;
    }

    /** {@inheritDoc} */
    public AttributeMap getAttributes() {
        return attributes;
    }

    /** {@inheritDoc} */
    public K getKey() {
        return key;
    }

    V getResult() {
        return result;
    }

    Throwable getCause() {
        return cause;
    }

    /** {@inheritDoc} */
    public boolean isDone() {
        return isDone;
    }

    /** {@inheritDoc} */
    public void completed(V result) {
        if (isDone) {
            throw new IllegalStateException("Result already set");
        }
        this.result = result;
        isDone = true;
    }

    /** {@inheritDoc} */
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
