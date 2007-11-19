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
 * An abstract implementation of AbstractManagedGroup.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractManagedGroup implements ManagedGroup {
    /** The allowed naming pattern of a group. */
    public final static Pattern GROUP_NAMING_PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F\\x2D]*(\\x2E([\\da-z\\x5F\\x2D])+)*");

    /** The child groups for this group. */
    private final ConcurrentHashMap<String, AbstractManagedGroup> childGroups = new ConcurrentHashMap<String, AbstractManagedGroup>();

    /** The description of this group. */
    private final String description;

    /** The name of this group. */
    private final String name;

    /** The ObjectName this group is registered under. */
    private ObjectName objectName;

    /** The parent of this group. */
    private AbstractManagedGroup parent;

    /** The MBeanServer this group is registered with. */
    private MBeanServer server;

    /**
     * Creates a new AbstractManagedGroup with the specified name and description.
     * 
     * @param name
     *            the name of the group
     * @param description
     *            the description of the group
     * @throws NullPointerException
     *             if the specified name or description is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified name does not follow the naming standard of managed
     *             groups
     */
    AbstractManagedGroup(String name, String description) {
        if (name == null) {
            throw new NullPointerException("name is null");
        } else if (description == null) {
            throw new NullPointerException("description is null");
        } else if (name.length() == 0) {
            throw new IllegalArgumentException("cannot specify the empty string as name");
        } else if (!GROUP_NAMING_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("not a valid name, was " + name);
        }
        this.name = name;
        this.description = description;
    }

    /** {@inheritDoc} */
    public Collection<ManagedGroup> getChildren() {
        return new ArrayList<ManagedGroup>(childGroups.values());
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
            parent.childGroups.remove(getName());
            parent = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Name= " + getName() + ", Description =" + getDescription();
    }

    /** {@inheritDoc} */
    public synchronized void unregister() throws JMException {
        if (objectName != null) {
            server.unregisterMBean(objectName);
        }
        objectName = null;
        server = null;
    }

    /**
     * Called by the class extending this class, when a child group is added.
     * 
     * @param group
     *            the group that should be added
     * @return the group that was added
     * @throws IllegalArgumentException
     *             if a group with the specified name has already been added
     */
    protected ManagedGroup addNewGroup(AbstractManagedGroup group) {
        if (childGroups.putIfAbsent(group.getName(), group) != null) {
            throw new IllegalArgumentException(
                    "Could not add group, group with same name has already been added "
                            + group.getName());
        }
        group.parent = this;
        return group;
    }
}
