/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.locking;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheLockManager<K, V> {

    ReadWriteLock getLock();

    ReadWriteLock getLock(K key);

    ReadWriteLock getLock(K... keys);
}
