package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;

import java.util.Collections;

public class DummyValidator implements Validator {

    private final String name;

    public DummyValidator(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValidationResult validate(JSONObject value) {
        return new ValidationResult(true, Collections.emptyList());
    }
}