package com.gederin.controller;

import com.gederin.model.ClusterInformation;
import com.gederin.service.ClusterInformationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("cluster/info")
@RequiredArgsConstructor
@Slf4j
public class ClusterInformationController {
    private final ClusterInformationService clusterInformationService;

    @GetMapping("/")
    public ClusterInformation getClusterInformation() {
        log.info("get cluster information...");
        return clusterInformationService.getClusterInformation();
    }
}
