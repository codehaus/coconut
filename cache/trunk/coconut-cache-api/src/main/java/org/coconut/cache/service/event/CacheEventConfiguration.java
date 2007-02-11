/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEventConfiguration extends AbstractCacheServiceConfiguration {

    /**
     * @param tag
     */
    public CacheEventConfiguration(String tag) {
        super("events");
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document, org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Document doc, Element parent) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#getServiceInterface()
     */
    @Override
    public Class getServiceInterface() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document, org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element parent) {
        // TODO Auto-generated method stub
        
    }

}
