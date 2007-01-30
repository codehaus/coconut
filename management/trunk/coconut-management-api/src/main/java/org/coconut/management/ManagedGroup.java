/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.util.Collection;

import javax.management.JMException;

import org.coconut.core.Named;

/**
 * A ManagedGroup is passive collection of attributes  pretty similar to a MBean. Easy to register as MBean
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ManagedGroup extends Named {

    /**
     * Adds an object to the group. The
     * 
     * @param o
     *            the object to add
     * @return the object that was added
     * @throws NullPointerException
     *             if the specified object is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the object has already been registered
     */
    ManagedGroup add(Object o);

    /**
     * Adds a child group.
     * 
     * @param name
     *            the name of the group. Cannot be the empty string
     * @param description
     *            the description of the group
     * @return
     * @throws NullPointerException
     *             if the specified name or description is null
     * @throws IllegalArgumentException
     *             if a group with the specified name has already been added or
     *             the specified name is the empty string
     */
    ManagedGroup addGroup(String name, String description);

    /**
     * Adds a child group.
     * 
     * @param name
     *            the name of the group. Cannot be the empty string
     * @param description
     *            the description of the group
     * @param register
     *            whether or not the group should be registered as a MBean
     * @return
     * @throws NullPointerException
     *             if the specified name or description is null
     * @throws IllegalArgumentException
     *             if a group with the specified name has already been added or
     *             the specified name is the empty string
     */
    ManagedGroup addGroup(String name, String description, boolean register);

    /**
     * Returns all the objects that have been registered in this group.
     */
    Collection<?> getAll();

    /**
     * Returns the description of this group
     */
    String getDescription();

    /**
     * Returns all the child groups.
     */
    Collection<ManagedGroup> getGroups();
    
    /**
     * @return
     */
    ManagedGroup getParent();
    /**
     * Registers the group. Any groups contained within this group is not
     * registered.
     * 
     * @param domain
     *            the domain to register on
     * @throws IllegalStateException
     *             if this group is already registered
     * @throws Exception 
     */
    void register(String domain) throws JMException;

    /**
     * Registers the group. Any groups contained within this group is not
     * registered.
     * 
     * @param domain
     *            the domain to register on
     * @throws IllegalStateException
     *             if this group or any of its sub groups are already registered
     * @throws Exception
     */
    void registerAll(JmxRegistrant namer) throws JMException;

    /**
     * Remove this group from its parent.
     */
    void remove();

    /**
     * If any sub groups has registered these will also be unregistered.
     * 
     * @throws Exception
     */
    void unregister() throws JMException;
}
