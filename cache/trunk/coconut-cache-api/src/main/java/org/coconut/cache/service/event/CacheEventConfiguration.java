/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import static org.coconut.internal.util.XmlUtil.getChild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.IllegalCacheConfigurationException;
import org.coconut.internal.util.XmlUtil;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * All events are enabled per default except AccessedEvent. While this might
 * seem inconsist. The main reason is that it is raised for every single access.
 * And if the cache is running with a 99% read ratio. There is going to be a
 * substantial overhead with enabling AccessedEvents compared to how often this
 * event is usefull. The main reason for excluding certain events is
 * performance.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEventConfiguration extends AbstractCacheServiceConfiguration {

	public static final String SERVICE_NAME = "event";

	public final static String INCLUDES_TAG = "includes";

	public final static String INCLUDE_TAG = "include";

	public final static String EXCLUDES_TAG = "excludes";

	public final static String EXCLUDE_TAG = "exclude";

	/** The classes that are excluded per default. */
	private final static Set<Class> defaultExcludes = new HashSet<Class>();

	private final static Map<String, Class> shortForm = new HashMap<String, Class>();
	static {
		defaultExcludes.add(CacheEntryEvent.ItemAccessed.class);
	}

	/** The classes that should be excluded. */
	private final Set<Class> excludes = new HashSet<Class>();

	/** The classes that should be included. */
	private final Set<Class> includes = new HashSet<Class>();

	// private boolean removeEventsForClear;

	/**
     * Creates a new CacheEventConfiguration with default settings.
     */
	public CacheEventConfiguration() {
		super(SERVICE_NAME, CacheEventService.class);
	}

	/**
     * Exclude the specified event types. The cache will not attempt to raise
     * any of the specified events. This can overriden by calls to
     * {@link #include(Class[])}. For example
     * <pre>
     * CacheEventConfiguration cef = null;
     * cef.exclude(CacheEntryEvent.class);
     * cef.include(CacheEntryEvent.ItemAdded.class);
     * </pre>
     * 
     * Will exclude all CacheEntryEvent except CacheEntryEvent.ItemAdded
     * 
     * @param classes
     *            the classes that should excluded.
     * @throws NullPointerException
     *             if one of the specified classes is null
     * @throws IllegalArgumentException
     *             if one or more of the specified classes does not inherit from
     *             CacheEvent
     */
	public void exclude(Class... classes) throws NullPointerException {
		checkClasses(classes);
		excludes.addAll(Arrays.asList(classes));
	}

	public void include(Class... classes) {
		checkClasses(classes);
		includes.addAll(Arrays.asList(classes));
	}

	/**
     * Returns <tt>true</tt> if an event should be raised for the specified
     * event type. Otherwise false.
     * 
     * @param clazz
     *            the specified event type
     * @return true if an event should be raised for the specified event type.
     *         Otherwise false.
     */
	public boolean isIncluded(Class<? extends CacheEvent> clazz) {
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}
		boolean isIncluded = !isCovered(defaultExcludes, clazz);
		isIncluded &= !isCovered(excludes, clazz);
		isIncluded |= isCovered(includes, clazz);
		return isIncluded;
	}

	private void checkClasses(Class[] classes) {
		if (classes == null) {
			throw new NullPointerException("classes is null");
		}
		for (int i = 0; i < classes.length; i++) {
			if (classes[i] == null) {
				throw new NullPointerException("classes contains a null [index = " + i
						+ "]");
			} else if (!CacheEvent.class.isAssignableFrom(classes[i])) {
				throw new IllegalArgumentException("the specified class (" + classes[i]
						+ ") does not extend " + CacheEvent.class);
			}
		}
	}

	private boolean isCovered(Set<Class> set, Class type) {
		for (Class c : set) {
			if (type.equals(c) || c.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	/**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
	@Override
	protected void fromXML(Document doc, Element parent) throws DOMException,
			ClassNotFoundException {
		Element includes = getChild(INCLUDES_TAG, parent);
		for (Element e : getChildElements(includes, INCLUDE_TAG)) {
			Class c = Class.forName(e.getTextContent());
			if (!CacheEvent.class.isAssignableFrom(c)) {
				throw new IllegalCacheConfigurationException("Included class "
						+ c.getCanonicalName() + " does not inherit from CacheEvent");
			}
			this.includes.add(c);
		}
		Element excludes = getChild(EXCLUDES_TAG, parent);
		for (Element e : getChildElements(excludes, EXCLUDE_TAG)) {
			Class c = Class.forName(e.getTextContent());
			if (!CacheEvent.class.isAssignableFrom(c)) {
				throw new IllegalCacheConfigurationException("Included class "
						+ c.getCanonicalName() + " does not inherit from CacheEvent");
			}
			this.excludes.add(c);
		}
	}

	private List<Element> getChildElements(Element e, String name) {
		List<Element> l = new ArrayList<Element>();
		if (e != null) {
			NodeList nl = e.getElementsByTagName(name);
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n instanceof Element) {
					l.add((Element) n);
				}
			}
		}
		return l;
	}

	/**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
	@Override
	protected void toXML(Document doc, Element parent) {
		add(includes, doc, parent, INCLUDES_TAG, INCLUDE_TAG);
		add(excludes, doc, parent, EXCLUDES_TAG, EXCLUDE_TAG);
	}

	static void add(Set<Class> set, Document doc, Element parent, String parentTag,
			String tag) {
		if (set.size() > 0) {
			Element e = XmlUtil.add(doc, parentTag, parent);
			for (Class c : set) {
				String name = c.getCanonicalName();
				if (c.getDeclaringClass() != null) {
					name = name.substring(0, name.length() - c.getSimpleName().length()
							- 1)
							+ "$" + c.getSimpleName();
				}
				XmlUtil.add(doc, tag, e, name);
			}
		}
	}

}
