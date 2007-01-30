/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Sizeof {
    static class MyObj {
        // private Object[] foo2=;
        // List l=Arrays.asList(new Object[3]);
    }

    static class MyOj extends MyObj {
        int fo = 3;

        int fof = 3;
        // int fosa=3;
        // int foss=1;
        // int fosss=1;
    }

    static class MyOja {
        int foo = 3;

        int fo = 3;

        int fof = 3;
    }

    public static void main(String[] args) throws Exception {
        // Warm up all classes/methods we will use
        runGC();
        usedMemory();

        // Array to keep strong references to allocated objects
        final int count = 10000;
        Object[] objects = new Object[count];

        long heap1 = 0;
        Integer[] ints=new Integer[1];
        for (int i = 0; i < ints.length; i++) {
            ints[i]=i;
        }
        // Allocate count+1 objects, discard the first one
        for (int i = -1; i < count; ++i) {
            Object object = null;

            // Instantiate your data here and assign it to object

            // object = new Object ();
            // object = new Integer (i);
            Map h = new ConcurrentHashMap();
            object = h;
            for (int j=0;j<ints.length;j++) {
                h.put(ints[j], ints[j]);
            }
            //h.put("sa", " 234kjh");
            // object = new Long (i);
            // object = new String ();
            // object = new byte [128][1]

            if (i >= 0)
                objects[i] = object;
            else {
                object = null; // Discard the warm up object
                runGC();
                heap1 = usedMemory(); // Take a before heap snapshot
            }
        }

        runGC();
        long heap2 = usedMemory(); // Take an after heap snapshot:

        final int size = Math.round(((float) (heap2 - heap1)) / count);
        System.out.println("'before' heap: " + heap1 + ", 'after' heap: " + heap2);
        System.out.println("heap delta: " + (heap2 - heap1) + ", {"
                + objects[0].getClass() + "} size = " + size + " bytes");

        for (int i = 0; i < count; ++i)
            objects[i] = null;
        objects = null;
    }

    private static void runGC() throws Exception {
        // It helps to call Runtime.gc()
        // using several method calls:
        for (int r = 0; r < 4; ++r)
            _runGC();
    }

    private static void _runGC() throws Exception {
        long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
            s_runtime.runFinalization();
            s_runtime.gc();
            Thread.currentThread().yield();

            usedMem2 = usedMem1;
            usedMem1 = usedMemory();
        }
    }

    private static long usedMemory() {
        return s_runtime.totalMemory() - s_runtime.freeMemory();
    }

    private static final Runtime s_runtime = Runtime.getRuntime();

} // End of class
