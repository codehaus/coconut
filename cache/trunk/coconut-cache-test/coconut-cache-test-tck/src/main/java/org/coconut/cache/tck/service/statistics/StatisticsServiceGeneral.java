/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.statistics;

import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class StatisticsServiceGeneral extends AbstractCacheTCKTest {
    @Before
    public void setup() {
        setCache();
    }

    @Test
    public void testServiceAvailable() {
        assertNotNull(c.getService(CacheStatisticsService.class));
    }
}
