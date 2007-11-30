/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class LongSamplingCounter {
    private long high = Long.MIN_VALUE;

    private long low = Long.MAX_VALUE;

    private long latest = Long.MIN_VALUE;

    private long total;

    private long samplings;

    /**
     * @param name
     */

    public LongSamplingCounter(String name, String description) {}

    public synchronized void report(long value) {
        if (value < low) {
            low = value;
        }
        if (value > high) {
            high = value;
        }
        latest = value;
        total += value;
        samplings++;
    }

}
