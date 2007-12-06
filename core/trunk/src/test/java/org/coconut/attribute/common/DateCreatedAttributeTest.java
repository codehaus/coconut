/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.spi.AbstractValueTest;


/**
 * Tests the {@link DateCreatedAttribute}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DateCreatedAttributeTest extends AbstractValueTest {

    public DateCreatedAttributeTest() {
        super(DateCreatedAttribute.INSTANCE, NON_NEGATIV_LONGS, NEGATIV_LONGS);
    }

}
