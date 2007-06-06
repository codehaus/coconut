/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ReflectionException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
abstract class AbstractAttribute {
	final String description;

	final String attribute;

	AbstractAttribute(final String attribute, final String description) {
		if (attribute == null) {
			throw new NullPointerException("attribute is null");
		} else if (description == null) {
			throw new NullPointerException("description is null");
		}
		this.attribute = attribute;
		this.description = description;
	}

	abstract MBeanAttributeInfo getInfo() throws IntrospectionException;

	abstract boolean hasSetter();
	
	abstract boolean hasGetter();
	abstract Object getValue() throws MBeanException, ReflectionException;

	abstract Object setValue(Object o) throws MBeanException, ReflectionException;
}
