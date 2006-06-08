package org.coconut.cache.tck.loading;

import static org.junit.Assert.assertNull;
import static org.coconut.test.CollectionUtils.M1;
import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class NoLoadingSupport extends CacheTestBundle {
    
    @Test
    public void testGet() {
        //hmm things can go wrong here:
        //1. loaders can load a value
        //2. the cache might throw exceptions if no value for the key can be found
        assertNull(c5.get(6));
        assertGet(M1);
    }
}
