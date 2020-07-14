package com.gederin.tfidf;

import com.gederin.model.TermsFrequencyInDocument;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@Component
public class TFIDF {

    /**
     * Calculate frequency of given term in the single document
     *
     * @param words - words in the document
     * @param term  - single term for which we need to calcultate frequency
     * @return - frequency of the term in the document (number of term appearance divided to number
     * of words in document)
     */
    public double calculateTermFrequency(List<String> words, String term) {
        long count = words.stream()
                .filter(word -> word.equalsIgnoreCase(term))
                .count();

        return (double) count / words.size();
    }

    /**
     * Calculate frequency of all terms in the single document
     *
     * @param words - words in the document
     * @param terms - all search terms for which we need to calculate frequency
     * @return - TermsFrequencyInDocument object with terms freqeuncy calculation
     */
    public TermsFrequencyInDocument calculateTermsFrequencyInDocument(List<String> words, List<String> terms) {
        TermsFrequencyInDocument documentData = new TermsFrequencyInDocument();

        terms.forEach(term -> {
            double termFrequency = calculateTermFrequency(words, term.toLowerCase());
            documentData.putTermFrequency(term, termFrequency);
        });

        return documentData;
    }


    public double calculateTermInverseDocumentFrequency(String term, Map<String, TermsFrequencyInDocument> termsFrequencyInDocumentMap) {
        double numberOfDocumentsWithTerm = 0;

        for (TermsFrequencyInDocument documentData : termsFrequencyInDocumentMap.values()) {
            double termFrequency = documentData.getFrequency(term);

            if (termFrequency > 0.0) {
                numberOfDocumentsWithTerm++;
            }
        }
        return numberOfDocumentsWithTerm == 0 ? 0 : Math.log10(termsFrequencyInDocumentMap.size() / numberOfDocumentsWithTerm);
    }

    public Map<String, Double> buildTermToInverseDocumentFrequencyMap(List<String> terms,
                                                                       Map<String, TermsFrequencyInDocument> documentResults) {
        Map<String, Double> termToIDF = new HashMap<>();
        for (String term : terms) {
            double idf = calculateTermInverseDocumentFrequency(term, documentResults);
            termToIDF.put(term, idf);
        }
        return termToIDF;
    }


    public Map<Double, List<String>> getDocumentsScores(List<String> terms,
                                                        Map<String, TermsFrequencyInDocument> termsFrequencyInDocumentMap) {
        TreeMap<Double, List<String>> scoreToDoc = new TreeMap<>();
        Map<String, Double> termToInverseDocumentFrequency = buildTermToInverseDocumentFrequencyMap(terms, termsFrequencyInDocumentMap);

        for (Entry<String, TermsFrequencyInDocument> entry : termsFrequencyInDocumentMap.entrySet()) {
            TermsFrequencyInDocument termsFrequencyInDocument = entry.getValue();

            double score = calculateDocumentScore(terms, termsFrequencyInDocument, termToInverseDocumentFrequency);

            addDocumentScoreToTreeMap(scoreToDoc, score, entry.getKey());
        }
        return scoreToDoc.descendingMap();
    }

    public void addDocumentScoreToTreeMap(TreeMap<Double, List<String>> scoreToDoc, double score, String document) {
        List<String> booksWithCurrentScore = scoreToDoc.get(score);
        if (null == booksWithCurrentScore) {
            booksWithCurrentScore = new ArrayList<>();
        }
        booksWithCurrentScore.add(document);
        scoreToDoc.put(score, booksWithCurrentScore);
    }

    public double calculateDocumentScore(List<String> terms,
                                          TermsFrequencyInDocument documentData,
                                          Map<String, Double> termToInverseDocumentFrequency) {
        double score = 0;

        for (String term : terms) {
            double termFrequency = documentData.getFrequency(term);
            double inverseTermFrequency = termToInverseDocumentFrequency.get(term);
            score += termFrequency * inverseTermFrequency;
        }

        return score;
    }


    public List<String> getWordsFromDocument(List<String> lines) {
        List<String> words = new ArrayList<>();
        for (String line : lines) {
            words.addAll(getWordsFromLine(line));
        }
        return words;
    }

    public List<String> getWordsFromLine(String line) {
        return Arrays.asList(line.split("(\\.)+|(,)+|( )+|(-)+|(\\?)+|(!)+|(;)+|(:)+|(/d)+|(/n)+"));
    }
}
