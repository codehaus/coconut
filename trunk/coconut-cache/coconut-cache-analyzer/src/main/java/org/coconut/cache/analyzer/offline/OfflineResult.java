package org.coconut.cache.analyzer.offline;

import org.coconut.cache.Cache;

public interface OfflineResult {
    Cache.HitStat getTotal();

    int getStepWidth();

    int getSteps();

    Cache.HitStat getResult(int step);
}
