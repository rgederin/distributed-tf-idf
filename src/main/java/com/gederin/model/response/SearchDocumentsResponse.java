package com.gederin.model.response;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SearchDocumentsResponse {
    private long millis;
    private final Map<Double, List<String>> documentsResult;
}
