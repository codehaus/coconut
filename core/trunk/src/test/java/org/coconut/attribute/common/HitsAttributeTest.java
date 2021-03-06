/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.spi.AbstractValueTest;


/**
 * Tests the {@link HitsAttribute}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class HitsAttributeTest extends AbstractValueTest {

    public HitsAttributeTest() {
        super(HitsAttribute.INSTANCE, NON_NEGATIV_LONGS, NEGATIV_LONGS);
    }

}
