package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionUtils.M1;

import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMaps;
import org.junit.Test;

/**
 * Tests all forcedLoad* methods of {@link CacheLoadingService}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ExplicitLoading.java 355 2007-06-16 08:05:33Z kasper $
 */
public class ExplicitForcedLoading extends AbstractLoadingTestBundle {

    @Test(expected = NullPointerException.class)
    public void forcedloadNPE() {
        loading().forceLoad(null);
    }

    @Test(expected = NullPointerException.class)
    public void testForcedLoadNullPointer1() {
        loading().forceLoad(null, AttributeMaps.EMPTY_MAP);
    }

    @Test(expected = NullPointerException.class)
    public void testForcedLoadNullPointer2() {
        loading().forceLoad(1, null);
    }

    @Test
    public void forceLoadSingleIntTwice() {
        loading().forceLoad(M1.getKey());
        awaitLoad();
        assertEquals(1, loader.getNumberOfLoads());
        loading().forceLoad(M1.getKey());
        awaitLoad();
        assertEquals(2, loader.getNumberOfLoads());
    }
}
