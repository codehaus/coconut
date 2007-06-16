/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import static org.coconut.internal.util.XmlUtil.addAndSetText;
import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.addComment;
import static org.coconut.internal.util.XmlUtil.getAttributeBoolean;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadOptional;
import static org.coconut.internal.util.XmlUtil.readValue;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.coconut.cache.CacheException;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.util.XmlUtil;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedGroupVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure how the cache can be remotely monitored and managed
 * using JMX.
 * <p>
 * Remote management (JMX) is turned off by default and you need to call
 * {@link #setEnabled(boolean)} to enable it before construction the cache.
 * <p>
 * If for some reason the cache fails to properly register with the MBeanServer at startup
 * time a {@link CacheException} will be thrown and the cache instance will be terminated.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
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

    /** XML rootgroup tag. */
    private final static String XML_ROOT_GROUP_TAG = "rootgroup";

    /** The domain to register managed beans under. */
    private String domain;

    /** Whether or not JMX management is enabled. */
    private boolean enabled; // default false

    /** The MBeanServer to register the managed beans under */
    private MBeanServer mBeanServer;

    /** The visitor to use for registration of the managed beans. */
    private ManagedGroupVisitor registrant;

    /** The top level ManagedGroup, all other services are registed under this. */
    private ManagedGroup root;

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
     * @return
     * @see #setRegistrant(ManagedGroupVisitor)
     */
    public ManagedGroupVisitor getRegistrant() {
        return registrant;
    }

    /**
     * @return
     * @see #setRoot(ManagedGroup)
     */
    public ManagedGroup getRoot() {
        return root;
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
     * @param name
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
                    "The specified domain results in an illegal objectname, "
                            + e.getMessage());
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
     * If no registrant is specified {@link #DEFAULT_REGISTRANT} will be used.
     * 
     * @param registrant
     * @see #getRegistrant()
     */
    public CacheManagementConfiguration setRegistrant(ManagedGroupVisitor registrant) {
        this.registrant = registrant;
        return this;
    }

    /**
     * Sets that ManagedGroup that each cache service will register with. Normal usage of
     * this cache does not involve using this method. But if you need to do something
     * crazy you can use this method.
     * 
     * @param root
     * @see #getRoot()
     */
    public CacheManagementConfiguration setRoot(ManagedGroup root) {
        this.root = root;
        return this;
    }

    /**
     * @see org.coconut.cache.service.spi.AbstractCacheConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Element e) throws Exception {
        domain = readValue(getChild(XML_DOMAIN_TAG, e), CacheMXBean.DEFAULT_JMX_DOMAIN);
        enabled = getAttributeBoolean(e, XML_ENABLED_ATTRIBUTE, false);
        registrant = loadOptional(e, XML_REGISTRANT_TAG, ManagedGroupVisitor.class);
        root = loadOptional(e, XML_ROOT_GROUP_TAG, ManagedGroup.class);
        if (getAttributeBoolean(e, "usePlatformMBeanServer", false)) {
            // This is bit whacked but we need it for consistency sake
            // sick configuration->ParentCache with custom MBeanServer
            // child wants to us platform MBeanServer, if set the
            // child server to null, it will use parents customer MBeanServer
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        }
    }

    /**
     * @see org.coconut.cache.service.spi.AbstractCacheConfiguration#toXML(org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element base) {
        XmlUtil.writeBooleanAttribute(base, XML_ENABLED_ATTRIBUTE, enabled, false);

        /* Domain */
        if (domain != null && !domain.equals(CacheMXBean.DEFAULT_JMX_DOMAIN)) {
            addAndSetText(doc, XML_DOMAIN_TAG, base, domain);
        }

        /* MBeanServer */
        if (mBeanServer != null) {
            if (mBeanServer == ManagementFactory.getPlatformMBeanServer()) {
                base.setAttribute("usePlatformMBeanServer", Boolean.toString(true));
            } else {
                addComment(doc, getResourceBundle(),
                        "management.cannotPersistMBeanServer", base);
            }
        }

        /* Registrant */
        addAndsaveObject(doc, base, XML_REGISTRANT_TAG, getResourceBundle(),
                "management.saveOfRegistrantFailed", registrant);

        /* Registrant */
        addAndsaveObject(doc, base, XML_ROOT_GROUP_TAG, getResourceBundle(),
                "management.saveOfRootGroupFailed", root);
    }
}
