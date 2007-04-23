/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.core.Log;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandlingConfiguration {

    /** The default exception log to log to. */
    private Log log;

    /**
     * Sets the log that will be used for logging information whenever the cache
     * or any of its services fails in some way. If no log has been set using
     * this method. The exception handling service will used the default logger
     * set using {@link org.coconut.cache.CacheConfiguration#setDefaultLog(Log)}.
     * If no default logger has been set, output will be sent to
     * {@link System#err}.
     * 
     * @param log
     *            the log to use for exception handling
     * @return this configuration
     */
    public CacheExceptionHandlingConfiguration setErrorLog(Log log) {
        this.log = log;
        return this;
    }

    /**
     * Returns the log that is used for exception handling, or <tt>null</tt>
     * if no such log has been set.
     * 
     * @return the log that is used for exception handling, or <tt>null</tt>
     *         if no such log has been set
     * @see #setErrorLog(Log)
     */
    public Log getErrorLog() {
        return log;
    }
}
