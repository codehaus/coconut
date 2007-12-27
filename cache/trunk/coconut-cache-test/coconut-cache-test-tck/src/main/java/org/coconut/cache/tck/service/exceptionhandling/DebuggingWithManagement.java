/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.exceptionhandling;

import java.io.PrintStream;

import org.coconut.cache.Cache;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.RequireService;
import org.coconut.cache.tck.service.exceptionhandling.Debugging.NullOutputStream;
import org.coconut.core.Loggers;
import org.coconut.core.Logger.Level;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@RequireService( { CacheManagementService.class })
public class DebuggingWithManagement extends AbstractCacheTCKTest {

    @Test
    public void debuggingOk() {
        conf.management().setEnabled(true);
        conf.setDefaultLogger(Loggers.printStreamLogger(Level.Trace, new PrintStream(new NullOutputStream())));
        conf.serviceManager().add(new ManagedLifecycle() {
            public void manage(ManagedGroup parent) {}
        });
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService service) {}
        });
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void started(Cache cache) {}
        });

        init();
        prestart();
    }
}
