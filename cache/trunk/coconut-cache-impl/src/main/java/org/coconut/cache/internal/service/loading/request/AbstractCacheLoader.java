/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading.request;

import java.util.Collection;

import org.coconut.cache.service.loading.CacheLoader;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheLoader<K, V> implements CacheLoader<K, V> {

    public final void loadAll(Collection<LoadRequest<K, V>> loadRequests) {
        for (LoadRequest<K, V> req : loadRequests) {
            try {
                V result = load(req.getKey(), req.getAttributes());
                req.completed(result);
            } catch (Exception e) {
                req.failed(e);
            }
        }
    }
}
