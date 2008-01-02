/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.parallel;

import org.coconut.cache.service.parallel.CacheParallelService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StatisticsServiceGeneral.java 526 2007-12-27 01:32:16Z kasper $
 */
public class ParallelServiceGeneral extends AbstractCacheTCKTest {

    @Test
    public void testServiceAvailable() {
        assertNotNull(c.getService(CacheParallelService.class));
    }
}
