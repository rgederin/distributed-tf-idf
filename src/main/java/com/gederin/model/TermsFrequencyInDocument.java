
package com.gederin.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class TermsFrequencyInDocument implements Serializable {
    private Map<String, Double> termToFrequency = new HashMap<>();

    public void putTermFrequency(String term, double frequency) {
        termToFrequency.put(term, frequency);
    }

    public double getFrequency(String term) {
        return termToFrequency.get(term);
    }
}
