/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.monitor.management;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Statistics {

    //long getLastSampleTime(); 
    long getStartTime();
    String getName();
    String getDescription();
    String getUnit();
    void reset();
    void remove();
    //void scale(double scale);
}
