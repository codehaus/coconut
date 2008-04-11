package org.codehaus.cake.attribute.common;

import org.codehaus.cake.attribute.LongAttribute;

public abstract class TimeInstanceAttribute extends LongAttribute {
	/** The default value of this attribute. */
	static final long DEFAULT_VALUE = 0;

	/** serialVersionUID. */
	private static final long serialVersionUID = -2353351535602223603L;

	/** Creates a new SizeAttribute. */
	public TimeInstanceAttribute(String name) {
		super(name, DEFAULT_VALUE);
	}

	/** {@inheritDoc} */
	@Override
	public void checkValid(long time) {
		if (time < 0) {
			throw new IllegalArgumentException(getName() + " was negative ("
					+ getName() + " = " + time + ")");
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isValid(long time) {
		return time >= 0;
	}
}
