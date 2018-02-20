package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class ValueDateForCurrencyValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ValueDateForCurrencyValidator.class);

    private final String name;
    private final RestTemplate restTemplate;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public ValueDateForCurrencyValidator(final String name,
                                         final RestTemplate restTemplate,
                                         final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = null;
        this.restTemplate = null;
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