package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueDateNotBeforeTradeDateValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ValueDateNotBeforeTradeDateValidator.class);

    private final String name;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public ValueDateNotBeforeTradeDateValidator(final String name,
                                                final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = null;
        this.messageConstructionStrategy = null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ValidationResult validate(final JSONObject object) {
        return null;
    }
}