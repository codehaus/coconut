/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.analyzer.offline;

import org.coconut.cache.Cache;

public interface OfflineResult {
    Cache.HitStat getTotal();

    int getStepWidth();

    int getSteps();

    Cache.HitStat getResult(int step);
}
