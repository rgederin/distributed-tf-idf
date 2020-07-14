package com.gederin.service.v2;

import com.gederin.model.request.TermsFrequencyRequest;
import com.gederin.model.response.TermsFrequencyResponse;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncRequestSenderService {

    private final RestTemplate restTemplate;

    @Async
    public CompletableFuture<TermsFrequencyResponse> calculateTermsFrequency(List<String> terms, String workerNode, List<String> workerDocuments) {
        String url = "http://" + workerNode + "/v2/termsfrequency/";

        log.info("sending {} documents to {} worker node", workerDocuments, workerNode);

        TermsFrequencyRequest request = new TermsFrequencyRequest(terms, workerDocuments);
        TermsFrequencyResponse response = restTemplate.postForObject(url, request, TermsFrequencyResponse.class);

        return CompletableFuture.completedFuture(response);
    }
}
