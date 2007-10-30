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
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractManagedGroup implements ManagedGroup {
    final static Pattern NAME_PATTERN = Pattern
            .compile("[\\da-zA-Z\\x5F\\x2D]*(\\x2E([\\da-z\\x5F\\x2D])+)*");

    private final String description;

    private final ConcurrentHashMap<String, AbstractManagedGroup> map = new ConcurrentHashMap<String, AbstractManagedGroup>();

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
        return new ArrayList<ManagedGroup>(map.values());
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
    public boolean isRegistered() {
        return getObjectName() != null;
    }

    /** {@inheritDoc} */
    public synchronized void register(MBeanServer service, ObjectName name)
            throws JMException {
        service.registerMBean(this, name);
        this.server = service;
        this.objectName = name;
    }

    /** {@inheritDoc} */
    public synchronized void remove() {
        if (parent != null) {
            parent.map.remove(getName());
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

    /** {@inheritDoc} */
    synchronized ManagedGroup addNewGroup(AbstractManagedGroup group) {
        if (map.containsKey(group.getName())) {
            throw new IllegalArgumentException();
        }
        map.put(group.getName(), group);
        group.parent = this;
        return group;
    }

}
