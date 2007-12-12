package org.coconut.cache.test.operations;

import java.util.Arrays;
import java.util.Collection;

import javax.crypto.KeyGenerator;

import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.operations.Ops.Generator;

/**
 * Used to invoke operations on {@link CacheLoadingService}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class LoadingOps<K> implements Runnable {

    public static final Collection<Class<? extends Runnable>> col = (Collection) Arrays.asList(
            Load.class, ForceLoad.class);

    static final String PREFIX = "test.opr.loading.";

    final Generator<K> keyGenerator;

    final CacheLoadingService loadingService;

    public LoadingOps(CacheLoadingService loadingService, Generator<K> keyGenerator) {
        // System.out.println("new " + getClass());
        this.loadingService = loadingService;
        this.keyGenerator = keyGenerator;
    }

    public static class Load<K> extends LoadingOps<K> {
        public final static String NAME = PREFIX + "load";

        public Load(CacheLoadingService loadingService, Generator<K> keyGenerator) {
            super(loadingService, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            loadingService.load(keyGenerator.generate());
        }
    }

    /**
     * Invokes the {@link CacheLoadingService#forceLoad(Object)} method.
     */
    public static class ForceLoad<K> extends LoadingOps<K> {
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
        public ForceLoad(CacheLoadingService loadingService, Generator<K> keyGenerator) {
            super(loadingService, keyGenerator);
        }

        /** {@inheritDoc} */
        public void run() {
            loadingService.forceLoad(keyGenerator.generate());
        }
    }
}
