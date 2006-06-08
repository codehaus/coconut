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
public interface ProfileMXBean {

    long getTotal();

    long getTotalSamplings();

    double getTotalAverage();

    String addProbe(String uniqueName);
    //
    
    //implementations might have method that directly return
    //an mbean
    String registerMovingAverage(int samplings);
    String registerMovingAverage(String name, int samplings);
    
    void stuff();
    
    StuffInfo getInfo();
    StuffInfo getInfo2();
}
