/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.util.Collection;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * A ManagedGroup is passive collection of attributes pretty similar to a MBean. Easy to
 * register as MBean.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ManagedGroup {

    /**
     * Returns the unique name of this group.
     * 
     * @Return the unique name of this group.
     */
    String getName();

    /**
     * Adds an object to the group. The attributes and methods of this object will be
     * added to the aggregated methods and operations of this group.
     * 
     * @param o
     *            the object to add
     * @return the object that was added
     * @throws NullPointerException
     *             if the specified object is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the object has already been registered, if it contains no methods or
     *             operations?, or if operations or methods with the same name has already
     *             been registered
     */
    ManagedGroup add(Object o);

    Collection<?> getObjects();

    /**
     * Adds a child group.
     * 
     * @param name
     *            the name of the group. Cannot be the empty string
     * @param description
     *            the description of the group
     * @throws NullPointerException
     *             if the specified name or description is null
     * @throws IllegalArgumentException
     *             if a group with the specified name has already been added or the
     *             specified name is the empty string
     */
    ManagedGroup addChild(String name, String description);

    boolean isRegistered();

    /**
     * Returns the objectname this group is registered under, or <code>null</code> if it
     * has not yet been registered.
     */
    ObjectName getObjectName();

    /**
     * Return the MBeanServer this group is registered with or <tt>null</tt> if this
     * group is not registered.
     */
    MBeanServer getServer();

    /**
     * Returns the description of this group
     */
    String getDescription();

    /**
     * Returns all the child groups.
     */
    Collection<ManagedGroup> getChildren();

    ManagedGroup getParent();

    /**
     * @param service
     * @param name *
     * @throws InstanceAlreadyExistsException
     *             This group is already under the control of a MBean server.
     * @throws JMException
     */
    void register(MBeanServer service, ObjectName objectName) throws JMException;

    /**
     * Remove this group from its parent.
     */
    void remove();

    /**
     * If any sub groups has registered these will also be unregistered.
     * 
     * @throws JMException
     */
    void unregister() throws JMException;
}
