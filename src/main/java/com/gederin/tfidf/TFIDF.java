package com.gederin.tfidf;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TFIDF {

    public List<String> getWordsFromLine(String line) {
        return Arrays.asList(line.split("(\\.)+|(,)+|( )+|(-)+|(\\?)+|(!)+|(;)+|(:)+|(/d)+|(/n)+"));
    }
}
