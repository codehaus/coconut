package org.coconut.cache.test.operations;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.test.keys.KeyGenerator;

/**
 * Used to invoke operations on {@link CacheLoadingService}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
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

    /**
     * Invokes the {@link CacheLoadingService#forceLoad(Object)} method.
     */
    public static class ForceLoad<K> extends LoadingServiceOperations<K> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "forceLoad";

        /**
         * Creates a new ForceLoad operation.
         * 
         * @param loadingService
         *            the CacheLoadingService to invoke the operation on
         * @param keyGenerator
         *            the generator for generating keys
         */
        public ForceLoad(CacheLoadingService loadingService, KeyGenerator<K> keyGenerator) {
            super(loadingService, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            loadingService.forceLoad(keyGenerator.nextKey());
        }
    }
}
