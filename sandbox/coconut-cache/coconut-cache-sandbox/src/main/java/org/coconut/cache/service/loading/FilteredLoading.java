/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.core.Transformer;
import org.coconut.core.Transformers;
import org.coconut.filter.Filters;
import org.junit.Test;

public class FilteredLoading extends AbstractLoadingTestBundle {
    @Test(expected = NullPointerException.class)
    public void loadNPE() {
        loading().filteredLoad(null);
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE1() {
        loading().filteredLoad(null, AttributeMaps.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE2() {
        loading().filteredLoad(Filters.TRUE, (AttributeMap) null);
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE3() {
        loading().filteredLoad(null, (Transformer) Transformers.passThroughTransformer());
    }

    @Test(expected = NullPointerException.class)
    public void loadNPE4() {
        loading().filteredLoad(Filters.TRUE, (Transformer) null);
    }
    
    //TODO more tests
}
