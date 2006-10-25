/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.defaults;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.coconut.apm.ApmGroup;
import org.coconut.apm.ExecutableApmGroup;
import org.coconut.apm.spi.NumberDynamicBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultExecutableApmGroup extends DefaultApmGroup implements
        ExecutableApmGroup {
    private final ScheduledExecutorService ses = Executors
            .newSingleThreadScheduledExecutor();

    /**
     * @param name
     * @param register
     */
    public DefaultExecutableApmGroup(String name, boolean register) {
        super(name, register);
    }

    /**
     * @see org.coconut.apm.defaults.DefaultApmGroup#add(java.lang.Runnable,
     *      long, java.util.concurrent.TimeUnit)
     */
    @Override
    public <T extends Runnable> T add(T r, long time, TimeUnit unit) {
        super.add(new Foo(r, time, unit));
        return r;
    }

    public synchronized void startSampling() {
        for (Object o : getAll()) {
            if (o instanceof Foo) {
                Foo f = (Foo) o;
                ses.scheduleAtFixedRate(f.o, 0, f.time, f.unit);
            }
        }
    }

    public synchronized void startAndRegister(String name) throws Exception {
        for (Object o : getAll()) {
            if (o instanceof Foo) {
                Foo f = (Foo) o;
                ses.scheduleAtFixedRate(f.o, 0, f.time, f.unit);
            }
        }
        register(name);
    }

    public synchronized void stopSampling() {
        ses.shutdown();
    }

    public synchronized void stopAndUnregister() {
        stopSampling();
    }

    /**
     * @see org.coconut.apm.defaults.DefaultApmGroup#register(org.coconut.apm.spi.NumberDynamicBean,
     *      java.lang.Object)
     */
    @Override
    void register(NumberDynamicBean bean, Object o) {
        if (o instanceof Foo) {
            super.register(bean, ((Foo) o).o);
        } else {
            super.register(bean, o);
        }
    }

    static class Foo {
        Foo(Runnable r, long time, TimeUnit unit) {
            this.o = r;
            this.time = time;
            this.unit = unit;
        }

        Runnable o;

        long time;

        TimeUnit unit;
    }
}
