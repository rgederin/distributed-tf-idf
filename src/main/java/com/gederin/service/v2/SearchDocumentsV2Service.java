package com.gederin.service.v2;

import com.gederin.model.TermsFrequencyInDocument;
import com.gederin.model.request.SearchDocumentsRequest;
import com.gederin.model.response.TermsFrequencyResponse;
import com.gederin.service.ClusterInformationService;
import com.gederin.tfidf.TFIDF;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchDocumentsV2Service {

    private final ClusterInformationService clusterInformationService;
    private final AsyncRequestSenderService requestSenderService;

    private final TFIDF tfidf;

    public Map<Double, List<String>> searchDocuments(SearchDocumentsRequest searchDocumentsRequest) throws FileNotFoundException, ExecutionException, InterruptedException {
        log.info("master node starts documents search ...");

        List<String> workers = getWorkerNodes();
        log.info("number of workers node in the cluster: {}", workers.size());

        List<String> terms = tfidf.getWordsFromLine(searchDocumentsRequest.getSearchQuery());

        Map<String, List<String>> documentsPerWorker = splitDocumentsPerWorker(createDocumentsList(), workers);
        log.info("documents for analysis per worker: {}", documentsPerWorker);

        CompletableFuture[] termsFrequency = documentsPerWorker.entrySet()
                .stream()
                .map(entry -> requestSenderService.calculateTermsFrequency(terms, entry.getKey(), entry.getValue()))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(termsFrequency);
        Map<String, TermsFrequencyInDocument> termsFrequencyResult = mergeTermsFrequency(termsFrequency);

        return tfidf.getDocumentsScores(terms, termsFrequencyResult);
    }

    private Map<String, TermsFrequencyInDocument> mergeTermsFrequency(CompletableFuture[] termsFrequency) throws ExecutionException, InterruptedException {
        Map<String, TermsFrequencyInDocument> termsFrequencyResult = new HashMap<>();

        for (CompletableFuture<TermsFrequencyResponse> completableFuture : termsFrequency) {
            TermsFrequencyResponse response = completableFuture.get();
            termsFrequencyResult.putAll(response.getTermsFrequencyPerDocument());
        }

        return termsFrequencyResult;
    }

    private List<String> getWorkerNodes() {
        return clusterInformationService.getWorkerClusterNodes();
    }

    private List<String> createDocumentsList() throws FileNotFoundException {
        File documentsDirectory = ResourceUtils.getFile("classpath:documents");

        return Arrays.stream(Objects.requireNonNull(documentsDirectory.list()))
                .map(documentName -> documentsDirectory.getPath() + "/" + documentName)
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> splitDocumentsPerWorker(List<String> documents, List<String> workers) {
        int numberOfWorkers = workers.size();
        int numberOfDocumentsPerWorker = (documents.size() + numberOfWorkers - 1) / numberOfWorkers;

        Map<String, List<String>> workerDocuments = new HashMap<>();

        for (int i = 0; i < numberOfWorkers; i++) {
            int firstDocumentIndex = i * numberOfDocumentsPerWorker;
            int lastDocumentIndexExclusive = Math.min(firstDocumentIndex + numberOfDocumentsPerWorker, documents.size());

            if (firstDocumentIndex >= lastDocumentIndexExclusive) {
                break;
            }
            List<String> currentWorkerDocuments = new ArrayList<>(documents.subList(firstDocumentIndex, lastDocumentIndexExclusive));
            workerDocuments.put(workers.get(i), currentWorkerDocuments);
        }

        return workerDocuments;
    }
}
