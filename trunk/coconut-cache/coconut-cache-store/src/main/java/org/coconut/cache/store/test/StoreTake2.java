/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.store.test;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coconut.core.CallbackFuture;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface StoreTake2<K, V> {
    Future<V> deleteAsync(K key, boolean retrievePreviousValue);

    V delete(K key, boolean retrievePreviousValue) throws Exception;

    CallbackFuture<V> deleteCallback(K key, boolean retrievePreviousValue);

    interface ItemLoaded<K, V> extends Map.Entry<K, V> {
        Map<String, Object> getAttributes();

        long getDuration(TimeUnit unit);
    }
}
