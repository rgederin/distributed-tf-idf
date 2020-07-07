package com.gederin.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClusterInformation {
    /**
     * Name of the master node in the cluster
     */
    private String masterNode;

    /**
     * List with all persistent Apache Zookeeper znodes in the cluster (both live and terminated)
     */
    private final List<String> allClusterNodes;

    /**
     * List with  live ephemeral Apache Zookeeper znodes in the cluster
     */
    private final List<String> liveClusterNodes;

    private final List<String> workerClusterNodes;
}
