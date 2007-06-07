/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy;

import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PolicyAttributes {
    public static final String CREATION_TIME = "creation_time";

    public static final String HITS = "hits";

    public static final String COST = "cost";

    public static final String SIZE = "size";

    public static long getSize(AttributeMap attributes) {
        long size = attributes.getLong(SIZE,
                ReplacementPolicy.DEFAULT_SIZE);
        return size;
    }
    
    public static double getCost(AttributeMap attributes) {
        double cost = attributes.getDouble(COST,
                ReplacementPolicy.DEFAULT_COST);
        return cost;
    }
}
