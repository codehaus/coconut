/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import java.util.Collection;

import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface RequestService<K, V> extends Offerable<CacheRequest<K, V>>,
        EventProcessor<CacheRequest<K, V>> {

    /**
     * A failure encountered while attempting to offering elements to an event
     * bus may result in some elements having already been processed. when the
     * associated exception is thrown. The behavior of this operation is
     * unspecified if the specified collection is modified while the operation
     * is in progress.
     * 
     * @param events
     *            the event to process
     * @return a boolean indicating if all events was accepted.
     */
    boolean offerAll(Collection<? extends CacheRequest<K, V>> c);
}
