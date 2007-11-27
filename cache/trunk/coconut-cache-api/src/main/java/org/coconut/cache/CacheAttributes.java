/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.IdentityHashMap;

import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.CreationTimeAttribute;
import org.coconut.attribute.common.HitsAttribute;
import org.coconut.attribute.common.LastModifiedTimeAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.attribute.common.TimeToRefreshAttribute;
import org.coconut.attribute.spi.DurationAttribute;
import org.coconut.attribute.spi.LongAttribute;

/**
 * The main purpose of a cache attribute is to support custom metadata associated with
 * each element in the cache. Currently is only possible to set properties when adding
 * elements through a cache loader . See
 * {@link org.coconut.cache.service.loading.CacheLoader} for examples.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CacheAttributes {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private CacheAttributes() {}
    // /CLOVER:ON

    public static final DurationAttribute TIME_TO_REFRESH_ATR = TimeToRefreshAttribute.INSTANCE;

    public static final TimeToLiveAttribute TIME_TO_LIVE_ATR = TimeToLiveAttribute.INSTANCE;

    public static final SizeAttribute Size_ATR = SizeAttribute.INSTANCE;

    public static final CostAttribute COST_ATR = CostAttribute.INSTANCE;

    public static final CreationTimeAttribute CREATION_TIME_ATR = CreationTimeAttribute.INSTANCE;

    public static final LastModifiedTimeAttribute Last_Modified_ATR = LastModifiedTimeAttribute.INSTANCE;

    public static final HitsAttribute HITS_ATR = HitsAttribute.INSTANCE;

}
