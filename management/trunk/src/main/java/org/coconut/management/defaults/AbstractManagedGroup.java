/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractManagedGroup implements ManagedGroup {
    final static Pattern NAME_PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F\\x2D]*(\\x2E([\\da-z\\x5F\\x2D])+)*");

    /** The description of this group. */
    private final String description;

    private final ConcurrentHashMap<String, AbstractManagedGroup> children = new ConcurrentHashMap<String, AbstractManagedGroup>();

    private final String name;

    private ObjectName objectName;

    private AbstractManagedGroup parent;

    private MBeanServer server;

    AbstractManagedGroup(String name, String description) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (name.length() == 0) {
            throw new IllegalArgumentException("cannot specify the empty string as name");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        }
        this.name = name;
        this.description = description;
    }

    /** {@inheritDoc} */
    public Collection<ManagedGroup> getChildren() {
        return new ArrayList<ManagedGroup>(children.values());
    }

    /** {@inheritDoc} */
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public synchronized ObjectName getObjectName() {
        return objectName;
    }

    /** {@inheritDoc} */
    public synchronized ManagedGroup getParent() {
        return parent;
    }

    /** {@inheritDoc} */
    public synchronized MBeanServer getServer() {
        return server;
    }

    /** {@inheritDoc} */
    public synchronized boolean isRegistered() {
        return objectName != null;
    }

    /** {@inheritDoc} */
    public synchronized void register(MBeanServer server, ObjectName objectName) throws JMException {
        if (server == null) {
            throw new NullPointerException("server is null");
        } else if (objectName == null) {
            throw new NullPointerException("objectName is null");
        } else if (this.objectName != null) {
            throw new IllegalStateException(
                    "This group has already been registered [MBeanServer = " + this.server
                            + ", ObjectName= " + this.objectName + "]");
        }
        server.registerMBean(this, objectName); // might fail
        this.server = server;
        this.objectName = objectName;
    }

    /** {@inheritDoc} */
    public synchronized void remove() {
        if (parent != null) {
            parent.children.remove(getName());
            parent = null;
        }
    }

    /** {@inheritDoc} */
    public synchronized void unregister() throws JMException {
        if (objectName != null) {
            server.unregisterMBean(objectName);
        }
        objectName = null;
        server = null;
    }

    synchronized ManagedGroup addNewGroup(AbstractManagedGroup group) {
        if (children.containsKey(group.getName())) {
            throw new IllegalArgumentException(
                    "Could not add group, group with same name has already been added "
                            + group.getName());
        }
        children.put(group.getName(), group);
        group.parent = this;
        return group;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Name= " + getName() + ", Description =" + getDescription();
    }
}
