/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
class IntrospectedAttribute extends AbstractAttribute {
	private final Method getter;

	private final Method setter;

	private final Object o;


	public IntrospectedAttribute(String attribute, String description, Object o,
			Method reader, Method writer) {
		super(attribute, description);
		this.o = o;
		this.getter = reader;
		this.setter = writer;
	}

	/**
     * @param name
     * @param description
     * @param type
     * @param number
     */
	MBeanAttributeInfo getInfo() throws IntrospectionException {
		return new MBeanAttributeInfo(attribute, description, getter, setter);
	}

	Object getValue() throws MBeanException, ReflectionException {
		try {
			return getter.invoke(o, null);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof RuntimeException) {
				final String msg = "RuntimeException thrown in the getter for the attribute "
						+ attribute;
				throw new RuntimeMBeanException((RuntimeException) t, msg);
			} else if (t instanceof Error) {
				throw new RuntimeErrorException((Error) t,
						"Error thrown in the getter for the attribute " + attribute);
			} else {
				throw new MBeanException((Exception) t,
						"Exception thrown in the getter for the attribute " + attribute);
			}
		} catch (RuntimeException e) {
			throw new RuntimeOperationsException(e,
					"RuntimeException thrown trying to invoke the getter"
							+ " for the attribute " + attribute);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e, "Exception thrown trying to"
					+ " invoke the getter for the attribute " + attribute);
		} catch (Error e) {
			throw new RuntimeErrorException( e,
					"Error thrown trying to invoke the getter " + " for the attribute "
							+ attribute);
		}
	}

	Object setValue(Object o) throws ReflectionException, MBeanException {
		try {
			setter.invoke(this.o, o);
			return o;
		} catch (IllegalAccessException e) {
			// Wrap the exception.
			throw new ReflectionException(e, "IllegalAccessException"
					+ " occured trying to invoke the setter on the MBean");
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof RuntimeException) {
				final String msg = "RuntimeException thrown in the setter for the attribute "
						+ attribute;
				throw wrapRuntimeException((RuntimeException) t, msg);
			} else if (t instanceof Error) {
				throw new RuntimeErrorException((Error) t,
						"Error thrown in the MBean's setter");
			} else {
				throw new MBeanException((Exception) t,
						"Exception thrown in the MBean's setter");
			}
		}
	}

	private RuntimeException wrapRuntimeException(RuntimeException re, String msg) {
		// if (wrapRuntimeExceptions)
		return new RuntimeMBeanException(re, msg);
		// else
		// return re;
	}

	/**
     * @see org.coconut.management.defaults.AbstractAttribute#hasGetter()
     */
	@Override
	boolean hasGetter() {
		return getter != null;
	}

	/**
     * @see org.coconut.management.defaults.AbstractAttribute#hasSetter()
     */
	@Override
	boolean hasSetter() {
		return setter != null;
	}
}
