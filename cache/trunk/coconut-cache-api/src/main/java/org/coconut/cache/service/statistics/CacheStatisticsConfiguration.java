/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheStatisticsConfiguration extends AbstractCacheServiceConfiguration {

    
    // add own counters???
    /** The short name of this service. */
    public static final String SERVICE_NAME = "statistics";

    /** Creates a new CacheStatisticsConfiguration. */
    public CacheStatisticsConfiguration() {
        super(SERVICE_NAME);
    }

//    public void setKeepStatisticsOnIndividualItems(boolean keepStatistics) {
//        
//    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fromXML( Element parent) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void toXML(Document doc, Element parent) {

    }

}
