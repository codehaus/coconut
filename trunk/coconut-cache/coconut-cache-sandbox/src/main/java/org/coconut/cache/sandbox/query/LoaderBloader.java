/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.sandbox.query;

import java.util.concurrent.Future;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface LoaderBloader<K, V> {
    Future<V> foo();

    interface LoaderCallback<K, V> {
        void loadingDone(K key, V value, Throwable error);
    }
}
