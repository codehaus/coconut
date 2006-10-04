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
public interface ApmGroup extends Named, Collection<Apm> {

    /**
     * (optional)
     * 
     * @param <T>
     * @param r
     * @param time
     * @param unit
     * @return
     */
    <T extends Apm> T addSampler(T r, long time, TimeUnit unit);

    ApmGroup addGroup(String name);

    ApmGroup getParentGroup();

    Collection<ApmGroup> getGroups();

    void removeFromParentGroup();
}