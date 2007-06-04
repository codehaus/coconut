/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.storage;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class StorageConfiguration {
    /**
     * WRITE_THROUGH = put/load value, make avilable for others, persist,
     * return. Is there any _observable_ difference between WRITE_THROUGH &
     * WRITE_THROUGH_ASYNC??? I can't think of a reason why we want all but the
     * caller to get() to see a value???
     * <p>
     * WRITE_THROUGH_SAFE = put/load value, persist, make avilable for others,
     * return
     * <p>
     * WRITE_THROUGH_ASYNC = put/load value, make available for other, return,
     * persist asynchronously later
     * <p>
     * WRITE_BACK = put/load value, make available for other, return, persist at
     * latests possible time. perhaps drop WRITE_THROUGH_ASYNC
     */
    public static enum StorageStrategy {
        WRITE_BACK, WRITE_THROUGH, WRITE_THROUGH_ASYNC, WRITE_THROUGH_SAFE;
    }
}
