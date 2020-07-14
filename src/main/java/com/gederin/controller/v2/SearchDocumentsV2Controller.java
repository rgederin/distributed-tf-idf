package com.gederin.controller.v2;

import com.gederin.model.response.SearchDocumentsResponse;
import com.gederin.model.request.SearchDocumentsRequest;
import com.gederin.service.v2.SearchDocumentsV2Service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v2/search")
@RequiredArgsConstructor
@Slf4j
public class SearchDocumentsV2Controller {

    private final SearchDocumentsV2Service searchDocumentsService;

    @PostMapping("/")
    public SearchDocumentsResponse searchDocuments(@RequestBody SearchDocumentsRequest searchDocumentsRequest) throws FileNotFoundException, ExecutionException, InterruptedException {
        long before = System.currentTimeMillis();
        SearchDocumentsResponse searchDocumentsResponse = new SearchDocumentsResponse(searchDocumentsService.searchDocuments(searchDocumentsRequest));
        long after = System.currentTimeMillis();

        searchDocumentsResponse.setMillis(after - before);

        return searchDocumentsResponse;
    }
}
