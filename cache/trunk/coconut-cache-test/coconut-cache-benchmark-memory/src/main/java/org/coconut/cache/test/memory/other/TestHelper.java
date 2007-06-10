/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.memory.other;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class TestHelper {
	private static final Runtime RUNTIME = Runtime.getRuntime();

	private Object o;

	private Object oo;

	public static void main(String[] args) throws Exception {
		final TestHelper th = new TestHelper();
		Runnable r = new Runnable() {
			public void run() {
				th.o = new Object();
				th.oo = new Object();
			}
		};
		long m = measure(r);
		System.out.println(m);
	}

	public static long measure(Runnable r) throws Exception {
		long after = 0;
		runGC();
		long before = usedMemory();
		r.run();
		runGC();
		after = usedMemory();
		return after - before;
	}

	public static void runGC() {
		for (int r = 0; r < 4; ++r) {
			long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
			for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
				RUNTIME.gc();
				//RUNTIME.runFinalization();
				RUNTIME.gc();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// IGNORE
				}
				usedMem2 = usedMem1;
				usedMem1 = usedMemory();
			}
		}
	}

	public static long usedMemory() {
		return RUNTIME.totalMemory() - RUNTIME.freeMemory();
	}
}
