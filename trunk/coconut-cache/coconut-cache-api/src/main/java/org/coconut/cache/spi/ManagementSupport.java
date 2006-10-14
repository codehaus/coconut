/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;

import org.coconut.apm.Apm;
import org.coconut.apm.ApmGroup;
import org.coconut.apm.monitor.DefaultMetricManager;
import org.coconut.cache.CacheConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ManagementSupport {


    private DefaultMetricManager dmm = new DefaultMetricManager();

    public ManagementSupport(CacheConfiguration<?, ?> conf) {

    }

    public void add(Apm o) {
//        g.add(o);
        dmm.addMetric(o);
    }

    public void foo(){
        try {
            dmm.startAndRegister("org.coconut.cache:name=Cache");
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
    }
}
