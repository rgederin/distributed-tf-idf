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
        int workersNumber = workers.size();

        log.info("number of workers node in the cluster: {}", workersNumber);

        List<String> terms = tfidf.getWordsFromLine(searchDocumentsRequest.getSearchQuery());
        List<String> documents = buildDocumentsList();

        Map<String, List<String>> documentsPerWorker = splitDocumentsPerWorker(documents, workers, workersNumber);

        log.info("documents for analysis per worker: {}", documentsPerWorker);


        List<CompletableFuture<TermsFrequencyResponse>> termsFrequency = documentsPerWorker.entrySet().stream()
                .map(entry -> requestSenderService.calculateTermsFrequency(terms, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        CompletableFuture.allOf(termsFrequency.stream().toArray(CompletableFuture[]::new));



        Map<String, TermsFrequencyInDocument> documentResults = new HashMap<>();


        for (CompletableFuture<TermsFrequencyResponse> completableFuture : termsFrequency){
            TermsFrequencyResponse response= completableFuture.get();

            documentResults.putAll(response.getTermsFrequencyPerDocument());
        }

        System.out.println(documentResults);

         return tfidf.getDocumentsScores(terms, documentResults);
    }




    private List<String> getWorkerNodes() {
        return clusterInformationService.getWorkerClusterNodes();
    }

    private List<String> buildDocumentsList() throws FileNotFoundException {
        File documentsDirectory = ResourceUtils.getFile("classpath:documents");

        return Arrays.stream(Objects.requireNonNull(documentsDirectory.list()))
                .map(documentName -> documentsDirectory.getPath() + "/" + documentName)
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> splitDocumentsPerWorker(List<String> documents, List<String> workers, int numberOfWorkers) {
        int numberOfDocumentsPerWorker = (documents.size() + numberOfWorkers - 1) / numberOfWorkers;
        Map<String, List<String>> workerDocuments = new HashMap<>();

        for (int i = 0; i < numberOfWorkers; i++) {
            int firstDocumentIndex = i * numberOfDocumentsPerWorker;
            int lastDocumentIndexExclusive = Math.min(firstDocumentIndex + numberOfDocumentsPerWorker, documents.size());

            if (firstDocumentIndex >= lastDocumentIndexExclusive) {
                break;
            }
            List<String> currentWorkerDocuments = new ArrayList<>(documents.subList(firstDocumentIndex, lastDocumentIndexExclusive));
            workerDocuments.put (workers.get(i), currentWorkerDocuments);
        }

        return workerDocuments;
    }
}
