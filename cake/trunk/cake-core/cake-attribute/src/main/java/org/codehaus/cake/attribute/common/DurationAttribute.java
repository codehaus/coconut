/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute.common;

import java.util.concurrent.TimeUnit;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.attribute.LongAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class DurationAttribute extends LongAttribute {

	/** The default value of this attribute. */
	protected static final long DEFAULT_DURATION = Long.MAX_VALUE;

	/** A value that indicates forever. */
	protected static final long FOREVER = Long.MAX_VALUE;

	/** The time unit of this attribute. */
	protected static final TimeUnit TIME_UNIT = TimeUnit.NANOSECONDS;

	/**
	 * Creates a new DurationAttribute.
	 * 
	 * @param name
	 *            the name of the attribute
	 */
	public DurationAttribute(String name) {
		super(name, DEFAULT_DURATION);
	}

	/**
	 * Analogous to {@link #getValue(AttributeMap)} except taking a parameter
	 * indicating what time unit the value should be returned in.
	 * 
	 * @param attributes
	 *            the attribute map to retrieve the value of this attribute from
	 * @param unit
	 *            the time unit to return the value in
	 * @return the value of this attribute
	 */
	public long getValue(AttributeMap attributes, TimeUnit unit) {
		return convertTo(attributes.get(this), unit);
	}

	public long getValue(AttributeMap attributes, TimeUnit unit,
			long defaultValue) {
		long val = attributes.get(this,0);
		if (val == 0) {
			return defaultValue;
		} else {
			return convertTo(val, unit);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isValid(long value) {
		return value > 0;
	}

	public AttributeMap set(AttributeMap attributes, Long duration,
			TimeUnit unit) {
		return set(attributes, duration.longValue(), unit);
	}

	public AttributeMap set(AttributeMap attributes, long duration,
			TimeUnit unit) {
		return set(attributes, convertFrom(duration, unit));
	}

	/**
	 * Returns an immutable AttributeMap containing only this attribute mapping
	 * to the specified value.
	 * 
	 * @param value
	 *            the value to create the singleton from
	 * @param unit
	 *            the time unit of the value
	 * @return an AttributeMap containing only this attribute mapping to the
	 *         specified value
	 */
	public AttributeMap singleton(long value, TimeUnit unit) {
		return super.singleton(convertFrom(value, unit));
	}

	public long convertFrom(long value, TimeUnit unit) {
		if (value == Long.MAX_VALUE) {
			return Long.MAX_VALUE;
		} else {
			return unit.toNanos(value);
		}
	}

	public long convertTo(long value, TimeUnit unit) {
		if (value == Long.MAX_VALUE) {
			return Long.MAX_VALUE;
		} else {
			return unit.convert(value, TimeUnit.NANOSECONDS);
		}
	}
}
