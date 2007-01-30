package org.coconut.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Grids {

    /**
     * Returns a map containing all the nodes defined in any cluster in a grid.
     * 
     * @param g
     *            the grid to return the nodes of
     * @return
     */
    public static Map<UUID, Node> getNodes(Grid... g) {
        HashMap<UUID, Node> map = new HashMap<UUID, Node>();
        for (Grid grid : g) {
            for (Cluster c : grid.getClusters().values()) {
                for (Map.Entry<UUID, Node> entry : c.getNodes().entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return map;
    }
}
