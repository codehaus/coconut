/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.spi;

import java.util.concurrent.Callable;

import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface JMXConfigurator {

    void add(Object o);

    /**
     * Adds
     * 
     * @param n
     *            a {@link java.util.concurrent.Callable} that when invoked will
     *            return the current value of the metric
     * @param name
     *            the (attribute) name of metric as will appear in the MBean
     */
    // void add(Callable<?> n, String name);
    // void addAttribute(Callable<?> n, String name, String description);
    <T> void addAttribute(String name, String description, Callable<T> reader,
            Class<? extends T> type);

    <T> void addAttribute(String name, String description, Callable<T> reader,
            EventProcessor<T> writer, Class<? extends T> type);

    void addOperation(Runnable r, String name, String description);
    // some kind of unregister?
}
