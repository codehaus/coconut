/**
 * 
 */
package org.coconut.management;

import java.util.Collection;

import org.coconut.core.Named;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ManagedGroup extends Named {

    /**
     * Adds an object to the group. The
     * 
     * @param r
     * @return
     * @throws NullPointerException
     *             if the specified object is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the object has already been registered
     */
    <T> T add(T r);

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
     * Registers the group.
     * 
     * @param name
     * @throws Exception
     */
    void register(String name) throws Exception;

    void unregister() throws Exception;

    /**
     * Remove this group from its parent.
     */
    void remove();
}
