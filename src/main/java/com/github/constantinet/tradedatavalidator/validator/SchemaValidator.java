package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.Schema;
import org.json.JSONObject;

import java.util.Collection;

public class SchemaValidator implements Validator {

    private final String name;
    private final Schema schema;

    public SchemaValidator(final String name,
                           final JSONObject schema,
                           final Collection<FormatValidator> formatValidators) {
        this.name = null;
        this.schema = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValidationResult validate(final JSONObject object) {
        return null;
    }
}