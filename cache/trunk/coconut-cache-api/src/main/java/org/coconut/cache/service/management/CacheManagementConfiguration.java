/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import static org.coconut.internal.util.XmlUtil.addAndSetText;
import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.addComment;
import static org.coconut.internal.util.XmlUtil.attributeBooleanGet;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadOptional;
import static org.coconut.internal.util.XmlUtil.readValue;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.util.XmlUtil;
import org.coconut.management.ManagedVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure how the cache can be remotely monitored and managed
 * using JMX.
 * <p>
 * Remote management (JMX) is turned off by default and you need to call
 * {@link #setEnabled(boolean)} to enable it before construction the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheManagementConfiguration extends AbstractCacheServiceConfiguration {

    /** The short name of this service. */
    public static final String SERVICE_NAME = "management";

    /** XML domain tag. */
    private final static String XML_DOMAIN_TAG = "domain";

    /** XML enabled tag. */
    private final static String XML_ENABLED_ATTRIBUTE = "enabled";

    /** XML registrant tag. */
    private final static String XML_REGISTRANT_TAG = "registrant";

    /** The domain to register managed beans under. */
    private String domain;

    /** Whether or not JMX management is enabled. */
    private boolean enabled; // default false

    /** The MBeanServer to register the managed beans under. */
    private MBeanServer mBeanServer;

    /** The visitor to use for registration of the managed beans. */
    private ManagedVisitor registrant;

    /**
     * Create a new CacheManagementConfiguration.
     */
    public CacheManagementConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the default domain to register all managed beans under.
     * 
     * @return the domain to register all managed beans under
     * @see #setDomain(String)
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return the configured MBeanServer
     * @see #setMBeanServer(MBeanServer)
     */
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    /**
     * Returns the configured registrant.
     * 
     * @return the configured registrant
     * @see #setRegistrant(ManagedVisitor)
     */
    public ManagedVisitor getRegistrant() {
        return registrant;
    }

    /**
     * Returns true if management is enabled for the cache, otherwise false.
     * <p>
     * The default setting is <tt>false</tt>.
     * 
     * @return <tt>true</tt> if management is enabled for the cache, otherwise
     *         <tt>false</tt>
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the specific domain that this cache should register under. If no domain is
     * specified the cache will use {@link CacheMXBean#DEFAULT_JMX_DOMAIN}.
     * 
     * @param domain
     *            the domain name
     * @return this configuration
     * @throws NullPointerException
     *             if domain is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the specified domain is not valid domain name
     */
    public CacheManagementConfiguration setDomain(String domain) {
        try {
            new ObjectName(domain + ":type=foo");
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(
                    "The specified domain results in an illegal objectname, " + e.getMessage());
        }
        this.domain = domain;
        return this;
    }

    /**
     * Sets whether or not management is enabled for the cache. The default value is
     * <tt>false</tt>.
     * 
     * @param enabled
     *            whether or not management should be enabled for the cache
     * @return this configuration
     * @see #isEnabled()
     */
    public CacheManagementConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Sets the {@link MBeanServer}} that the cache should register with. If no
     * MBeanServer is set and this service is enabled; the
     * {@link java.lang.management.ManagementFactory#getPlatformMBeanServer() platform MBeanServer}
     * will be used.
     * 
     * @param server
     *            the server that the cache should register with
     * @return this configuration
     * @see #getMBeanServer()
     */
    public CacheManagementConfiguration setMBeanServer(MBeanServer server) {
        mBeanServer = server;
        return this;
    }

    /**
     * Sets a ManagedGroupVisitor that will used to register all the. Normal users will
     * seldom need to use this method. But if you need some kind of non standard naming of
     * {@link javax.management.ObjectName ObjectNames}, wants to only register a specific
     * cache service or any other special thing. You can use this method to specify a
     * special registrant that will visit each cache service.
     * <p>
     * If no registrant is specified a default registrant will be used.
     * 
     * @param registrant
     *            the registrant
     * @return this configuration
     * @see #getRegistrant()
     */
    public CacheManagementConfiguration setRegistrant(ManagedVisitor registrant) {
        this.registrant = registrant;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element e) throws Exception {
        domain = readValue(getChild(XML_DOMAIN_TAG, e), CacheMXBean.DEFAULT_JMX_DOMAIN);
        enabled = attributeBooleanGet(e, XML_ENABLED_ATTRIBUTE, false);
        registrant = loadOptional(e, XML_REGISTRANT_TAG, ManagedVisitor.class);
        if (attributeBooleanGet(e, "usePlatformMBeanServer", false)) {
            // This is bit whacked but we need it for consistency sake
            // sick configuration->ParentCache with custom MBeanServer
            // child wants to us platform MBeanServer, if set the
            // child server to null, it will use parents customer MBeanServer
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        } else {
            mBeanServer = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element base) {
        XmlUtil.attributeBooleanSet(base, XML_ENABLED_ATTRIBUTE, enabled, false);

        /* Domain */
        if (domain != null && !domain.equals(CacheMXBean.DEFAULT_JMX_DOMAIN)) {
            addAndSetText(doc, XML_DOMAIN_TAG, base, domain);
        }

        /* MBeanServer */
        if (mBeanServer != null) {
            if (mBeanServer == ManagementFactory.getPlatformMBeanServer()) {
                base.setAttribute("usePlatformMBeanServer", Boolean.toString(true));
            } else {
                addComment(doc, getResourceBundle(), getClass(), "cannotPersistMBeanServer", base);
            }
        }

        /* Registrant */
        addAndsaveObject(doc, base, XML_REGISTRANT_TAG, getResourceBundle(), getClass(),
                "saveOfRegistrantFailed", registrant);

    }
}
