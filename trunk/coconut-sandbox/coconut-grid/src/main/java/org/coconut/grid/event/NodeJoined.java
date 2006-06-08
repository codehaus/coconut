package org.coconut.grid.event;

import org.coconut.grid.Cluster;
import org.coconut.grid.Node;

public interface NodeJoined {
    //Timestampt
    //Sequenceid
    
    Node getNode();
    Cluster getCluster();
}
