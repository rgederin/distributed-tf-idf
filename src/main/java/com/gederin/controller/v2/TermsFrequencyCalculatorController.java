package com.gederin.controller.v2;

import com.gederin.model.request.TermsFrequencyRequest;
import com.gederin.model.response.TermsFrequencyResponse;
import com.gederin.service.v2.TermsFrequencyCalculatorService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("v2/termsfrequency")
@RequiredArgsConstructor
@Slf4j
public class TermsFrequencyCalculatorController {

    private final TermsFrequencyCalculatorService termsFrequencyCalculatorService;

    @PostMapping("/")
    public TermsFrequencyResponse calculateTermsFrequency(@RequestBody TermsFrequencyRequest request) throws FileNotFoundException {
        return new TermsFrequencyResponse(termsFrequencyCalculatorService.
                calculateTermsFrequency(request.getTerms(), request.getDocuments()));
    }
}
