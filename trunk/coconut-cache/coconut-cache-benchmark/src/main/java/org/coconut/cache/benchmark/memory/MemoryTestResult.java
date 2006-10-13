/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MemoryTestResult {

    private static final Runtime RUNTIME = Runtime.getRuntime();

    private long startMemory;

    long memoryUse;

    private int iterations;

    Object[] refs;

    MemoryTestResult(int testCounts, int iterations) {
        refs = new Object[testCounts];
        this.iterations = iterations;
    }

    public int getIterations() {
        return iterations;
    }

    public int getTotal() {
        return refs.length;
    }

    <T> T get(int index) {
        return (T) refs[index];
    }

    void set(int index, Object o) {
        refs[index] = o;
    }

    public void run(MemoryOverheadTest o) throws Exception {
        runGC();
        o.test(this);
        refs = null;
        runGC();
    }

    public void start() throws Exception {
        startMemory = usedMemory();
    }

    public void stop() throws Exception {
        runGC();
        Thread.sleep(100);
        runGC();
        Thread.sleep(100);
        memoryUse = usedMemory() - startMemory;
    }

    private static void runGC() throws Exception {
        for (int r = 0; r < 4; ++r) {
            long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
            for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
                RUNTIME.runFinalization();
                RUNTIME.gc();
                Thread.sleep(50);

                usedMem2 = usedMem1;
                usedMem1 = usedMemory();
            }
        }
    }

    private static long usedMemory() {
        return RUNTIME.totalMemory() - RUNTIME.freeMemory();
    }
}
