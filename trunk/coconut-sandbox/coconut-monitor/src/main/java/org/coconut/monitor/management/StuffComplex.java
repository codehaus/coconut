/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.monitor.management;

import java.util.Arrays;
import java.util.List;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class StuffComplex extends LazyCompositeData implements StuffInfo {

    private static final CompositeType lockInfoCompositeType;
    static {
        CompositeType l = null;
        try {
            l = (CompositeType) MappedMXBeanType.toOpenType(StuffInfo.class);
        } catch (OpenDataException e) {
            e.printStackTrace();
            // Should never reach here
            assert (false);
        }
        lockInfoCompositeType = l;
    }

    protected CompositeData getCompositeData() {
        // CONTENTS OF THIS ARRAY MUST BE SYNCHRONIZED WITH
        // lockInfoItemNames!
        final Object[] lockInfoItemValues = { 8, 77, getValues1(),getFoo() };

        try {
            return new CompositeDataSupport(lockInfoCompositeType,
                    lockInfoItemNames, lockInfoItemValues);
        } catch (OpenDataException e) {
            throw new IllegalStateException("gfd");
        }
    }

    private static final String CLASS_NAME = "high";

    private static final String IDENTITY_HASH_CODE = "low";

    private static final String JJ = "values";

    private static final String[] lockInfoItemNames = { CLASS_NAME,
            IDENTITY_HASH_CODE, JJ,"foo" };

    /**
     * @see org.coconut.monitor.management.StuffInfo#getHigh()
     */
    public int getHigh() {
        // TODO Auto-generated method stub
        return 8;
    }

    /**
     * @see org.coconut.monitor.management.StuffInfo#getLow()
     */
    public int getLow() {
        // TODO Auto-generated method stub
        return 77;
    }

    /**
     * @see org.coconut.monitor.management.StuffInfo#getJj()
     */
    public List<Long> getJj() {
        return Arrays.asList(new Long[] { 87l, 6l, 9l, 6l, 9l });
    }

    public Long[] getValues1() {
        return new Long[] { 87l, 6l, 9l, 6l, 9l };
    }

    /**
     * @see org.coconut.monitor.management.StuffInfo#getValues()
     */
    public long[] getValues() {
        return new long[] { 87l, 6l, 9l, 6l, 9l };
    }

    /**
     * @see org.coconut.monitor.management.StuffInfo#getFoo()
     */
    public String[] getFoo() {
        return new String[] { "erer", "lwkjer", "wekljelrkjt", "kljlj" };
    }

}
