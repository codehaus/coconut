/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface GroupedApm {
    Apm getApm();

    boolean isSampler();

    /**
     * Returns the remaining delay associated with this object, in the given
     * time unit.
     * 
     * @param unit
     *            the time unit
     * @return the remaining delay; zero or negative values indicate that the
     *         delay has already elapsed
     */
    long getSampleDelay(TimeUnit unit);

    long getLastUpdateTime();

    long getNextUpdateTime();

    GroupedApm setSampleDelay(long timeout, TimeUnit unit);

    GroupedApm setMonitoringEnabled(boolean enabled);

    boolean setEnabled(boolean enabled);

    void isEnabled();
}
