/**
 * 
 */
package org.coconut.apm.spi;

import java.util.concurrent.Callable;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.coconut.apm.Apm;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractApmNumber extends Number implements Callable<Number>,
        Apm {

    private final static String DEFAULT_NAME = "unknown";

    private volatile String desc;

    private volatile String name = DEFAULT_NAME;

    private final NumberDynamicBean ndb;

    public AbstractApmNumber() {
        this("Unknown", "Unknown");
    }

    public AbstractApmNumber(String name) {
        this(name, "value of " + name);
    }

    public AbstractApmNumber(String name, String description) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (description == null) {
            throw new NullPointerException("description is null");
        }
        this.name = name;
        this.desc = description;
        ndb = new NumberDynamicBean(name);
    }

    public Number call() {
        return getValue();
    }

    /**
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return getValue().doubleValue();
    }

    /**
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return getValue().floatValue();
    }

    public void setNameAndDescription(String name, String description) {
        this.name = name;
        this.desc = description;
    }

    protected final void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected final void setDescription(String description) {
        this.desc = description;
    }

    public String getDescription() {
        return desc;
    }

    /**
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return getValue().intValue();
    }

    public boolean isRegistered() {
        return ndb.isRegistered();
    }

    /**
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return getValue().intValue();
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

    protected Class<? extends Number> getNumberClass() {
        return Number.class;
    }

    protected abstract Number getValue();

    /**
     * @see org.coconut.metric.spi.ManagedMetric#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configureJMX(JMXConfigurator jmx) {
        jmx.add(this);
    }
}
