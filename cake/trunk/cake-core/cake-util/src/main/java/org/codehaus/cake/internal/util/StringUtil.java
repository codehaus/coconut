/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.util;

/**
 * Various String utils.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class StringUtil {
	///CLOVER:OFF
	/** Cannot instantiate. */
	private StringUtil() {
	}
	///CLOVER:ON

	public static String capitalize(String capitalizeMe) {
		if (capitalizeMe == null) {
			throw new NullPointerException("capitalizeMe is null");
		} else if (capitalizeMe.length() == 0) {
			return capitalizeMe;
		}
		return capitalizeMe.substring(0, 1).toUpperCase()
				+ capitalizeMe.substring(1);
	}
}
