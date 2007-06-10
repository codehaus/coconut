/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class KeyValues {

	private final static long randomSeed = 2342723;

	private final static int MAXIMUM_ELEMENTS = 100000;

	private final static String[] keys = new String[100000];

	private final static Integer[] values;

	static {
		Random rnd = new Random(randomSeed);
		ArrayList<Integer> list = new ArrayList<Integer>(MAXIMUM_ELEMENTS);
		
		for (int i = 0; i < MAXIMUM_ELEMENTS; i++) {
			list.add(i);
		}
		Collections.shuffle(list, rnd);
		
		values = list.toArray(new Integer[MAXIMUM_ELEMENTS]);
		for (int i = 0; i < values.length; i++) {
			keys[i] = Integer.toHexString(values[i]);
		}

	}

	public static Integer getInt(int i) {
		return values[i];
	}

	public static String getString(int i) {
		return keys[i];
	}
}
