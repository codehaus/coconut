/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.ByteArrayOutputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheServiceConfiguration<K, V> {

	private CacheConfiguration<K, V> conf;

	private final Class serviceInterface;

	private final String serviceName;

	private final ResourceBundle bundle;

	public AbstractCacheServiceConfiguration(String serviceName, Class serviceInterface) {
		this(serviceName, serviceInterface, Resources.DEFAULT_CACHE_BUNDLE);
	}

	public AbstractCacheServiceConfiguration(String serviceName, Class serviceInterface,
			ResourceBundle bundle) {
		if (serviceName == null) {
			throw new NullPointerException("serviceName is null");
		} else if (serviceInterface == null) {
			throw new NullPointerException("serviceInterface is null");
		}
		this.serviceName = serviceName;
		this.serviceInterface = serviceInterface;
		this.bundle = bundle;
	}
	protected ResourceBundle getResourceBundle() {
		return bundle;
	}
	/**
     * The parent configuration, or <tt>null</tt> if this configuration has
     * not been registered yet.
     */
	public CacheConfiguration<K, V> c() {
		return conf;
	}

	/**
     * Returns the service interface for this configuration. For example,
     * {@link org.coconut.cache.service.expiration.CacheExpirationConfiguration}
     * returns
     * {@link org.coconut.cache.service.expiration.CacheExpirationService}.
     */
	public final Class getServiceInterface() {
		return serviceInterface;
	}

	/**
     * Returns the unique name of this service. For example,
     * {@link org.coconut.cache.service.expiration.CacheExpirationConfiguration}
     * returns 'expiration'
     */
	public String getServiceName() {
		return serviceName;
	}

	/**
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		ByteArrayOutputStream sos = new ByteArrayOutputStream();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement(serviceName);
			doc.appendChild(root);
			toXML(doc, root);
			XmlConfigurator.transform(doc, sos);
			return new String(sos.toByteArray());
		} catch (Exception e) {
			if (conf.getClass().getPackage().getName().startsWith(
					Cache.class.getPackage().getName())) {
				// speciel exception for Coconut Cache implementations.
				throw new CacheException(Resources.getHighlyIrregular(), e);
			} else {
				throw new CacheException(
						"A String representation of this package could not be obtained, because it could not be serialized to XML",
						e);
			}
		}
	}

	protected abstract void fromXML(Document doc, Element parent) throws Exception;

	/**
     * Persists this configuration to xml.
     * 
     * @param doc
     *            the Document to write to
     * @param parent
     *            the top element of this configuration
     * @throws Exception
     *             this configuration could not be probably persisted
     */
	protected abstract void toXML(Document doc, Element parent) throws Exception;

	protected String lookup(String key) {
		if (bundle == null) {
			throw new IllegalStateException("No bundle has been defined");
		}
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			throw new RuntimeException("missing entry for key " + key, e);
		}
	}

	void setConfiguration(CacheConfiguration<K, V> conf) {
		this.conf = conf;
	}
}
