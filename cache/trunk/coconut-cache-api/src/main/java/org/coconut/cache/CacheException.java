/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

/**
 * <code>CacheException</code> is the main runtime exception thrown by Coconut Cache.
 * For control of when exceptions are thrown see
 * {@link org.coconut.cache.service.exceptionhandling.CacheExceptionHandler}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @see $HeadURL$
 */
public class CacheException extends RuntimeException {

    /** <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 4178832681965556432L;

    /**
     * Constructs a new CacheException with <code>null</code> as its detailed message.
     * The cause is not initialized, and may subsequently be initialized by a call to
     * {@link Throwable#initCause}.
     */
    public CacheException() {}

    /**
     * Constructs a new CacheException with the specified detailed message. The cause is
     * not initialized, and may subsequently be initialized by a call to
     * {@link Throwable#initCause}.
     * 
     * @param message
     *            the detailed message. The detailed message is saved for later retrieval
     *            by the {@link #getMessage()} method.
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * Constructs a new CacheException with the specified detail message and cause.
     * <p>
     * Note that the detailed message associated with <code>cause</code> is <i>not</i>
     * automatically incorporated in this CacheException's detailed message.
     * 
     * @param message
     *            the detailed message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A<tt>null</tt> value is permitted, and indicates that the
     *            cause is nonexistent or unknown.)
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CacheException with the specified cause and a detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the
     * class and detail message of <tt>cause</tt>). This constructor is useful for
     * CacheException that are little more than wrappers for other throwables.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A <tt>null</tt> value is permitted, and indicates that the
     *            cause is nonexistent or unknown.)
     */
    public CacheException(Throwable cause) {
        super(cause);
    }
}
