/**
 * 
 */
package org.coconut.apm;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.core.Named;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ApmGroup extends Named {

    <T extends Runnable> ApmGroup add(T r, long time, TimeUnit unit);

    ApmGroup add(Object r);

    Collection<?> getAll();

    void setDescription(String name);

    String getDescription();

    ApmGroup addGroup(String name);

    ApmGroup addGroup(String name, boolean register);

    ApmGroup getParentGroup();

    Collection<ApmGroup> getGroups();

    void remove();

    void register(String name) throws Exception;
}
