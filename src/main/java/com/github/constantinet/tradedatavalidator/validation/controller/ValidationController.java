package com.github.constantinet.tradedatavalidator.validation.controller;

import com.github.constantinet.tradedatavalidator.validation.IndexedFailure;
import com.github.constantinet.tradedatavalidator.validation.service.ValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
@Api(description = "Provides functionality to validate trade data")
public class ValidationController {

    private final ValidationService validationService;

    @Autowired
    public ValidationController(final ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/trades/validation")
    @ApiOperation(value = "Trade Validation", notes = "Validates array of JSON objects")
    public List<IndexedFailure> validate(@RequestBody final List<JSONObject> trades) {
        return validationService.validate(trades).stream()
                .sorted(Comparator.comparingInt(IndexedFailure::getIndex))
                .collect(Collectors.toList());
    }
}