/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractBulkCacheLoader<K, V> implements BulkCacheLoader<K, V> {

    /**
     * @see org.coconut.cache.internal.service.loading.BulkCacheLoader#loadAll(java.util.Collection)
     */
    public final void loadAll(Collection<LoadRequest<K, V>> loadRequests) {
        for (LoadRequest<K, V> req : loadRequests) {
            V result = null;
            try {
                result = load(req.getKey(), req.getAttributes());
            } catch (Exception e) {
                req.completed(result);
            }
        }
    }

}
