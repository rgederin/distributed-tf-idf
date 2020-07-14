package com.gederin.service.v1;

import com.gederin.model.response.SearchDocumentsResponse;
import com.gederin.model.request.SearchDocumentsRequest;
import com.gederin.model.TermsFrequencyInDocument;
import com.gederin.tfidf.TFIDF;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchDocumentsV1Service {

    private final TFIDF tfidf;

    public SearchDocumentsResponse searchDocuments(SearchDocumentsRequest searchDocumentsRequest) throws FileNotFoundException {
        List<String> terms = tfidf.getWordsFromLine(searchDocumentsRequest.getSearchQuery());

        File documentsDirectory = ResourceUtils.getFile("classpath:documents");

        List<String> documents = Arrays.stream(Objects.requireNonNull(documentsDirectory.list()))
                .map(documentName -> documentsDirectory.getPath() + "/" + documentName)
                .collect(Collectors.toList());

        return new SearchDocumentsResponse(findMostRelevantDocuments(documents, terms));
    }

    private Map<Double, List<String>> findMostRelevantDocuments(List<String> documents, List<String> terms) throws FileNotFoundException {
        Map<String, TermsFrequencyInDocument> documentResults = new HashMap<>();

        for (String document : documents){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));

            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = tfidf.getWordsFromDocument(lines);

            TermsFrequencyInDocument documentData = tfidf.calculateTermsFrequencyInDocument(words, terms);
            documentResults.put(document.split("/")[document.split("/").length-1], documentData);
        }

        return tfidf.getDocumentsScores(terms, documentResults);
    }
}

