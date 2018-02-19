package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;

public interface Validator {

    String getName();

    ValidationResult validate(JSONObject value);
}