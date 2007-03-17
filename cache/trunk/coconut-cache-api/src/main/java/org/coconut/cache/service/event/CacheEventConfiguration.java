/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * All events are enabled as default except AccessedEvent. While this might seem
 * inconsist. The main reason is that it is raised for every access to the and
 * if the cache is running with a 99% read ratio. There is going to be a
 * substantial overhead compared to how often this event is usefull.
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
        CacheEventConfiguration cef = new CacheEventConfiguration();
        // Collection<Class<? extends CacheEvent>>
        cef.exclude(CacheEvent.CacheCleared.class);
        cef.exclude(CacheEntryEvent.class);
        System.out.println(cef.isIncluded(CacheEntryEvent.ItemAccessed.class));
        System.out.println(cef.isIncluded(CacheEntryEvent.ItemRemoved.class));
        System.out.println(cef.isIncluded(CacheEvent.CacheCleared.class));
        System.out.println(cef.isIncluded(CacheEvent.CacheEvicted.class));
        cef.exclude(CacheEvent.CacheCleared.class, CacheEntryEvent.ItemAccessed.class);
    }

    // statistics for event bus
    // provide own eventbus?
    // Er
    private final static Set<Class> defaultExcludes = new HashSet<Class>();
    static {
        defaultExcludes.add(CacheEntryEvent.ItemAccessed.class);
    }

    private final Set<Class> includes = new HashSet<Class>();

    private final Set<Class> excludes = new HashSet<Class>();

    public void exclude(Class... classes) {
        checkClasses(classes);
        excludes.addAll(Arrays.asList(classes));
    }

    public void include(Class... classes) {
        checkClasses(classes);
        includes.addAll(Arrays.asList(classes));
    }

    public boolean isIncluded(Class<? extends CacheEvent> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }
        boolean isIncluded = !isCovered(defaultExcludes, clazz);
        isIncluded &= !isCovered(excludes, clazz);
        isIncluded |= isCovered(includes, clazz);
        return isIncluded;
    }

    private boolean isCovered(Set<Class> set, Class type) {
        for (Class c : set) {
            if (type.equals(c) || c.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    private void checkClasses(Class[] classes) {
        if (classes == null) {
            throw new NullPointerException("classes is null");
        }
        for (Class c : classes) {
            if (!CacheEvent.class.isAssignableFrom(c)) {
                throw new IllegalArgumentException("the specified class (" + c
                        + ") does not inherit from " + CacheEvent.class);
            }
        }
    }

}
