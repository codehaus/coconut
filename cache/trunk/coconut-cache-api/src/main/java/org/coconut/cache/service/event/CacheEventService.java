/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import org.coconut.event.EventBus;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheEventService<K, V> extends EventBus<CacheEvent<K, V>> {

    /**
     * @param event
     * @throws UnsupportedOperationException
     *             if no management is configured for this cache.
     */
    //how do we handle serial numbers?
    //Det er lige før vi bliver nødt til at fjerne den fra interfaces
    //og putte det på som en property..
//    void publishJMX(CacheEvent<?, ?> event);

}
