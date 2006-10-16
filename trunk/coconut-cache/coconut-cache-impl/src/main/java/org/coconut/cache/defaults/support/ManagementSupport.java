/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.support;

import org.coconut.apm.ApmGroup;
import org.coconut.apm.defaults.DefaultApmGroup;
import org.coconut.apm.monitor.DefaultMetricManager;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.AbstractCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ManagementSupport {

    private DefaultMetricManager dmm = new DefaultMetricManager();
    private final ApmGroup group;
    
    public ManagementSupport(CacheConfiguration<?, ?> conf) {
        String name = "org.coconut.cache:name=" + conf.getName()
                + ",group=$1,subgroup=$2";
        group = DefaultApmGroup.newRoot(name, conf.jmx().getMBeanServer());
    }
    public ApmGroup getGroup() {
        return group;
    }

    public void start(AbstractCache cache) {
        if (cache.getConfiguration().jmx().isRegister()) {
            try {
                group.register("org.coconut.cache:name=$0,group=$1,subgroup=$2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
