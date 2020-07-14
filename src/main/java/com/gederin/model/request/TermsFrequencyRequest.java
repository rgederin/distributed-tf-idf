package com.gederin.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TermsFrequencyRequest {
    private final List<String> terms;
    private final List<String> documents;
}
