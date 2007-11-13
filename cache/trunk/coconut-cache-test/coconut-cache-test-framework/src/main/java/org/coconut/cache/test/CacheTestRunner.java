/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheServices;
import org.coconut.cache.test.keys.RandomKeyGenerator;
import org.coconut.cache.test.operations.CacheOperations;
import org.coconut.cache.test.operations.LoadingServiceOperations;
import org.coconut.internal.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.test.LoopHelpers;
import org.coconut.test.harness.Operation;
import org.coconut.test.harness.ThreadOperation;
import org.coconut.test.harness.ThreadRunner;

public class CacheTestRunner {

    private final DefaultPicoContainer p = new DefaultPicoContainer();

    private final DefaultPicoContainer tests = new DefaultPicoContainer(
            new ConstructorInjectionComponentAdapterFactory(), p);

    private final CacheConfiguration<?, ?> configuration;

    private final Cache c;

    public CacheTestRunner(InputStream conf, Class<? extends Cache> type) throws Exception {
        this(CacheConfiguration.loadConfigurationFrom(conf), type);
    }

    public CacheTestRunner(String conf, Class<? extends Cache> type) throws Exception {
        this(CacheConfiguration.loadConfigurationFrom(new FileInputStream(conf)), type);
    }

    public CacheTestRunner(CacheConfiguration<?, ?> conf, Class<? extends Cache> type) {
        this.configuration = conf;
        c = conf.newCacheInstance(type);
        for (Object o : CacheServices.servicemanager(c).getAllServices().values()) {
            p.registerComponentInstance(o);
        }
        p.registerComponentInstance(c);
        p.registerComponentInstance(conf);
        for (Object o : conf.getAllConfigurations()) {
            p.registerComponentInstance(o);
        }
        p.registerComponentImplementation(RandomKeyGenerator.class);
        for (Class c : LoadingServiceOperations.col) {
            tests.registerComponentImplementation(c);
        }
        for (Class c : CacheOperations.col) {
            tests.registerComponentImplementation(c);
        }

    }

    public void start() throws Exception {

        int i = 10;
        LoopHelpers.BarrierTimer timer = new LoopHelpers.BarrierTimer();
        CyclicBarrier barrier = new CyclicBarrier(i + 1);
        CyclicBarrier inner = new CyclicBarrier(i + 1, timer);
        final ExecutorService pool = Executors.newCachedThreadPool();
        int nops = 500000;
        List<ThreadRunner> runners = new ArrayList<ThreadRunner>();
        for (int t = 0; t < i; ++t) {
            ThreadRunner runner = new ThreadRunner(t, tests, configuration.getProperties(), nops,
                    barrier, inner);
            runners.add(runner);
            pool.execute(runner);
        }

        barrier.await(); // start
        System.out.println("hmm");
        int threadCount = ManagementFactory.getThreadMXBean().getPeakThreadCount();
        inner.await(); // start
        inner.await(); // wait on stop
        System.out.println("Threads was started "
                + (ManagementFactory.getThreadMXBean().getPeakThreadCount() - threadCount));
        barrier.await(); // stop
        long time = timer.getTime();
        long tpo = time / (i * (long) nops);
        System.out.print(LoopHelpers.rightJustify(tpo) + " ns per op");
        double secs = ((double) (time)) / 1000000000.0;
        System.out.println("\t " + secs + "s run time");
        c.shutdown();
        pool.shutdown();
        if (!c.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("could not stop cache");
        }
        Map<String, Operation> results = new HashMap<String, Operation>();
        for (ThreadRunner tr : runners) {
            for (ThreadOperation to : tr.runnables) {
                if (!results.containsKey(to.operation)) {
                    results.put(to.operation, new Operation(to.operation));
                }
                results.get(to.operation).invocations += to.invocations;
            }
        }
        for (Operation oper : results.values()) {
            System.out.println(oper);
        }
        System.out.println("bye");
    }

}
