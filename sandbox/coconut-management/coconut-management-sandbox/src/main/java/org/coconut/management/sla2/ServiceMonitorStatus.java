/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla2;

/**
 * Immutable
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ServiceMonitorStatus<T extends Enum> {

    /**
     * Returns the id.
     * 
     * @return
     */
    long statusId(); // if status change -> statusId++

    long timestamp();

    /**
     * An application dependent.
     */
    T getStatus();

    /**
     * Readable description of the status.
     */
    String getDescription();
    
    Throwable getException();
}
