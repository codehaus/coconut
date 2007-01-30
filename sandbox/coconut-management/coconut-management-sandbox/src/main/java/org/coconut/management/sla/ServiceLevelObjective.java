/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla;

import java.util.concurrent.TimeUnit;

import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ServiceLevelObjective {

    Status getStatus();

    interface Level {
        String name();
        Filter f();
    }
    interface Status {

        long getUpdateCount();
        ServiceLevelObjective getSlo();

        /**
         * Returns true if ServiceLevelObjective is enabled, otherwise false.
         */
        boolean isEnabled();

        boolean isOutOfLimit();

        String getStatus();

        double getDuration(TimeUnit unit);
    }
}
