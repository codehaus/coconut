/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.util.Collection;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * A ManagedGroup is passive collection of attributes pretty similar to a MBean. Easy to
 * register as MBean.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface ManagedGroup {

    /**
     * Adds an object to the group. The attributes and methods of this object will be
     * added to the aggregated methods and operations of this group.
     * 
     * @param o
     *            the object to add
     * @return this group
     * @throws NullPointerException
     *             if the specified object is <tt>null</tt>
     * @throws IllegalStateException
     *             if this group has already been register with a {@link MBeanServer}
     * @throws IllegalArgumentException
     *             if the object has already been registered, if it contains no methods or
     *             operations?, or if operations or methods with the same name has already
     *             been registered
     */
    ManagedGroup add(Object o);

    /**
     * Adds a child group.
     * 
     * @param name
     *            the name of the group. Cannot be the empty string
     * @param description
     *            the description of the group
     * @return this group
     * @throws NullPointerException
     *             if the specified name or description is null
     * @throws IllegalArgumentException
     *             if a group with the specified name has already been added or the
     *             specified name is the empty string
     */
    ManagedGroup addChild(String name, String description);

    /**
     * Returns all this groups child groups.
     * @return all this groups child groups
     */
    Collection<ManagedGroup> getChildren();

    /**
     * @return the description of this group.
     */
    String getDescription();

    /**
     * Returns the unique name of this group.
     * 
     * @return the unique name of this group.
     */
    String getName();

    /**
     * @return the objectname this group is registered under, or <code>null</code> if it
     *         has not yet been registered.
     */
    ObjectName getObjectName();

    /**
     * Returns the objects that are registered in this group.
     * 
     * @return the objects that are registered in this group
     */
    Collection<?> getObjects();

    /**
     * Returns the parent of this group or <code>null</code> if this group does not have
     * a parent.
     * 
     * @return the parent of this group or <code>null</code> if this group does not have
     * a parent
     */
    ManagedGroup getParent();

    /**
     * @return the MBeanServer this group is registered with or <tt>null</tt> if this
     *         group is not registered.
     */
    MBeanServer getServer();

    /**
     * Returns whether or not this group has been registered with a {@link MBeanServer}.
     * 
     * @return whether or not this group has been registered with a {@link MBeanServer}
     */
    boolean isRegistered();

    /**
     * @param server
     *            the mbean server where this group should be registered
     * @param objectName
     *            the objectname of this group
     * @throws JMException
     *             if the mbean could not be properly registered
     * @throws IllegalStateException
     *             if this group has already been registered with a {@link MBeanServer}
     */
    void register(MBeanServer server, ObjectName objectName) throws JMException;

    /**
     * Remove this group from its parent.
     */
    void remove();

    /**
     * Unregisters this group from the registered {@link MBeanServer} server. Any child
     * groups will not be unregistered.
     * 
     * @throws JMException
     *             if the mbean could not be properly unregistered
     */
    void unregister() throws JMException;
}
