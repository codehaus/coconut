/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla2;

import java.util.Collection;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ServiceMonitorGroup<T extends Enum> extends Collection<T>,
        ServiceMonitor<T> {
    
    //hmm collection, we would want to use a CopyOnWrite collectino
    Collection<ServiceMonitor<T>> get(T type);

    Collection<ServiceMonitor<T>> getHighest();
    
    void updateAll();
}
