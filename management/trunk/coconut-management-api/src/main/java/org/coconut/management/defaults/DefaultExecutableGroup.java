/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.JMException;

import org.coconut.management.ManagedExecutableGroup;
import org.coconut.management.ManagedGroup;
import org.coconut.management.spi.NumberDynamicBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultExecutableGroup extends DefaultManagedGroup implements
        ManagedExecutableGroup {
    private final ScheduledExecutorService ses;

    /**
     * @param name
     * @param register
     */
    public DefaultExecutableGroup(String name,String description, boolean register) {
        this(name, description, register, Executors.newSingleThreadScheduledExecutor());
    }

    /**
     * @param e
     * @return
     * @see java.util.Collection#add(java.lang.Object)
     */
    public ManagedExecutableGroup add(Object e) {
        super.add(e);
        return this;
    }

    /**
     * @param name
     * @param register
     */
    public DefaultExecutableGroup(String name, String description, boolean register,
            ScheduledExecutorService ses) {
        super(name, description, register);
        this.ses = ses;
    }

    /**
     * @see org.coconut.apm.defaults.DefaultApmGroup#add(java.lang.Runnable,
     *      long, java.util.concurrent.TimeUnit)
     */
    public <T extends Runnable> ManagedExecutableGroup add(T r, long time, TimeUnit unit) {
        super.add(new Foo(r, time, unit));
        return this;
    }

    public synchronized void start() {
        for (Object o : getAll()) {
            if (o instanceof Foo) {
                Foo f = (Foo) o;
                ses.scheduleAtFixedRate(f.o, 0, f.time, f.unit);
            }
        }
    }

    public synchronized void startAndRegister(String name) throws JMException {
        start();
        super.registerGroup(name);
    }

    public synchronized void stop() {
        ses.shutdown();
    }

    public synchronized void stopAndUnregister() throws JMException {
        stop();
        super.unregister();
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

    /**
     * @see org.coconut.management.ExecutableGroup#reSchedule(java.lang.Runnable,
     *      long, java.util.concurrent.TimeUnit)
     */
    public <T extends Runnable> ManagedExecutableGroup reSchedule(T r, long time, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }
}
