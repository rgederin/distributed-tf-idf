package com.gederin.controller;

import com.gederin.model.SearchQuery;
import com.gederin.tfidf.TFIDF;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v1/search")
@RequiredArgsConstructor
@Slf4j
public class SearchDocumentsController {

    private final TFIDF tfidf;

    @PostMapping("/")
    public List<String> searchDocuments(@RequestBody SearchQuery searchQuery) {
        List<String> terms = tfidf.getWordsFromLine(searchQuery.getSearchQuery());
        return terms;
    }
}
