package com.gederin.service;

import com.gederin.model.ClusterInformation;
import com.gederin.repository.ClusterInformationRepository;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClusterInformationService {

    private final ClusterInformationRepository clusterInformationRepository;

    public void rebuildAllNodesList(List<String> allNodes) {
        clusterInformationRepository.getAllClusterNodes().clear();
        clusterInformationRepository.getAllClusterNodes().addAll(allNodes);
    }

    public void rebuildLiveNodesList(List<String> liveNodes) {
        clusterInformationRepository.getLiveClusterNodes().clear();
        clusterInformationRepository.getLiveClusterNodes().addAll(liveNodes);
    }

    public List<String> getLiveClusterNodes() {
        return Collections.unmodifiableList(clusterInformationRepository.getLiveClusterNodes());
    }

    public List<String> getWorkerClusterNodes() {
        return Collections.unmodifiableList(clusterInformationRepository.getLiveClusterNodes()
                .stream()
                .filter(node -> !node.equals(getMasterNode()))
                .collect(Collectors.toList()));
    }

    public void setMasterNode(String node) {
        clusterInformationRepository.setMasterNode(node);
    }

    public ClusterInformation getClusterInformation() {
        return new ClusterInformation(getMasterNode(),
                clusterInformationRepository.getAllClusterNodes(),
                getLiveClusterNodes(),
                getWorkerClusterNodes());
    }

    public String getMasterNode() {
        return clusterInformationRepository.getMasterNode();
    }
}
