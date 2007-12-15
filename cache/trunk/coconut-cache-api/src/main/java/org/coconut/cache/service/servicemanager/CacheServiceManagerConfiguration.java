/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import static org.coconut.internal.util.XmlUtil.addTypedElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.CacheSPI;
import org.coconut.internal.util.XmlUtil;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheServiceManagerConfiguration extends AbstractCacheServiceConfiguration {

    /** The short name of this service. */
    public static final String SERVICE_NAME = "serviceManager";

    /** The XML tag for the cache loader. */
    private final static String SERVICE_TAG = "service";

    /** Any additional services attached to the cache. */
    private final Map<Object, Boolean> registeredServices = new LinkedHashMap<Object, Boolean>();

    /**
     * Create a new CacheManagementConfiguration.
     */
    public CacheServiceManagerConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Registers a object for the cache. Only objects of type {@link CacheLifecycle} or
     * {@link ManagedLifecycle}, are valid. If the object is of type
     * {@link CacheLifecycle} the cache will invoke the respectic lifecycle methods on the
     * object. If the object is of type {@link ManagedLifecycle} and management is enabled
     * for the cache (see {@link CacheManagementConfiguration#setEnabled(boolean)}). It
     * will be registered with a {@link ManagedGroup}.
     * <p>
     * Attaches the specified instance to the service map of the cache. This object can
     * then later be retrived by calling {@link org.coconut.cache.Cache#getService(Class)}.
     * 
     * <pre>
     * CacheServiceManagerConfiguration csmc;
     * csmc.attach(String.class, &quot;fooboo&quot;);
     * 
     * ...later..
     * Cache&lt;?,?&gt; c;
     * assert &quot;fooboo&quot; = c.getService(String.class);
     * </pre>
     * 
     * If the specified key conflicts with the key-type of any of the build in service an
     * exception will be thrown when the cache is constructed.
     * 
     * @param o
     *            the object to register
     * @return this configuration
     * @throws IllegalArgumentException
     *             in case of an argument of invalid type or if the object has already
     *             been registered.
     */
    public CacheServiceManagerConfiguration add(Object o) {
        if (o == null) {
            throw new NullPointerException("o is null");
        } else if (registeredServices.containsKey(o)) {
            throw new IllegalArgumentException("Object has already been registered");
        } else if (!(o instanceof CacheLifecycle || o instanceof ManagedLifecycle)) {
            throw new IllegalArgumentException(
                    "Object must be an instance of CacheLifecycle or ManagedObject");
        }
        registeredServices.put(o, false);
        return this;
    }

    /**
     * Returns the objects that have been registered through {@link #add(Object)}.
     * 
     * @return the objects that have been registered
     */
    public Collection<Object> getObjects() {
        return new ArrayList(registeredServices.keySet());
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element element) throws Exception {
        NodeList nl = element.getElementsByTagName(SERVICE_TAG);
        for (int i = 0; i < nl.getLength(); i++) {
            Object o = XmlUtil.loadObject((Element) nl.item(i), Object.class);
            add(o);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) throws Exception {
        for (Object o : getObjects()) {
            addTypedElement(doc, parent, SERVICE_TAG, CacheSPI.DEFAULT_CACHE_BUNDLE,
                    CacheServiceManagerConfiguration.class, "saveOfLifecycleFailed", o);
        }
    }
}
