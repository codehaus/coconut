/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.management;

import org.coconut.cache.internal.service.OldInternalCacheService;
import org.coconut.cache.spi.AbstractCacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheManagementService extends AbstractCacheService
        implements OldInternalCacheService {

    /**
     * 
     */
    public AbstractCacheManagementService() {
        super("management");
    }

}
