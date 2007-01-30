package org.coconut.grid;

import java.util.UUID;

public interface Node {
    UUID getNodeID();

    enum State {
        Unavailable, Started, Stopped, Destroyed
    }

    void addProbe(Probe probe);

    void removeProbe(Probe probe);

    Cluster getCluster();
}
