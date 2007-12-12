package org.coconut.cache.test.operations;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.Cache;
import org.coconut.operations.Ops.Generator;

public abstract class CacheOps<K> implements Runnable {

    public static final Collection<Class<? extends Runnable>> col = (Collection) Arrays.asList(
            Get.class, Peek.class);

    static final String PREFIX = "test.opr.cache.";

    final Generator<K> keyGenerator;

    final Cache cache;

    public CacheOps(Cache cache) {
        this(cache, null);
    }

    public CacheOps(Cache cache, Generator<K> keyGenerator) {
        this.cache = cache;
        this.keyGenerator = keyGenerator;
    }

    public static class Get<K> extends CacheOps<K> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "get";

        public Get(Cache cache, Generator<K> keyGenerator) {
            super(cache, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            cache.get(keyGenerator.generate());
        }
    }

    /**
     * Invokes the {@link Cache#clear()} method.
     */
    public static class Clear extends CacheOps {
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
    public static class Shutdown extends CacheOps {
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
    public static class ShutdownNow extends CacheOps {
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

    public static class Peek<K> extends CacheOps<K> {
        public final static String NAME = PREFIX + "peek";

        public Peek(Cache cache, Generator<K> keyGenerator) {
            super(cache, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            cache.peek(keyGenerator.generate());
        }
    }
}
