/**
 * 
 */
package org.coconut.management;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.core.Named;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ApmGroup extends Named {

    /**
     * Adds an object to the group. The
     * 
     * @param r
     * @return
     * @throws NullPointerException
     *             if the specified object is <tt>null</tt>
     */
    <T> T add(T r);

    /**
     * (optional)
     * 
     * @param <T>
     * @param r
     * @param time
     * @param unit
     * @return
     */
    <T extends Runnable> T add(T r, long time, TimeUnit unit);

    ApmGroup addGroup(String name, String description);

    ApmGroup addGroup(String name, String description, boolean register);

    Collection<?> getAll();

    String getDescription();

    Collection<ApmGroup> getGroups();

    ApmGroup getParentGroup();

    void register(String name) throws Exception;

    void unregister() throws Exception;

    void remove();
}
