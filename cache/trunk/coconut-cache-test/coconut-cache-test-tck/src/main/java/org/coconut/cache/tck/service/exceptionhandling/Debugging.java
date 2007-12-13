/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.exceptionhandling;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.coconut.cache.Cache;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.Loggers;
import org.coconut.core.Logger.Level;
import org.coconut.test.throwables.RuntimeException1;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Debugging extends AbstractCacheTCKTest {

    @Test
    public void debugging() {
        conf.setDefaultLogger(Loggers.printStreamLogger(Level.Trace, new PrintStream(
                new NullOutputStream())));
        // conf.setDefaultLogger(Loggers.systemOutLogger(Level.Trace));
        conf.event().setEnabled(true);
        conf.loading().setLoader(new IntegerToStringLoader());
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void initialize(Initializer cli) {}

            @Override
            public void start(CacheServiceManagerService service) {}
        });
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService service) {}
        });
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void started(Cache cache) {}
        });

        setCache();
        prestart();
    }

    @Test
    public void debuggingNoTrace() {
        conf.setDefaultLogger(Loggers.printStreamLogger(Level.Debug, new PrintStream(
                new NullOutputStream())));
        conf.event().setEnabled(true);
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void initialize(Initializer cli) {}

            @Override
            public void start(CacheServiceManagerService service) {}
        });
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void start(CacheServiceManagerService service) {}
        });
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void started(Cache cache) {}
        });

        setCache();
        prestart();
    }

    @Test(expected = RuntimeException1.class)
    public void debuggingInitializeFailed() {
        conf.setDefaultLogger(Loggers.printStreamLogger(Level.Debug, new PrintStream(
                new NullOutputStream())));
        //conf.setDefaultLogger(Loggers.systemOutLogger(Level.Warn));
        conf.serviceManager().add(new AbstractCacheLifecycle() {
            @Override
            public void initialize(Initializer cli) {
                throw new RuntimeException1();
            }
        });
        setCache();
        prestart();
    }

    static class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {}
    }
}
