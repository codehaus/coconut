/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.pocket;

import org.coconut.cache.pocket.ValueLoader;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DummyLoader implements ValueLoader<String, Integer>{

    /**
     * @see org.coconut.cache.pocket.ValueLoader#load(java.lang.Object)
     */
    public Integer load(String key) {
        return Integer.valueOf(key);
    }

}
