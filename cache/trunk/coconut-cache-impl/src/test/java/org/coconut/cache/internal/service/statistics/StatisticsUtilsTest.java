/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

import org.junit.Test;

public class StatisticsUtilsTest {

    @Test(expected = NullPointerException.class)
    public void wrapMXBean() {
        StatisticsUtils.wrapMXBean(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void wrapService() {
        StatisticsUtils.wrapService(null);
    }
}
