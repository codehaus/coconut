/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.spi;

import org.coconut.event.EventBusConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AbstractEventBus<E> {

    private final static EventBusConfiguration NO_CONF = EventBusConfiguration.DEFAULT_CONFIGURATION;

    private final EventBusConfiguration<E> configuration;

    public AbstractEventBus() {
        this(NO_CONF);
    }
    /**
     * @param configuration
     */
    public AbstractEventBus(final EventBusConfiguration<E> configuration) {
        this.configuration = configuration;
    }
}
