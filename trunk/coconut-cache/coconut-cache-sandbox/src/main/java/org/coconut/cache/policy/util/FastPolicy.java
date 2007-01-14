/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.util;

import java.util.List;

import org.coconut.cache.policy.ReplacementPolicy;

/**
 * This class can be used to quickly test new policies...
 * Instead of using ints as the replacement policy does.
 * It uses references
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class FastPolicy<T> implements ReplacementPolicy<T>{

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#add(java.lang.Object)
     */
    public int add(T element) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#getSize()
     */
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#peekAll()
     */
    public List<T> peekAll() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#remove(int)
     */
    public T remove(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#touch(int)
     */
    public void touch(int index) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.policy.ReplacementPolicy#update(int, java.lang.Object)
     */
    public boolean update(int index, T newElement) {
        // TODO Auto-generated method stub
        return false;
    }

}
