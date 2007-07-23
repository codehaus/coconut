/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import org.coconut.cache.CacheConfiguration;

/**
 * This method is thrown if the a {@link CacheConfiguration} is invalid in some way.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IllegalCacheConfigurationException extends IllegalStateException {

    /** serialVersionUID. */
    private static final long serialVersionUID = -1695400915732143052L;

    /**
     * Constructs a new illegal cache configuration exception with <code>null</code> as
     * its detail message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link Throwable#initCause}.
     */
    public IllegalCacheConfigurationException() {}

    /**
     * Constructs a new illegal cache configuration exception with the specified detail
     * message. The cause is not initialized, and may subsequently be initialized by a
     * call to {@link Throwable#initCause}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by
     *            the {@link #getMessage()} method.
     */
    public IllegalCacheConfigurationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new illegal cache configuration exception with the specified detail
     * message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is <i>not </i>
     * automatically incorporated in this cache exception's detail message.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A<tt>null</tt> value is permitted, and indicates that the
     *            cause is nonexistent or unknown.)
     */
    public IllegalCacheConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new illegal cache configuration exception with the specified cause and
     * a detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for cache exceptions that are little more than wrappers for
     * other throwables.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A <tt>null</tt> value is permitted, and indicates that the
     *            cause is nonexistent or unknown.)
     */
    public IllegalCacheConfigurationException(final Throwable cause) {
        super(cause);
    }
}
