/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;

/**
 * This class is used to configure the statistics service prior to usage. Currently there
 * isn't much functionality to configure.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheStatisticsConfiguration extends AbstractCacheServiceConfiguration {

    /** The short name of this service. */
    public static final String SERVICE_NAME = "statistics";

    /** Creates a new CacheStatisticsConfiguration. */
    public CacheStatisticsConfiguration() {
        super(SERVICE_NAME);
    }
}
