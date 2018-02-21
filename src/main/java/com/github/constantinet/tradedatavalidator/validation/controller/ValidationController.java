package com.github.constantinet.tradedatavalidator.validation.controller;

import com.github.constantinet.tradedatavalidator.validation.IndexedFailure;
import com.github.constantinet.tradedatavalidator.validation.service.ValidationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController(value = "/trades/validation")
public class ValidationController {

    private final ValidationService validationService;

    @Autowired
    public ValidationController(final ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping
    public List<IndexedFailure> validate(@RequestBody final List<JSONObject> trades) {
        return validationService.validate(trades).stream()
                .sorted(Comparator.comparingInt(IndexedFailure::getIndex))
                .collect(Collectors.toList());
    }
}