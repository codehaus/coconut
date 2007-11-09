package org.coconut.management;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface ManagedObject {
    void manage(ManagedGroup parent);
}
