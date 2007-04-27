/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.lang.reflect.Method;

import javax.management.MBeanServer;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Managements {

	public static <T> T narrow(ManagedGroup group, Class<? extends T> c) {
		return null;
	}
	
	public static Object wrap(Object o, String name, String description, Method m,
			Object... parameters) {
		return null;
	}
	
	
	public static ManagedGroupVisitor register(MBeanServer server, String domain, String... levels) {
		return null;
	}
}
