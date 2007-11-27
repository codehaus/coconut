/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.spi.LongAttribute;

public class HitsAttribute extends LongAttribute {
    public final static HitsAttribute INSTANCE = new HitsAttribute();
    private HitsAttribute() {
        super("Hits");
    }

    @Override
    protected void checkValid(long hits) {
        if (hits < 0) {
            throw new IllegalArgumentException("invalid hit count (hits = " + hits + ")");
        }
    }

    @Override
    public boolean isValid(long hits) {
        return hits >= 0;
    }

    /**
     * The <tt>Hits</tt> attribute indicates the number of hits for a cache element. The
     * mapped value must be of a type <tt>long</tt> between 0 and {@link Long#MAX_VALUE}.
     * 
     * @see #setHits(AttributeMap, long)
     * @see #getHits(AttributeMap)
     */
    public static final String HITS = "hits";
}
