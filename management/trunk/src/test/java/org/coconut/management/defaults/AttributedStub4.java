/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import org.coconut.management.annotation.ManagedAttribute;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class AttributedStub4 {

	private int integer;

	private String readOnlyString;

	@ManagedAttribute
	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	@ManagedAttribute(readOnly=true)
	public String getReadOnlyString() {
		return readOnlyString;
	}

	public void setReadOnlyString(String readOnlyString) {
		this.readOnlyString = readOnlyString;
	}

}
