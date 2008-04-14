/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 * An abstract implementation of AbstractManagedGroup.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AbstractManagedGroup.java 510 2007-12-12 08:52:55Z kasper $
 */
public abstract class AbstractManagedGroup implements ManagedGroup {
    /** The allowed naming pattern of a group. */
    public final static Pattern GROUP_NAMING_PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F\\x2D]*(\\x2E([\\da-z\\x5F\\x2D])+)*");

    /** The child groups for this group. */
    private final ConcurrentHashMap<String, AbstractManagedGroup> childGroups = new ConcurrentHashMap<String, AbstractManagedGroup>();

    /** The description of this group. */
    private final String description;

    /* main lock shared among parents and children */
    final Lock mainLock;

    /** The name of this group. */
    private final String name;

    /** The ObjectName this group is registered under. */
    private volatile ObjectName objectName;

    /** The parent of this group. */
    private AbstractManagedGroup parent;

    /** The MBeanServer this group is registered with. */
    private volatile MBeanServer server;

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
        this(null, name, description);
    }

    AbstractManagedGroup(AbstractManagedGroup parent, String name, String description) {
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

        if (parent == null) {
            mainLock = new ReentrantLock();
        } else {
            if (parent.childGroups.putIfAbsent(name, this) != null) {
                throw new IllegalArgumentException(
                        "Could not add group, group with same name has already been added " + name);
            }
            this.parent = parent;
            mainLock = parent.mainLock;
        }
    }

    protected Lock getLock() {
        return mainLock;
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
    public ObjectName getObjectName() {
        return objectName;
    }

    /** {@inheritDoc} */
    public ManagedGroup getParent() {
        return parent;
    }

    /** {@inheritDoc} */
    public MBeanServer getServer() {
        return server;
    }

    /** {@inheritDoc} */
    public boolean isRegistered() {
        return objectName != null;
    }

    /** {@inheritDoc} */
    public void register(MBeanServer server, ObjectName objectName) throws JMException {
        if (server == null) {
            throw new NullPointerException("server is null");
        } else if (objectName == null) {
            throw new NullPointerException("objectName is null");
        }
        mainLock.lock();
        try {
            beforeMutableOperationInner();
            if (this.objectName != null) {
                throw new IllegalStateException(
                        "This group has already been registered [MBeanServer = " + this.server
                                + ", ObjectName= " + this.objectName + "]");
            }
            server.registerMBean(getRegistrant(), objectName); // might fail
            this.server = server;
            this.objectName = objectName;
        } finally {
            mainLock.unlock();
        }
    }
    protected void beforeMutableOperation() {
        
    }
    void beforeMutableOperationInner() {
        if (parent != null) {
            parent.beforeMutableOperationInner();
        } else {
            beforeMutableOperation();
        }
    }

    /** {@inheritDoc} */
    public void remove() {
        mainLock.lock();
        try {
            beforeMutableOperationInner();
            if (parent != null) {
                parent.childGroups.remove(getName());
                parent = null;
            }
        } finally {
            mainLock.unlock();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Name= " + getName() + ", Description =" + getDescription();
    }

    /** {@inheritDoc} */
    public void unregister() throws JMException {
        mainLock.lock();
        try {
            if (objectName != null) {
                beforeMutableOperationInner();
                server.unregisterMBean(objectName);
                objectName = null;
                server = null;
            }
        } finally {
            mainLock.unlock();
        }
    }

    abstract Object getRegistrant();
}
