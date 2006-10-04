package org.coconut.grid;

import java.util.Map;
import java.util.UUID;

public interface Grid {
    
    UUID getUUID();
    /**
     * Returns a Map containing all the clusters that are part of this grid.
     */
    Map<UUID, Cluster> getClusters();
}
