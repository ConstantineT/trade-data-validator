package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateBeforeDeliveryDateInOptionsValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(DateBeforeDeliveryDateInOptionsValidator.class);

    private final String name;
    private final String propertyNameToValidate;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public DateBeforeDeliveryDateInOptionsValidator(final String name,
                                                    final String propertyNameToValidate,
                                                    final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = null;
        this.propertyNameToValidate = null;
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