/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheStatisticsConfigurationTest {

    private CacheStatisticsConfiguration conf;

    @Test
    public void xmlTest() throws Exception {
        conf = new CacheStatisticsConfiguration();
        conf = reloadService(conf);
    }
}
