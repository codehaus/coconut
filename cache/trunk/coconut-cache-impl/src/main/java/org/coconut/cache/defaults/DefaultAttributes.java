/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface DefaultAttributes {
    
    String TIME_TO_LIVE_NANO = "expiration_time";

    String CREATION_TIME = "creation_time";

    String LAST_MODIFIED_TIME = "last_modified";
    
    String COST = "cost";
    String SIZE = "size";
}
