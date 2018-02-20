package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class ValueDateAgainstProductTypeValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ValueDateAgainstProductTypeValidator.class);

    private final String name;
    private final Clock clock;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public ValueDateAgainstProductTypeValidator(final String name,
                                                final Clock clock,
                                                final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = null;
        this.clock = null;
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