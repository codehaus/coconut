package org.coconut.cache.test.operations;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.Cache;
import org.coconut.cache.test.keys.KeyGenerator;

public abstract class CacheOperations<K> implements Runnable {

    public static final Collection<Class<? extends Runnable>> col = (Collection) Arrays.asList(
            Get.class, Peek.class);

    static final String PREFIX = "test.opr.cache.";

    final KeyGenerator<K> keyGenerator;

    final Cache cache;

    public CacheOperations(Cache cache) {
        this(cache, null);
    }

    public CacheOperations(Cache cache, KeyGenerator<K> keyGenerator) {
        // System.out.println("new " + getClass());
        this.cache = cache;
        this.keyGenerator = keyGenerator;
    }

    public static class Get<K> extends CacheOperations<K> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "get";

        public Get(Cache cache, KeyGenerator<K> keyGenerator) {
            super(cache, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            cache.get(keyGenerator.nextKey());
        }
    }

    /**
     * Invokes the {@link Cache#clear()} method.
     */
    public static class Clear extends CacheOperations {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "clear";

        /**
         * Creates a new Clear operation.
         * 
         * @param cache
         *            the cache to invoke the operation on
         */
        public Clear(Cache cache) {
            super(cache);
        }

        /** {@inheritDoc} */
        public void run() {
            cache.clear();
        }
    }

    /**
     * Invokes the {@link Cache#shutdown()} method.
     */
    public static class Shutdown extends CacheOperations {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "shutdown";

        /**
         * Creates a new Clear operation.
         * 
         * @param cache
         *            the cache to invoke the operation on
         */
        public Shutdown(Cache cache) {
            super(cache);
        }

        /** {@inheritDoc} */
        public void run() {
            cache.shutdown();
        }
    }

    /**
     * Invokes the {@link Cache#shutdownNow()} method.
     */
    public static class ShutdownNow extends CacheOperations {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "shutdownNow";

        /**
         * Creates a new Clear operation.
         * 
         * @param cache
         *            the cache to invoke the operation on
         */
        public ShutdownNow(Cache cache) {
            super(cache);
        }

        /** {@inheritDoc} */
        public void run() {
            cache.shutdown();
        }
    }

    public static class Peek<K> extends CacheOperations<K> {
        public final static String NAME = PREFIX + "peek";

        public Peek(Cache cache, KeyGenerator<K> keyGenerator) {
            super(cache, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            cache.peek(keyGenerator.nextKey());
        }
    }
}
