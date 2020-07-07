package com.gederin.service;

import com.gederin.model.SearchQuery;
import com.gederin.tfidf.TFIDF;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchDocumentsService {
    private final TFIDF tfidf;

    public void searchDocuments (SearchQuery searchQuery){
        List<String> terms = tfidf.getWordsFromLine(searchQuery.getSearchQuery());

    }
}
