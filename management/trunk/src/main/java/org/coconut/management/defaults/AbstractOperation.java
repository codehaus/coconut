/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import javax.management.IntrospectionException;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
abstract class AbstractOperation {

	final String description;

	final String name;

	AbstractOperation(final String name, final String description) {
		if (name == null) {
			throw new NullPointerException("name is null");
		} else if (description == null) {
			throw new NullPointerException("description is null");
		}
		this.name = name;
		this.description = description;
	}
	abstract MBeanOperationInfo getInfo() throws IntrospectionException;
	abstract Object invoke(Object... arguments) throws ReflectionException;
	
	abstract String[] getSignature();
}
