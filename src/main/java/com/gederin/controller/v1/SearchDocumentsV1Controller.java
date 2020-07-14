package com.gederin.controller.v1;

import com.gederin.model.response.SearchDocumentsResponse;
import com.gederin.model.request.SearchDocumentsRequest;
import com.gederin.service.v1.SearchDocumentsV1Service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v1/search")
@RequiredArgsConstructor
@Slf4j
public class SearchDocumentsV1Controller {

    private final SearchDocumentsV1Service searchDocumentsService;

    @PostMapping("/")
    public SearchDocumentsResponse searchDocuments(@RequestBody SearchDocumentsRequest searchDocumentsRequest) throws FileNotFoundException {
        long before = System.currentTimeMillis();
        SearchDocumentsResponse searchDocumentsResponse = searchDocumentsService.searchDocuments(searchDocumentsRequest);
        long after = System.currentTimeMillis();

        searchDocumentsResponse.setMillis(after - before);

        return searchDocumentsResponse;
    }
}
