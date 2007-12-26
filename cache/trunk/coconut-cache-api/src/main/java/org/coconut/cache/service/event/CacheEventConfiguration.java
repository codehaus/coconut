/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import static org.coconut.internal.util.XmlUtil.attributeBooleanGet;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.util.XmlUtil;
import org.coconut.operations.Ops.Predicate;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The configuration object for the Cache Event service bundle. This service is disabled
 * per default and must be enabled by setting {@link #setEnabled(boolean)} to
 * <code>true</code>.
 * <p>
 * All events will be raised per default except ItemAccessed. While this might seem
 * inconsistent there is performance drawback for having this event enabled per default.
 * If the cache is running with a 99% read ratio there will be a substantial overhead of
 * notifying all listeners on each cache access. Instead, it must be manually enabled by
 * calling ...some code...
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEventConfiguration extends AbstractCacheServiceConfiguration {

    /** The name of this service. */
    public static final String SERVICE_NAME = "event";

    /** the XML enabled tag. */
    private final static String XML_ENABLED_ATTRIBUTE = "enabled";

    /** Whether or not this service is enabled. */
    private boolean enabled;

    private Predicate<? super Class<?>> enabledEventPredicate;

    /**
     * Creates a new CacheEventConfiguration with default settings.
     */
    @SuppressWarnings("unchecked")
    public CacheEventConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns <code>true</code> if the event service is enabled for the cache,
     * otherwise <code>false</code>.
     * <p>
     * The default setting is <tt>false</tt>.
     *
     * @return <tt>true</tt> if the event service is enabled for the cache, otherwise
     *         <tt>false</tt>
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether or not the event service is enabled for the cache. The default value
     * is <tt>false</tt>.
     * <p>
     *
     * @param enabled
     *            whether or not the event service should be enabled for the cache
     * @return this configuration
     * @see #isEnabled()
     */
    public CacheEventConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element parent) throws DOMException, ClassNotFoundException {
        setEnabled(attributeBooleanGet(parent, XML_ENABLED_ATTRIBUTE, false));
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) {
        XmlUtil.attributeBooleanSet(parent, XML_ENABLED_ATTRIBUTE, enabled, false);
    }

    public Predicate<? super Class<?>> getEnabledEventPredicate() {
        return enabledEventPredicate;
    }

    public void setEnabledEventPredicate(Predicate<? super Class<?>> enabledEventPredicate) {
        this.enabledEventPredicate = enabledEventPredicate;
    }

}
