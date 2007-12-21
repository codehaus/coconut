/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.operations;

import org.coconut.cache.service.loading.CacheLoadingService;

/**
 * Used to invoke operations on {@link CacheLoadingService}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class LoadingOps {

    static final String PREFIX = "test.opr.loading.";

    public static class Load<K, V> extends AbstractOperation<K, V> {
        public final static String NAME = PREFIX + "load";

        /** {@inheritDoc} */
        public void run() {
            loadingService.load(keyGenerator.generate());
        }
    }

    /**
     * Invokes the {@link CacheLoadingService#forceLoad(Object)} method.
     */
    public static class ForceLoad<K, V> extends AbstractOperation<K, V> {
        /** The name of this operation. */
        public final static String NAME = PREFIX + "forceLoad";
        /** {@inheritDoc} */
        public void run() {
            loadingService.forceLoad(keyGenerator.generate());
        }
    }
}
