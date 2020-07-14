package com.gederin.service.v2;

import com.gederin.model.TermsFrequencyInDocument;
import com.gederin.tfidf.TFIDF;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermsFrequencyCalculatorService {

    private final TFIDF tfidf;

    public Map<String, TermsFrequencyInDocument> calculateTermsFrequency (List<String> terms, List<String> documents) throws FileNotFoundException {
        Map<String, TermsFrequencyInDocument> termsFrequencyPerDocument = new HashMap<>();

        for (String document : documents){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));

            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = tfidf.getWordsFromDocument(lines);

            TermsFrequencyInDocument documentData = tfidf.calculateTermsFrequencyInDocument(words, terms);

            String[] split = document.split("/");
            termsFrequencyPerDocument.put(split[split.length -1], documentData);
        }

        return termsFrequencyPerDocument;
    }
}
