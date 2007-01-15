/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla;

import java.util.concurrent.TimeUnit;

import org.coconut.filter.Filter;
import org.coconut.management.sla.ServiceLevelObjective.Level;
import org.coconut.management.sla2.ServiceMonitor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SlaFactory {

    public static SlaFactory newSla(String name) {
        return new SlaFactory();
    }

    public static void main(String[] args) {
        SlaFactory sla = newSla("Average Bytes written/s");
        sla.setPollInterval(100, TimeUnit.MILLISECONDS);
        ServiceMonitor warning = null;
        ServiceMonitor error = null;
        
        Level w = sla.addLevel(warning, "Warning",
                "Average Bytes written/s must be greater then 300 Bytes/s");
        Level e = sla.addLevel(error, "Error",
                "Average Bytes written/s must be greater then 200 Bytes/s");

    }

    /**
     * @param warning
     * @param string
     * @param string2
     * @return
     */
    private Level addLevel(ServiceMonitor warning, String string, String string2) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param warning
     * @param string
     * @param string2
     * @return
     */
    private Level addLevel(int level, String string, String string2) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param string
     * @param warning
     */
    private Level addLevel(String string, Filter warning) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param i
     * @param milliseconds
     */
    public void setPollInterval(long i, TimeUnit milliseconds) {
        // TODO Auto-generated method stub

    }
}
