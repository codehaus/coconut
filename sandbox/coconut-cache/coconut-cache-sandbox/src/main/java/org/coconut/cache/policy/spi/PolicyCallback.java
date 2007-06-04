/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.spi;

import java.util.List;

/**
 * This class can be used to quickly test new policies... Instead of using ints
 * as the replacement policy does. It uses references
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface PolicyCallback<T> {
    void remove(List<? extends T> l );
    
    //cache sets policyCallback
    //cache does somethinf
    //replacementPolicy.callback.remove(foo) -> cache
}
