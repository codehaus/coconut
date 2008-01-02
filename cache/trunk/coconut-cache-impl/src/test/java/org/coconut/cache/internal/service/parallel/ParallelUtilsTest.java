/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.parallel;

import org.junit.Test;

/**
 * This class tests {@link ParallelUtils}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ManagementUtilsTest.java 520 2007-12-21 17:53:31Z kasper $
 */
public class ParallelUtilsTest {

    @Test(expected = NullPointerException.class)
    public void wrapServiceNPE() {
        ParallelUtils.wrapService(null);
    }
}
