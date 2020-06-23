package com.gederin;

import com.gederin.model.DocumentData;
import com.gederin.search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

public class Application {

    private static final String BOOKS_DIRECTORY = "./resources/books";

    private static final String SEARCH_1 = "The best detective that catches many criminals using his deductive skills";
    private static final String SEARCH_2 = "The girl that fails through a rabbit hole into a fantasy wonderland";
    private static final String SEARCH_3 = "A war between Russia and France";

    public static void main(String[] args) throws FileNotFoundException {
        File documentsDirectory = new File(BOOKS_DIRECTORY);

        List<String> documents = Arrays.stream(Objects.requireNonNull(documentsDirectory.list()))
                .map(documentName -> BOOKS_DIRECTORY + "/" + documentName)
                .collect(Collectors.toList());
        
        List<String> terms = TFIDF.getWordsFromLine(SEARCH_3);
        
        findMostRelevantDocuments (documents, terms);
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms) throws FileNotFoundException {
        Map<String, DocumentData> documentResults = new HashMap<>();

        for (String document : documents){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));

            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = TFIDF.getWordsFromDocument(lines);
            
            DocumentData documentData = TFIDF.createDocumentData(words, terms);
            documentResults.put(document, documentData);
        }
        
        Map<Double, List<String>> documentByScore = TFIDF.getDocumentsScores(terms, documentResults);
        printResults(documentByScore);
    }

    private static void printResults(Map<Double, List<String>> documentByScore) {
        for (Entry<Double, List<String>> pair: documentByScore.entrySet()){
            double score = pair.getKey();
            for (String document : pair.getValue()){
                System.out.println(String.format("Book: %s - score: %f", document.split("/")[3], score));
            }
        }
    }
}
