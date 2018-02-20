package com.github.constantinet.tradedatavalidator.formatvalidator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import org.everit.json.schema.FormatValidator;

import java.util.Optional;

public class CurrencyPairFormatValidator implements FormatValidator {

    private final String name;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public CurrencyPairFormatValidator(final String name,
                                       final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = null;
        this.messageConstructionStrategy = null;
    }

    @Override
    public Optional<String> validate(final String string) {
        return null;
    }

    @Override
    public String formatName() {
        return null;
    }
}