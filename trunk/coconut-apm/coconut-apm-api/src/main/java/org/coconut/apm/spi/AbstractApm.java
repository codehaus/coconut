/**
 * 
 */
package org.coconut.apm.spi;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.coconut.apm.Apm;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractApm implements Apm {
    private final static String DEFAULT_NAME = "unknown";

    private volatile String desc;

    private volatile String name = DEFAULT_NAME;

    private final NumberDynamicBean ndb;

    public AbstractApm() {
        this("Unknown", "Unknown");
    }

    public AbstractApm(String name) {
        this(name, "value of " + name);
    }

    public AbstractApm(String name, String description) {
        this.name = name;
        this.desc = description;
        ndb = new NumberDynamicBean(name);
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFilterValue(String variable, String value) {
        // ala replace $name with fooName
    }

    protected void setDescription(String description) {
        this.desc = description;
    }

    public String getDescription() {
        return desc;
    }

    public boolean isRegistered() {
        return ndb.isRegistered();
    }

    public synchronized void register(MBeanServer server, ObjectName name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        configureJMX(ndb);
        ndb.register(server, name);
    }

    public synchronized void register(MBeanServer server, String name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        configureJMX(ndb);
        ndb.register(server, name);
    }

    public synchronized void register(ObjectName on)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        configureJMX(ndb);
        ndb.register(on);
    }

    public synchronized void register(String name) throws InstanceAlreadyExistsException,
            MBeanRegistrationException {
        configureJMX(ndb);
        ndb.register(name);
    }

    public synchronized void unregister() throws MBeanRegistrationException {
        ndb.unregister();
    }

    /**
     * Invokes <tt>shutdown</tt> when this executor is no longer referenced.
     */
    protected void finalize() {
        try {
            ndb.unregister();
        } catch (MBeanRegistrationException e) {
            // ignore, really nothing we can do
        }
    }
}