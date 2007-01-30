package org.coconut.grid;

import java.util.Map;
import java.util.UUID;

/**
 * Clusters are nodes with equally configured software.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface Cluster {
    /**
     * Returns the unique id of the cluster.
     */
    UUID getClusterID();

    Grid getGrid();

    Map<UUID, Node> getNodes();
}
