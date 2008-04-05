/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.service.management;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.codehaus.cake.util.management.ManagedVisitor;

/**
 * This class is used to configure how a container can be remotely monitored and managed
 * using JMX.
 * <p>
 * Remote management (JMX) is turned off by default and you need to call
 * {@link #setEnabled(boolean)} to enable it.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheManagementConfiguration.java 525 2007-12-26 18:42:40Z kasper $
 */
public class ManagementConfiguration {

    /** The domain to register managed beans under. */
    private String domain;

    /** Whether or not JMX management is enabled. */
    private boolean enabled;

    /** The MBeanServer to register the managed beans under. */
    private MBeanServer mBeanServer;

    /** The visitor to use for registration of the managed beans. */
    private ManagedVisitor<?> registrant;

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
    public ManagedVisitor<?> getRegistrant() {
        return registrant;
    }

    /**
     * Returns true if management is enabled, otherwise false.
     * <p>
     * The default setting is <tt>false</tt>.
     * 
     * @return <tt>true</tt> if management is enabled, otherwise <tt>false</tt>
     * @see #setEnabled(boolean)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the specific domain that MBeans should register under. If no domain is
     * specified the container will use a default name. For example, {@link org.coconut.map.CacheMXBean}
     * is registered under {@link org.coconut.map.CacheMXBean#DEFAULT_JMX_DOMAIN}.
     * 
     * @param domain
     *            the domain name
     * @return this configuration
     * @throws NullPointerException
     *             if the specified domain is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the specified domain is not valid domain name
     */
    public ManagementConfiguration setDomain(String domain) {
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
     * Sets whether or not management is enabled. The default value is <tt>false</tt>.
     * 
     * @param enabled
     *            whether or not management should be enabled
     * @return this configuration
     * @see #isEnabled()
     */
    public ManagementConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Sets the {@link MBeanServer}} that MBeans should register with. If no MBeanServer
     * is set and this service is enabled; the
     * {@link java.lang.management.ManagementFactory#getPlatformMBeanServer() platform MBeanServer}
     * will be used.
     * 
     * @param server
     *            the server that MBeans should register with
     * @return this configuration
     * @see #getMBeanServer()
     */
    public ManagementConfiguration setMBeanServer(MBeanServer server) {
        mBeanServer = server;
        return this;
    }

    /**
     * Sets a ManagedGroupVisitor that will used to register all the. Normal users will
     * seldom need to use this method. But if you need some kind of non standard naming of
     * {@link javax.management.ObjectName ObjectNames}, wants to only register a specific
     * service or any other special thing. You can use this method to specify a special
     * registrant that will visit each service.
     * <p>
     * If no registrant is specified a default registrant will be used.
     * 
     * @param registrant
     *            the registrant
     * @return this configuration
     * @see #getRegistrant()
     */
    public ManagementConfiguration setRegistrant(ManagedVisitor<?> registrant) {
        this.registrant = registrant;
        return this;
    }
}
