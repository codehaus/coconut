/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import java.util.Collection;

/**
 * An abstract implementation of a {@link CacheLoader}. Use this class if you only need
 * to override the {@link CacheLoader#load(Object, org.coconut.core.AttributeMap)} method.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys used for loading values
 * @param <V>
 *            the type of values returned when loading
 */
public abstract class AbstractCacheLoader<K, V> implements CacheLoader<K, V> {

    /** {@inheritDoc} */
    public final void loadAll(
            Collection<? extends CacheLoaderCallback<? extends K, ? super V>> loadCallbacks) {
        for (CacheLoaderCallback<? extends K, ? super V> req : loadCallbacks) {
            try {
                V result = load(req.getKey(), req.getAttributes());
                req.completed(result);
            } catch (Throwable t) {
                req.failed(t);
            }
        }
    }
}
