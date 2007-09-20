/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.loadOptional;
import static org.coconut.internal.util.XmlUtil.readLogger;
import static org.coconut.internal.util.XmlUtil.writeLogger;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.core.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the exception handling service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheExceptionHandlingConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {
    /** The name of this service. */
    public static final String SERVICE_NAME = "exceptionhandling";

    /** The XML tag used to save the exception logger. */
    private final static String EXCEPTION_LOGGER_TAG = "exception-logger";

    /** The XML tag used to save the exception handler. */
    private final static String EXCEPTION_HANDLER_TAG = "exception-handler";

    /** The exception handler used for handling erroneous conditions in the cache. */
    private CacheExceptionHandler<K, V> exceptionHandler;

    /** The default exception log to log to. */
    private Logger logger;

    /**
     * Creates a new CacheExceptionHandlingConfiguration.
     */
    public CacheExceptionHandlingConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the exception handler that should be used to handle all exceptions and
     * warnings or <code>null</code> if it has been defined.
     * 
     * @return the exceptionHandler that is configured for the cache
     * @see #setExceptionHandler(CacheExceptionHandler)
     */
    public CacheExceptionHandler<K, V> getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Returns the log that is used for exception handling, or <tt>null</tt> if no such
     * log has been set.
     * 
     * @return the log that is used for exception handling, or <tt>null</tt> if no such
     *         log has been set
     * @see #setExceptionLogger(Logger)
     */
    public Logger getExceptionLogger() {
        return logger;
    }

    /**
     * Sets the exception handler that should be used to handle all exceptions and
     * warnings. If no exception handler is set using this method the cache should use the
     * one specified to {@link CacheConfiguration#setDefaultLogger(Logger). If a logger
     * has not been set using that method either. The cache will, unless otherwise
     * specified, use an instance of
     * {@link CacheExceptionHandlers#defaultLoggingExceptionHandler()} to handle
     * exceptions.
     * 
     * @param exceptionHandler
     *            the exceptionHandler to use for handling exceptions and warnings
     * @return this configuration
     */
    public CacheExceptionHandlingConfiguration<K, V> setExceptionHandler(
            CacheExceptionHandler<K, V> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Sets the log that will be used for logging information whenever the cache or any of
     * its services fails in some way.
     * <p>
     * If no logger has been set using this method. The exception handling service will
     * used the default logger returned from
     * {@link org.coconut.cache.CacheConfiguration#getDefaultLogger()}. If no default
     * logger has been set, output will be sent to {@link System#err}.
     * 
     * @param log
     *            the log to use for exception handling
     * @return this configuration
     */
    public CacheExceptionHandlingConfiguration<K, V> setExceptionLogger(Logger log) {
        this.logger = log;
        return this;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    protected void fromXML(Element parent) throws Exception {
        /* Exception Logger */
        logger = readLogger(parent, EXCEPTION_LOGGER_TAG);

        /* Exception Handler */
        exceptionHandler = loadOptional(parent, EXCEPTION_HANDLER_TAG,
                CacheExceptionHandler.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) throws Exception {
        /* Exception Logger */
        writeLogger(doc, parent, EXCEPTION_LOGGER_TAG, logger);

        /* Exception Handler */
        addAndsaveObject(doc, parent, EXCEPTION_HANDLER_TAG, getResourceBundle(),
                "exceptionhandling.saveOfExceptionHandlerFailed", exceptionHandler);

    }
}
