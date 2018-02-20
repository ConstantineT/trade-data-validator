package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class SchemaValidator implements Validator {

    private final String name;
    private final Schema schema;

    public SchemaValidator(final String name,
                           final JSONObject schema,
                           final Collection<FormatValidator> formatValidators) {
        this.name = name;

        final SchemaLoader.SchemaLoaderBuilder builder = SchemaLoader.builder()
                .schemaJson(schema)
                .draftV6Support();
        formatValidators.forEach(builder::addFormatValidator);
        this.schema = builder.build().load().build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValidationResult validate(final JSONObject object) {
        Objects.requireNonNull(object, "object can not be null");

        try {
            schema.validate(object);
        } catch (final ValidationException ex) {
            return new ValidationResult(false, ex.getAllMessages());
        }

        return new ValidationResult(true, Collections.emptyList());
    }
}