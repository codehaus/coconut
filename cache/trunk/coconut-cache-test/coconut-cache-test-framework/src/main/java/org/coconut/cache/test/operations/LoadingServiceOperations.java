package org.coconut.cache.test.operations;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.test.keys.KeyGenerator;

public abstract class LoadingServiceOperations<K> implements Runnable {

    public static final Collection<Class<? extends Runnable>> col = (Collection) Arrays.asList(
            Load.class, ForceLoad.class);

    static final String PREFIX = "test.opr.loading.";

    final KeyGenerator<K> keyGenerator;

    final CacheLoadingService loadingService;

    public LoadingServiceOperations(CacheLoadingService loadingService, KeyGenerator<K> keyGenerator) {
        // System.out.println("new " + getClass());
        this.loadingService = loadingService;
        this.keyGenerator = keyGenerator;
    }

    public static class Load<K> extends LoadingServiceOperations<K> {
        public final static String NAME = PREFIX + "load";

        public Load(CacheLoadingService loadingService, KeyGenerator<K> keyGenerator) {
            super(loadingService, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            loadingService.load(keyGenerator.nextKey());
        }
    }

    public static class ForceLoad<K> extends LoadingServiceOperations<K> {
        public final static String NAME = PREFIX + "forceLoad";

        public ForceLoad(CacheLoadingService loadingService, KeyGenerator<K> keyGenerator) {
            super(loadingService, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            loadingService.forceLoad(keyGenerator.nextKey());
        }
    }
}
