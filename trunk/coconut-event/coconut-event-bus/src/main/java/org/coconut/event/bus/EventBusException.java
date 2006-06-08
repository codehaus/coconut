package org.coconut.event.bus;

/**
 * <code>EventBusException</code> is the superclass of those exceptions that
 * can be thrown by Coconut EventBus.
 */
public class EventBusException extends RuntimeException {

    /** Default serial version uid */
    private static final long serialVersionUID = 3257570589891113265L;

    /**
     * Constructs a new exception with <code>null</code> as its detail
     * message. The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public EventBusException() {
    }

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * {@link #initCause}.
     * 
     * @param message
     *            the detail message. The detail message is saved for later
     *            retrieval by the {@link #getMessage()}method.
     */
    public EventBusException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with <code>cause</code> is
     * <i>not </i> automatically incorporated in this exception's detail
     * message.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()}method).
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()}method). (A<tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public EventBusException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of <tt>(cause==null ? null : cause.toString())</tt> (which typically
     * contains the class and detail message of <tt>cause</tt>). This
     * constructor is useful for exceptions that are little more than wrappers
     * for other throwables.
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()}method). (A<tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public EventBusException(final Throwable cause) {
        super(cause);
    }
}
