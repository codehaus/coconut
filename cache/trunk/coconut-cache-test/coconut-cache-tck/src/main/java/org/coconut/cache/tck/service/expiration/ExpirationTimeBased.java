/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExpirationTimeBased extends ExpirationTestBundle {
    
    @Before
    public void setUpCaches() {
        c = newCache(newConf().setClock(clock));
        c0 = newCache(newConf().setClock(clock).expiration().setDefaultTimeToLive(10,
                TimeUnit.NANOSECONDS).c());
        put(M1, 2);
        put(M2, 3);
        put(M3, 4);
        put(M4, 4);
        put(M5, 6);
    }

    /**
     * Tests a custom expiration filter with get.
     */
    @Test
    public void testFilterGet() {

    }


}
