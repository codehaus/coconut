/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.threading.CacheThreadingConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XmlConfigurator is used to load and save
 * {@link org.coconut.cache.CacheConfiguration} as XML. Normally users do not
 * use this class but instead rely on
 * {@link CacheConfiguration#create(InputStream)},
 * {@link CacheConfiguration#createAndInstantiate(InputStream)} or
 * {@link CacheConfiguration#createInstantiateAndStart(InputStream)} methods in
 * {@link CacheConfiguration}
 * <p>
 * TODO: Move some of this functionality to an internal package.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlConfigurator {

	/**
     * The key for which the type of the cache is specified in
     * CacheConfiguration.getProperty()
     */
	public static final String CACHE_INSTANCE_TYPE = "org.coconut.cache.type";

	/** The name of cache */
	static final String CACHE_NAME_ATTR = "name";

	/** The root tag for a cache instance */
	static final String CACHE_TAG = "cache";

	/** The type of the cache */
	static final String CACHE_TYPE_ATTR = "type";

	/** The root tag */
	static final String CONFIG_TAG = "cache-config";

	/** The cache-config->version tag */
	static final String CONFIG_VERSION_ATTR = "version";

	/** The current version of the XML schema */
	public static final String CURRENT_VERSION = "0.0.4";

	private final static XmlConfigurator INSTANCE = new XmlConfigurator();

	private final Map<String, Class<? extends AbstractCacheServiceConfiguration>> services = new HashMap<String, Class<? extends AbstractCacheServiceConfiguration>>();

	protected void addDefaultConfiguration(
			Class<? extends AbstractCacheServiceConfiguration> clazz) {
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}
		final String name;
		try {
			Field f = clazz.getField("SERVICE_NAME");
			try {
				name = (String) f.get(null);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("service configuration (" + clazz
						+ ") should have a public SERVICE_NAME field");
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("service configuration (" + clazz
						+ ") should have a public SERVICE_NAME field");
			}
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("service configuration (" + clazz
					+ ") should have a public SERVICE_NAME field");
		}
		addDefaultConfiguration(clazz, name);
	}

	protected void addDefaultConfiguration(
			Class<? extends AbstractCacheServiceConfiguration> clazz, String name) {
		if (name == null) {
			throw new NullPointerException("name is null");
		} else if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}
		if (services.containsKey(name)) {
			throw new IllegalArgumentException("Service with name " + name
					+ " allready specified, with implementation " + services.get(name));
		}
		services.put(name, clazz);
	}

	XmlConfigurator() {
		initiateDefaultServices();
	}
	
	protected void initiateDefaultServices() {
		addDefaultConfiguration(CacheEventConfiguration.class);
		addDefaultConfiguration(CacheManagementConfiguration.class);
		addDefaultConfiguration(CacheStatisticsConfiguration.class);
		addDefaultConfiguration(CacheLoadingConfiguration.class);
		addDefaultConfiguration(CacheExpirationConfiguration.class);
		addDefaultConfiguration(CacheThreadingConfiguration.class);
		addDefaultConfiguration(CacheEvictionConfiguration.class);
	}

	/**
     * Returns the default instance of a XmlConfigurator.
     */
	public static XmlConfigurator getInstance() {
		return INSTANCE;
	}

	/**
     * Reads the XML configuration from the specified InputStream and populates
     * the specified CacheConfiguration.
     * 
     * @param configuration
     *            the CacheConfiguration to populate
     * @param stream
     *            the InputStream to load the configuration from
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being
     *             populated
     */
	public <K, V> void from(CacheConfiguration<K, V> configuration, InputStream stream)
			throws Exception {
		if (configuration == null) {
			throw new NullPointerException("configuration is null");
		} else if (stream == null) {
			throw new NullPointerException("stream is null");
		}
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				stream);
		from(configuration, doc);
	}

	/**
     * Serializes the specified CacheConfiguration to the specified
     * OutputStream.
     * 
     * @param cc
     *            the CacheConfiguration to serialize to XML
     * @param stream
     *            the OutputStream to write the serialized CacheConfiguration
     * @throws Exception
     *             some Exception prevented the CacheConfiguration from being
     *             serialized
     */
	public void to(CacheConfiguration<?, ?> cc, OutputStream stream) throws Exception {
		if (cc == null) {
			throw new NullPointerException("cc is null");
		} else if (stream == null) {
			throw new NullPointerException("stream is null");
		}
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();
		doc.setDocumentURI("http://www.dr.dk");

		Element root = doc.createElement(CONFIG_TAG);
		root.setAttribute(CONFIG_VERSION_ATTR, CURRENT_VERSION);
		doc.appendChild(root);

		Element cache = doc.createElement(CACHE_TAG);
		root.appendChild(cache);

		to(cc, doc, cache);
		transform(doc, stream);
	}

	<K, V> void from(CacheConfiguration<K, V> base, Document doc) throws Exception {
		Element root = doc.getDocumentElement();
		int length = root.getElementsByTagName(CACHE_TAG).getLength();
		if (length == 0) {
			throw new IllegalStateException(
					"No cache is defined in the specified document, "
							+ doc.getDocumentURI());
		}
		Node n = root.getElementsByTagName("cache").item(0);
		readCache(base, doc, (Element) n);
	}

	<K, V> void readCache(CacheConfiguration<K, V> conf, Document doc, Element cache)
			throws Exception {
		if (cache.hasAttribute(CACHE_NAME_ATTR)) {
			conf.setName(cache.getAttribute(CACHE_NAME_ATTR));
		}
		if (cache.hasAttribute(CACHE_TYPE_ATTR)) {
			conf.setProperty(XmlConfigurator.CACHE_INSTANCE_TYPE, cache
					.getAttribute(CACHE_TYPE_ATTR));
		}
		for (Class<? extends AbstractCacheServiceConfiguration> c : services.values()) {
			AbstractCacheServiceConfiguration acsc = c.newInstance();
			Element e = (Element) cache.getElementsByTagName(acsc.getServiceName()).item(
					0);
			if (e != null) {
				conf.addConfiguration(acsc);
				acsc.fromXML(doc, e);
			}
		}
	}

	void to(CacheConfiguration<?, ?> cc, Document doc, Element cache) throws Exception {
		cache.setAttribute(CACHE_NAME_ATTR, cc.getName());
		if (cc.getProperties().containsKey(XmlConfigurator.CACHE_INSTANCE_TYPE)) {
			cache.setAttribute(CACHE_TYPE_ATTR, cc.getProperty(CACHE_INSTANCE_TYPE)
					.toString());
		}
		
		/* Read other configurations */
		for (AbstractCacheServiceConfiguration p : cc.getConfigurations()) {
			Element ee = doc.createElement(p.getServiceName());
			cache.appendChild(ee);
			p.toXML(doc, ee);
		}
	}

	static void transform(Document doc, OutputStream stream) throws TransformerException {
		DOMSource domSource = new DOMSource(doc);
		StreamResult result = new StreamResult(stream);
		Transformer f = TransformerFactory.newInstance().newTransformer();
		f.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		f.setOutputProperty(OutputKeys.INDENT, "yes");
		f.transform(domSource, result);
	}
}
