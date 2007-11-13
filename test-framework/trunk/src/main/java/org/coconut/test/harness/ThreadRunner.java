package org.coconut.test.harness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import org.coconut.internal.picocontainer.PicoContainer;
import org.coconut.test.LoopHelpers;

public class ThreadRunner implements Runnable {
    public final ThreadOperation[] runnables;

    final LoopHelpers.SimpleRandom rng;

    final CyclicBarrier outer;

    final CyclicBarrier inner;

    int operations;

    int total;

    public ThreadRunner(int id, PicoContainer tests, Map<String, String> conf, int operations,
            CyclicBarrier outer, CyclicBarrier inner) throws Exception {

        this.operations = operations;
        this.outer = outer;
        this.inner = inner;
        Collection<Runnable> col = tests.getComponentInstancesOfType(Runnable.class);
        List<ThreadOperation> map = new ArrayList<ThreadOperation>();
        for (Runnable r : col) {
            String name = (String) r.getClass().getDeclaredField("NAME").get(null);
            String p = conf.get(name);
            if (p == null) {
                p = "0.0";
            }
            float f = Float.parseFloat(p);
            if (f > 0) {
                int rnd = (int) ((f / 100.0 * 0x7FFFFFFFL));
                ThreadOperation o = new ThreadOperation(name, rnd, r);
                map.add(o);
            }
        }
        runnables = map.toArray(new ThreadOperation[0]);

        rng = new LoopHelpers.SimpleRandom((id + 1) * 8862213513L);
        rng.next();
    }

    boolean isDone() {
        return operations-- <= 0;
    }

    public void run() {
        ThreadOperation o = null;
        try {
            outer.await();
            inner.await();
            while (!isDone()) {
                for (int i = 0; i < runnables.length; i++) {
                    o = runnables[i];
                    int rnd = rng.next();
                    if (rnd <= o.rnd) {
                        o.r.run();
                        o.invocations++;
                    }
                }
            }
            inner.await();
            outer.await();
        } catch (Exception ex) {
            if (o != null) {
                o.t = ex;
            }
            ex.printStackTrace();
        }
    }
}
