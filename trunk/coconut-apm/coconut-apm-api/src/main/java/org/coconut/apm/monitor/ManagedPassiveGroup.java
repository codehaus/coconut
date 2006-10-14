/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.monitor;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;

import org.coconut.apm.Apm;
import org.coconut.apm.monitor.DefaultMetricManager.Foo;
import org.coconut.apm.spi.NumberDynamicBean;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ManagedPassiveGroup {
    private NumberDynamicBean bean;

    private List l = new ArrayList();

    public <T extends Apm> T addMetric(T r) {
        l.add(r);
        return r;
    }

    /**
     * 
     */
    public synchronized void startAndRegister(String name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        bean = new NumberDynamicBean("No Description");
        // deregister allready registered?
        for (Object o : l) {
            if (o instanceof Foo) {
                o = ((Foo) o).o;
            }
            if (o instanceof Apm) {
                Apm p = (Apm) o;
                p.configureJMX(bean);
            }
        }
        bean.register(name);
    }
    
    /**
     * 
     */
    public void stopAndUnregister() throws MBeanRegistrationException {
        bean.unregister();
    }
}
