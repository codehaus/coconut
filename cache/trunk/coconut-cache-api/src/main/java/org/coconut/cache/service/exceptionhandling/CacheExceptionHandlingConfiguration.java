/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.core.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandlingConfiguration<K, V> extends
		AbstractCacheServiceConfiguration<K, V> {
	public static final String SERVICE_NAME = "exceptionhandling";

	private AbstractCacheExceptionHandler<K, V> exceptionHandler = new CacheExceptionHandlingStrategies.DefaultCacheExceptionHandler<K, V>();

	/** The default exception log to log to. */
	private Logger log;

	/**
     * @param serviceName
     * @param serviceInterface
     */
	public CacheExceptionHandlingConfiguration() {
		super(SERVICE_NAME, null);
	}

	/**
     * @return the exceptionHandler
     */
	public AbstractCacheExceptionHandler<K, V> getExceptionHandler() {
		return exceptionHandler;
	}

	/**
     * Returns the log that is used for exception handling, or <tt>null</tt>
     * if no such log has been set.
     * 
     * @return the log that is used for exception handling, or <tt>null</tt>
     *         if no such log has been set
     * @see #setErrorLog(Log)
     */
	public Logger getExceptionLog() {
		return log;
	}

	/**
     * @param exceptionHandler
     *            the exceptionHandler to set
     */
	public void setExceptionHandler(AbstractCacheExceptionHandler<K, V> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

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
	public CacheExceptionHandlingConfiguration setExceptionLog(Logger log) {
		this.log = log;
		return this;
	}

	/**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
	@Override
	protected void fromXML(Document doc, Element parent) throws Exception {
	// TODO Auto-generated method stub

	}

	/**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
	@Override
	protected void toXML(Document doc, Element parent) throws Exception {
	// TODO Auto-generated method stub

	}
}
