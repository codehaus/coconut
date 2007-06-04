/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.locking;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheLockConfiguration<K,V> {

    // when we lock, we need all keys to implement comparable
    // unless we have used setLockKeyComparator(Comparator<K>))
    // we could have a setUseOnlyExclusiveLock method, calling readLock
    // on ReadWriteLock would result in a unsupportedOperationException
    //
    public Comparator<K> getLockKeyComparator() {
        return null;
    }

    public CacheLockConfiguration setAllowLockNonExistingElements(boolean allowLocking) {
        return this;
    }

    public CacheLockConfiguration setLocking(LockStrategy locking) {
        return this;
    }

    /**
     * This property will we ignored if Locking is set to NO_LOCKING How long
     * time we . If set to 0 = infinite
     * 
     * @param timeout
     *            the default lock timeout
     * @param unit
     *            the unit of the timeout
     * @return this configuration
     */
    public CacheLockConfiguration setLockInternalTimeout(long timeout, TimeUnit unit) {
        // difference for put/get???
        // well this needs to throw some kind of exception if it fails??
        // not sure this under the 80/20 rule
        return this;
    }

    /**
     * Too avoid deadlock when multiple locks on the different objects are
     * requested. A lock comparator can be set to make sure that locks are
     * allways acquired in the specified order. If no lock comparator is set the
     * default order among the keys are used. This is only needed if acquiring
     * locks on multiple objects on the same time.
     * 
     * @param lockKeyComperator
     *            the comperator to use for generating combo locks
     * @return this configuration
     */
    public CacheLockConfiguration setLockKeyComparator(Comparator<K> lockKeyComperator) {
        // if null natural lock order among keys is used
        return this;
    }

    // This will override
    // public void setInternalLockTimeoutPut(long timeout, TimeUnit unit) {
    // //difference for put/get???
    //

    /**
     * if this false the users needs to explicitly lock things.
     * 
     * @param lockOnGetPut
     *            whether or not to use locks explicitly
     * @return this configuration
     */
    public CacheLockConfiguration setLockOnPutGet(boolean lockOnGetPut) {
        return this;
    }
    
    
    /**
     * What about peek does it lock it???
     */
    public static enum LockStrategy {
        /**
         * 
         */
        NO_LOCKING,

        /**
         * Trying to acquire a readLock on the ReadWriteLock will throw an
         * unsupported operation.
         */
        READ_WRITE,

        /**
         * Trying to acquire a readLock on the ReadWriteLock will throw an
         * unsupported operation.
         */
        WRITE_ONLY;
    }
}


