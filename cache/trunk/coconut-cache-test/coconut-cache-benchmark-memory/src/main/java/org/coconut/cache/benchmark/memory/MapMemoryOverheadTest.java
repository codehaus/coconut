/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import java.util.Map;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MapMemoryOverheadTest {

    private final Class<? extends Map> c;

    /**
     * @param c
     */
    public MapMemoryOverheadTest(final Class<? extends Map> c) {
        this.c = c;
    }

    public void test(MemoryTestResult runs) throws Exception {
        runs.start();
        for (int i = 0; i < runs.getTotal(); i++) {
            runs.set(i, c.newInstance());
        }
        final int iterations = runs.getIterations();
        for (int i = 0; i < runs.getTotal(); i++) {
            Map<String, Integer> c = runs.get(i);
            for (int j = 0; j < iterations; j++) {
                c.put(getString(j), getInt(j));
            }
        }
        runs.stop();
    }

    /**
	 * @param j
	 * @return
	 */
	private Integer getInt(int j) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param j
	 * @return
	 */
	private String getString(int j) {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
        return c.getCanonicalName().toString();
    }
}
