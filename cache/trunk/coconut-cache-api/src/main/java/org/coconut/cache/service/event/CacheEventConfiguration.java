/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import java.util.Collection;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * All events are enabled pr default except AccessedEvent. While this might seem
 * inconsist. The main reason is that it is raised for every access, if running
 * in 99% read settings. In very few cases this information is usefull.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEventConfiguration extends AbstractCacheServiceConfiguration {

    private boolean removeEventsForClear;

    /**
     * @param tag
     */
    public CacheEventConfiguration() {
        super("events", CacheEventService.class);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Document doc, Element parent) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element parent) {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args) {
        CacheEventConfiguration cef = null;
        // cef.includeAll(Arrays.asList(CacheEvent.CacheEvicted.class));
    }

    // Management, we can post some events
    // on a notification fittelihut
    // omvendt af regular cache events.
    // alle er excluded some default

    // Det skal kunne være muligt at definere et filter
    // f.eks. 3 cache.gets > 1 second indenfor 10 seconder
    // raise event....
    // måske skal det snarere være en option hos
    // Management configuration

    // statistics for event bus
    // provide own eventbus?
    // Er
    public void exclude(Class<? extends CacheEvent> c) {

    }

    public void excludeAll(Collection<Class<? extends CacheEvent>> c) {

    }

    public void include(Class<? extends CacheEvent> c) {

    }

    public void includeAll(Collection<Class<? extends CacheEvent>> c) {

    }
}
