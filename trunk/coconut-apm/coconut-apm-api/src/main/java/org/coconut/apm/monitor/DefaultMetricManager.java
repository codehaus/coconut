/**
 * 
 */
package org.coconut.apm.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;

import org.coconut.apm.Apm;
import org.coconut.apm.spi.NumberDynamicBean;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultMetricManager {

    private NumberDynamicBean bean;

    private List l = new ArrayList();

    private final ScheduledExecutorService ses = Executors
            .newSingleThreadScheduledExecutor();

    <T extends Apm> void addAll(Collection<? extends T> r) {
        for (T t : r) {
            addMetric(t);
        }
    }

    public <T extends Apm> T addMetric(T r) {
        l.add(r);
        return r;
    }

    public <T extends Runnable> T add(T r, long time, TimeUnit unit) {
        Foo f = new Foo();
        f.o = r;
        f.time = time;
        f.unit = unit;
        l.add(f);
        return r;
    }

    public synchronized void start() {
        for (Object o : l) {
            if (o instanceof Foo) {
                Foo f = (Foo) o;
                ses.scheduleAtFixedRate(f.o, 0, f.time, f.unit);
            } else if (o instanceof Delayed) {
                ses.scheduleAtFixedRate((Runnable) o, 0, ((Delayed) o)
                        .getDelay(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS);
            }
        }
    }

    /**
     * 
     */
    public synchronized void startAndRegister(String name)
            throws InstanceAlreadyExistsException, MBeanRegistrationException {
        start();
        bean = new NumberDynamicBean("No Description");
        // deregister allready registered?
        for (Object o : l) {
            if (o instanceof Foo) {
                o = ((Foo) o).o;
            }
            if (o instanceof Apm) {
                Apm p = (Apm) o;
                p.configureJMX(bean);
            }
        }
        bean.register(name);
    }

    /**
     * 
     */
    public synchronized void stopAndUnregister() throws MBeanRegistrationException {
        ses.shutdown();
        bean.unregister();
    }

    static class Foo {
        Runnable o;

        long time;

        TimeUnit unit;
    }
}
