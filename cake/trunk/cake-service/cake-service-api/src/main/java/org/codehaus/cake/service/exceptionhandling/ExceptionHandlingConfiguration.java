package org.codehaus.cake.service.exceptionhandling;

import org.codehaus.cake.util.Logger;

/**
 * This class is used to configure the exception handling service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheExceptionHandlingConfiguration.java 525 2007-12-26 18:42:40Z kasper $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class ExceptionHandlingConfiguration<T extends ExceptionHandler> {

    /** The exception handler used for handling erroneous conditions in the cache. */
    private T exceptionHandler;

    /** The default exception log to log to. */
    private Logger logger;

    /**
     * Returns the exception handler that should be used to handle all exceptions and warnings or
     * <code>null</code> if it has been defined.
     * 
     * @return the exceptionHandler that is configured for the cache
     * @see #setExceptionHandler(ExceptionHandler)
     */
    public T getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Returns the log that is used for exception handling, or <tt>null</tt> if no such log has
     * been set.
     * 
     * @return the log that is used for exception handling, or <tt>null</tt> if no such log has
     *         been set
     * @see #setExceptionLogger(Logger)
     */
    public Logger getExceptionLogger() {
        return logger;
    }

    /**
     * Sets the exception handler that should be used to handle all exceptions and warnings. If no
     * exception handler is set using this method the cache should use the one specified to
     * {@link org.coconut.map.configuration.CacheConfiguration#setDefaultLogger(Logger)}. If a
     * logger has not been set using that method either. The cache will, unless otherwise specified,
     * use an instance of {@link CacheExceptionHandlers#defaultLoggingExceptionHandler()} to handle
     * exceptions.
     * 
     * @param exceptionHandler
     *            the exceptionHandler to use for handling exceptions and warnings
     * @return this configuration
     */
    public ExceptionHandlingConfiguration<T> setExceptionHandler(T exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Sets the log that will be used for logging information whenever the cache or any of its
     * services fails in some way.
     * <p>
     * If no logger has been set using this method. The exception handling service will used the
     * default logger returned from
     * {@link org.coconut.map.configuration.CacheConfiguration#getDefaultLogger()}. If no default
     * logger has been set, output will be sent to {@link System#err}.
     * 
     * @param log
     *            the log to use for exception handling
     * @return this configuration
     */
    public ExceptionHandlingConfiguration<T> setExceptionLogger(Logger log) {
        this.logger = log;
        return this;
    }
}
