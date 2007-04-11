/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.util.concurrent.Executor;

import org.coconut.cache.spi.CacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface OldInternalCacheService extends CacheService {
    boolean isDummy();

    void doStart() throws Exception;

    void shutdown(Executor callback) throws Exception;
}
