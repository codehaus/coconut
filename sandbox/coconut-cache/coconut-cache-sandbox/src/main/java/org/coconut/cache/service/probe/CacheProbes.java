/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.probing;

import java.util.Arrays;

import net.jcip.annotations.ThreadSafe;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheProbes {
	public static void main(String[] args) {
		StackTraceElement[] elems= Thread.currentThread().getStackTrace();
		System.out.println();
		System.out.println(Arrays.toString(elems));
		}

	public interface CacheCleared {

	}
}
