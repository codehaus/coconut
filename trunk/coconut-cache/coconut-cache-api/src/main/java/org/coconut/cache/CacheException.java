/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

/**
 * <code>CacheException</code> is the main exception thrown by Coconut Cache.
 */
public class CacheException extends RuntimeException {

    /** <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 4178832681965556432L;

    /**
     * Constructs a new cache exception with <code>null</code> as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public CacheException() {
    }

    /**
     * Constructs a new cache exception with the specified detail message. The
     * cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()} method.
     */
    public CacheException(final String message) {
        super(message);
    }

    /**
     * Constructs a new cache exception with the specified detail message and
     * cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is
     * <i>not </i> automatically incorporated in this cache exception's detail
     * message.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A<tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public CacheException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new cache exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for cache exceptions that are little more than
     * wrappers for other throwables.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public CacheException(final Throwable cause) {
        super(cause);
    }
}