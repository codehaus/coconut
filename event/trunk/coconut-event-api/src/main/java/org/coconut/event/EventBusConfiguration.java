/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import java.util.concurrent.Executor;

import org.coconut.core.Logger;
import org.coconut.event.spi.EventBusErrorHandler;
import org.coconut.filter.matcher.FilterMatcher;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventBusConfiguration<E> {

    /**
     * The default configuration that can be used by any event bus that is not
     * provided with a eventbus configuration object.
     */
    public static final EventBusConfiguration DEFAULT_CONFIGURATION = newConf();

    public static <E> EventBusConfiguration<E> newConf() {
        return new EventBusConfiguration<E>();
    }
    
    public EventBusConfiguration conf;

    private FilterMatcher<?, E> filterMatcher;

    private boolean reentrant = false;

    private EventBusErrorHandler<E> errorHandler;

    public void setCheckReentrant(boolean check) {
        this.reentrant = check;
    }

    public boolean getCheckReentrant() {
        return reentrant;
    }
    /**
     * Returns the log configured for the cache.
     * 
     * @return the log configured for the cache, or <tt>null</tt> if no log is
     *         configured
     */
    public EventBusErrorHandler<E> getErrorHandler() {
        return errorHandler;
    }
    
    /**
     * Sets the log that the cache should use for logging anomalies and errors.
     * If no log is set the cache will redirect all output to dev/null
     * 
     * @return this configuration
     * @see org.coconut.core.Loggers
     * @see org.coconut.core.Logger
     * @throws NullPointerException
     *             if log is <tt>null</tt>
     */
    public EventBusConfiguration<E> setErrorHandler(EventBusErrorHandler<E> errorHandler) {
        if (errorHandler == null) {
            throw new NullPointerException("errorHandler is null");
        }
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * @return the filterMatcher
     */
    public FilterMatcher<?, E> getFilterMatcher() {
        return filterMatcher;
    }

    /**
     * @param filterMatcher the filterMatcher to set
     */
    public void setFilterMatcher(FilterMatcher<?, E> filterMatcher) {
        this.filterMatcher = filterMatcher;
    }
}
